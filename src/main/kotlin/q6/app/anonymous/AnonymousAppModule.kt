package q6.app.anonymous

import org.http4k.template.TemplateRenderer
import q6.core.auth.AuthModule
import q6.core.users.UsersModule
import q6.platform.web.TemplatesModule


class AnonymousAppModule(
    templatesModule: TemplatesModule,
    usersModule: UsersModule,
    authModule: AuthModule
) {

    private val pages = listOf(
        LoginPageController(templatesModule.renderer, authModule.authService),
        RegistrationPageController(templatesModule.renderer, usersModule.usersService)
    )

    val routes = pages.map { it.routes }
}
