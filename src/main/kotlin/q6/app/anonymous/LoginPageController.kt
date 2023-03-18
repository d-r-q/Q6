package q6.app.anonymous

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.webForm
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.TemplateRenderer
import q6.app.platform.AUTH_TOKEN_COOKIE
import q6.app.platform.CookieAuthenticator
import q6.app.platform.Q6ViewModel
import q6.app.platform.location
import q6.core.auth.api.AuthService
import q6.core.auth.api.LoginRequest
import q6.platform.web.PageController
import kotlin.time.Duration.Companion.minutes

class LoginPageController(
    private val renderer: TemplateRenderer,
    private val authService: AuthService
) : PageController {

    override val routes = routes(
        "/login" bind Method.GET to {
            Response(OK).body(renderer(LoginPage))
        },
        "/login" bind Method.POST to { req ->
            val email = FormField.required("email")
            val password = FormField.required("password")
            val loginForm = Body.webForm(Validator.Strict, email, password).toLens().extract(req)
            val loginRequest = LoginRequest(email(loginForm), password(loginForm))
            val authResult = authService.authenticate(loginRequest)
            if (authResult.isSuccess) {
                Response(FOUND)
                    .location("/app/main")
                    .cookie(createSecureCookie(authResult))
            } else {
                Response(OK).body(renderer(LoginPage))
            }
        }
    )

    private fun createSecureCookie(authResult: Result<String>) = Cookie(
        AUTH_TOKEN_COOKIE,
        authResult.getOrThrow(),
        maxAge = 1.minutes.inWholeMilliseconds,
        secure = true,
        httpOnly = true,
        sameSite = SameSite.Strict,
    )
}


object LoginPage : Q6ViewModel