package q6.app.infra.auth

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.template.renderToResponse
import q6.app.Q6Core
import q6.core.users.api.Role
import q6.infra.http4k.TemplatesModule
import q6.platform.http4k.StaticPage
import q6.platform.http4k.location

class Q6AuthModule(
    templatesModule: TemplatesModule,
    q6Core: Q6Core
) {

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


    val filters = listOf(
        CookieAuthenticator(q6Core.auth.authService),
        authorizer
    )

}