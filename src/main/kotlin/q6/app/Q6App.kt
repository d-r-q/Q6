package q6.app

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.ServerConfig
import org.http4k.server.Undertow
import org.http4k.template.renderToResponse
import org.slf4j.LoggerFactory
import q6.app.anonymous.AnonymousAppModule
import q6.app.main.Q6AppModule
import q6.app.platform.*
import q6.core.accounts.AccountsModule
import q6.core.auth.AuthModule
import q6.core.users.UsersModule
import q6.core.users.api.Role
import q6.platform.conf.AppConfig
import q6.platform.db.DbModule
import q6.platform.web.TemplatesModule
import q6.platform.web.WebModule
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import kotlin.time.Duration.Companion.milliseconds


class Q6App(
    config: Map<String, String> = emptyMap(),
    createServer: (Int) -> ServerConfig = { Undertow(it) }
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val appConfig = AppConfig(config)
    val dbModule = DbModule(appConfig)

    val accounts = AccountsModule(dbModule)
    val users = UsersModule(dbModule)
    val auth = AuthModule(users)

    val templatesModule = TemplatesModule(appConfig)

    val anonymousAppModule = AnonymousAppModule(templatesModule, users, auth)
    val q6AppModule = Q6AppModule(templatesModule)

    val authRules = PathPatternAuthorizationRules(
        "/app/.*" to setOf(Role.ROLE_USER),
        "/.*" to emptySet()
    )

    val authorizer = UserIdentityAuthorizer(
        authRules,
        {
            templatesModule.renderer.renderToResponse(
                StaticPage("q6/app/anonymous/ForbiddenPage.html"),
                Status.FORBIDDEN
            )
        },
        { Response(Status.FOUND).location("/login") }
    )

    val web = WebModule(
        appConfig,
        createServer,
        listOf(
            CookieAuthenticator(auth.authService),
            authorizer
        ),
        anonymousAppModule.routes + q6AppModule.routes
    )

    fun init(): Q6App {
        log.info("Starting Q6")
        dbModule.init()
        log.info("Q6 is initialized")
        Runtime.getRuntime().addShutdownHook(object : Thread() {

            override fun run() {
                web.server.stop()
                dbModule.stop()
                log.info("Q6 is stopped")
            }
        })
        return this
    }

    fun startServer() {
        web.server.start()

        val bean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean()
        val startTime: Long = bean.startTime
        log.info("Q6 server started (startup time: ${(System.currentTimeMillis() - startTime)}ms)")
    }

}

fun main() {
    Q6App()
        .init()
        .startServer()
}