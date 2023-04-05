package q6.app.main

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.TemplateRenderer
import q6.platform.http4k.SameFileNameViewModel
import q6.platform.web.PageController


class MainPageController(
    renderer: TemplateRenderer
) : PageController {

    override val routes = routes(
        "/app/main" bind Method.GET to { req ->
            Response(Status.OK).body(renderer(MainPage()))
        },
    )

}

class MainPage : SameFileNameViewModel