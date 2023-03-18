package q6.app.main

import q6.platform.web.TemplatesModule


class Q6AppModule(
    templatesModule: TemplatesModule,
) {

    private val pages = listOf(
        MainPageController(templatesModule.renderer),
    )

    val routes = pages.map { it.routes }

}