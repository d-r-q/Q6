package q6.platform.web

import org.http4k.routing.RoutingHttpHandler


interface PageController {

    val routes: RoutingHttpHandler

}