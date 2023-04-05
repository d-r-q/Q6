package pro.azhidkov.q6.cases.app

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies
import q6.app.infra.auth.AUTH_TOKEN_COOKIE


class Q6Client(
    private val authToken: String
) {

    fun authenticate(req: Request) = req.cookie(AUTH_TOKEN_COOKIE, authToken)

    companion object {

        fun login(app: HttpHandler, email: String, pass: String): Q6Client {
            val postCredsRequest = Request(Method.POST, "/login")
                .contentType("application/x-www-form-urlencoded")
                .form("email", email)
                .form("password", pass)
            val loginResponse = app(postCredsRequest)
            check(loginResponse.status == Status.FOUND) { "Login failed: $loginResponse" }

            val authToken = loginResponse.cookies().find { it.name == AUTH_TOKEN_COOKIE } ?: error("No auth cookie")
            return Q6Client(authToken.value)
        }
    }
}