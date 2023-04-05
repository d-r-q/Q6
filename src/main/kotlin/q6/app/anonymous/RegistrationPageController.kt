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
import q6.core.users.api.RegisterUserRequest
import q6.core.users.api.UsersService
import q6.platform.http4k.SameFileNameViewModel
import q6.platform.web.PageController


class RegistrationPageController(
    renderer: TemplateRenderer,
    usersService: UsersService
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
            usersService.registerUser(registerUserRequest)
            Response(OK)
                .header("HX-Redirect", "successful-registration")
        },
        "/successful-registration" bind Method.GET to {
            Response(OK).body(renderer(SuccessfulRegistrationPage))
        }
    )

}

data class RegistrationPage(
    val registerUserRequest: RegisterUserRequest = RegisterUserRequest("", "", "")
) : SameFileNameViewModel

object SuccessfulRegistrationPage : SameFileNameViewModel