package q6.platform.web

import org.http4k.routing.RoutingHttpHandler


interface WebSubModule {

    val routes: List<RoutingHttpHandler>

}