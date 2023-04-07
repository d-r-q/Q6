package q6.app.anonymous

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.webForm
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.TemplateRenderer
import q6.core.users.api.DuplicatedEmail
import q6.core.users.api.RegisterUserRequest
import q6.core.users.api.UsersService
import q6.platform.http4k.SameFileNameViewModel
import q6.platform.kotlin.throwIt
import q6.platform.web.PageController


class RegistrationPageController(
    private val renderer: TemplateRenderer,
    private val usersService: UsersService
) : PageController {

    override val routes = routes(
        "/register" bind Method.GET to {
            Response(OK).body(renderer(RegistrationPage()))
        },
        "/register" bind Method.POST to { req ->
            val email = FormField.required("email")
            val password = FormField.required("password")
            val name = FormField.required("name")
            val registerFrom = Body.webForm(Validator.Strict, email, password, name).toLens().extract(req)
            val registerUserRequest =
                RegisterUserRequest(email(registerFrom), password(registerFrom), name(registerFrom))

            registerUser(registerUserRequest)
        },
        "/successful-registration" bind Method.GET to {
            Response(OK).body(renderer(SuccessfulRegistrationPage))
        }
    )

    private fun registerUser(registerUserRequest: RegisterUserRequest): Response {
        val res = Result.runCatching { usersService.registerUser(registerUserRequest) }
        return when {
            res.isSuccess -> Response(OK)
                .header("HX-Redirect", "successful-registration")

            res.exceptionOrNull() is DuplicatedEmail -> Response(OK)
                .body(renderer(RegistrationPage(registerUserRequest, true, "form")))

            else -> res.throwIt()
        }
    }

}

data class RegistrationPage(
    val request: RegisterUserRequest = RegisterUserRequest("", "", ""),
    val duplicatedEmail: Boolean = false,
    override val selector: String? = null
) : SameFileNameViewModel

object SuccessfulRegistrationPage : SameFileNameViewModel