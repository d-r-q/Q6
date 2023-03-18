package q6.app.anonymous

import q6.core.auth.AuthModule
import q6.core.users.UsersModule
import q6.platform.web.TemplatesModule
import q6.platform.web.WebSubModule


class AnonymousAppModule(
    templatesModule: TemplatesModule,
    usersModule: UsersModule,
    authModule: AuthModule
) : WebSubModule {

    private val pages = listOf(
        LoginPageController(templatesModule.renderer, authModule.authService),
        RegistrationPageController(templatesModule.renderer, usersModule.usersService)
    )

    override val routes = pages.map { it.routes }

}
