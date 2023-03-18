package q6.platform.web

import org.http4k.core.Filter
import org.http4k.core.then
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.ServerConfig
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.ThymeleafTemplates
import q6.platform.conf.AppConfig


class WebModule(
    appConfig: AppConfig,
    createServer: (Int) -> ServerConfig = { Undertow(it) },
    filters: List<Filter>,
    appRoutes: List<RoutingHttpHandler>,
) {

    val app = filters.reduce { l, r -> l.then(r) }
        .then(routes(* appRoutes.toTypedArray()))

    val server = app
        .asServer(createServer(appConfig["q6.web.port"]))

}
