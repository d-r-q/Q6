package q6.app

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.routing.routes
import org.http4k.server.ServerConfig
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.renderToResponse
import org.slf4j.LoggerFactory
import q6.app.anonymous.AnonymousAppModule
import q6.app.main.Q6AppModule
import q6.app.platform.*
import q6.core.users.api.Role
import q6.platform.web.TemplatesModule
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean


class Q6AppHttp4kPort(
    private val q6Core: Q6Core,
    private val createServer: (Int) -> ServerConfig = { Undertow(it) },
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val templatesModule = TemplatesModule(q6Core.appConfig)

    private val anonymousAppModule = AnonymousAppModule(templatesModule, q6Core.users, q6Core.auth)
    private val q6AppModule = Q6AppModule(templatesModule)

    private val authRules = PathPatternAuthorizationRules(
        "/app/.*" to setOf(Role.ROLE_USER),
        "/.*" to emptySet()
    )

    private val authorizer = UserIdentityAuthorizer(
        authRules,
        {
            templatesModule.renderer.renderToResponse(
                StaticPage("q6/app/anonymous/ForbiddenPage.html"),
                Status.FORBIDDEN
            )
        },
        { Response(Status.FOUND).location("/login") }
    )


    private val filters = listOf(
        CookieAuthenticator(q6Core.auth.authService),
        authorizer
    )

    private val webAppSubmodules = listOf(anonymousAppModule, q6AppModule)

    val app = filters.reduce { l, r -> l.then(r) }
        .then(routes(*(webAppSubmodules.flatMap { it.routes }).toTypedArray()))

    fun startServer() {
        val server = app
            .asServer(createServer(q6Core.appConfig["q6.web.port"]))
            .start()

        Runtime.getRuntime().addShutdownHook(object : Thread() {

            override fun run() {
                server.stop()
                log.info("Q6 server is stopped")
            }
        })

        val bean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean()
        val startTime: Long = bean.startTime
        log.info("Q6 server started (startup time: ${(System.currentTimeMillis() - startTime)}ms)")
    }

}

fun main() {
    val q6Core = Q6Core()
        .init()
    val q6Http4kPort = Q6AppHttp4kPort(q6Core)
    q6Http4kPort.startServer()
}