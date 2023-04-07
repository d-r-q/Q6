package q6.app

import org.http4k.core.then
import org.http4k.routing.routes
import org.http4k.server.ServerConfig
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import q6.app.anonymous.AnonymousAppModule
import q6.app.infra.auth.Q6AuthModule
import q6.app.main.Q6AppModule
import q6.infra.http4k.TemplatesModule
import q6.platform.conf.AppConfig
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

    private val webAppSubmodules = listOf(anonymousAppModule, q6AppModule)

    private val authModule = Q6AuthModule(templatesModule, q6Core.auth.authService::exchange)

    val app = authModule.filters.reduce { l, r -> l.then(r) }
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
    val appConfig = AppConfig(emptyMap())
    val q6Infra = Q6Infra(appConfig)
        .init()
    val q6Core = Q6Core(appConfig, q6Infra)
    val q6Http4kPort = Q6AppHttp4kPort(q6Core)
    q6Http4kPort.startServer()
}