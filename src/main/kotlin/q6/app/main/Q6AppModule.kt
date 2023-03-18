package q6.app.main

import q6.platform.web.TemplatesModule
import q6.platform.web.WebSubModule


class Q6AppModule(
    templatesModule: TemplatesModule,
) : WebSubModule {

    private val pages = listOf(
        MainPageController(templatesModule.renderer),
    )

    override val routes = pages.map { it.routes }

}