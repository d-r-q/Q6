package q6.cases.app

import io.github.ulfs.assertj.jsoup.Assertions
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.body.form
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import q6.core.users.api.RegisterUserRequest
import q6.infra.q6Core
import q6.infra.q6Http4kApp
import kotlin.test.assertEquals

class RegistrationCases {

    private val log = LoggerFactory.getLogger(javaClass)

    @Test
    fun `Unauthorized user should have access to registration page`() {
        // Given
        val getRegisterPageRequest = Request(Method.GET, "/register")

        // When
        val response = q6Http4kApp(getRegisterPageRequest)

        // Then
        assertEquals(200, response.status.code)
        val body = Jsoup.parse(response.bodyString())
        Assertions.assertThatSpec(body) {
            node("#registerForm") {
                exists()
                attribute("hx-post") {
                    hasText("/register")
                }
            }

            node("input#submit") {
                exists()
                attribute("type") { hasText("submit") }
            }
        }
    }

    @Test
    fun `Successful registration page should contains corresponding message`() {
        // When
        val response = q6Http4kApp(Request(Method.GET, "successful-registration"))

        // Then
        assertEquals(200, response.status.code)
        Assertions.assertThatSpec(Jsoup.parse(response.bodyString())) {
            node("#registrationSuccess") { exists() }
        }
    }

    @Test
    fun `User should be able to login after registration`() {
        // Given
        val email = "newUser@ya.ru"
        val pass = "password"
        val postRegisterUserRequest = registerUserRequest(email, pass)

        // When
        val registerResponse = q6Http4kApp(postRegisterUserRequest)
        assertEquals(200, registerResponse.status.code)
        assertEquals("successful-registration", registerResponse.header("HX-Redirect"))

        // And when
        val postCredsRequest = Request(Method.POST, "/login")
            .contentType("application/x-www-form-urlencoded")
            .form("email", email)
            .form("password", pass)

        // Then
        val loginResponse = q6Http4kApp(postCredsRequest)

        assertEquals(302, loginResponse.status.code)
        assertEquals("/app/main", loginResponse.header("Location"))
    }

    @Test
    fun `When registration form with registered email submitted, then fragment with error message should be returned`() {
        // Given
        val theEmail = "test@ya.ru"
        val password = "password"
        val name = "name"
        q6Core.users.usersService.registerUser(RegisterUserRequest(theEmail, password, name))
        val reregisterRequest = registerUserRequest(theEmail, password, name)

        // When
        val reregisterResponse = q6Http4kApp(reregisterRequest)
        log.debug("Response: {}", reregisterResponse)

        // Then
        assertEquals(200, reregisterResponse.status.code)
        Assertions.assertThatSpec(Jsoup.parse(reregisterResponse.bodyString())) {
            node("input#emailInput.is-invalid") { exists() }
            node("input#nameInput") { attribute("value") { hasText(name) } }
            node("input#emailInput") { attribute("value") { hasText(theEmail) } }
            node("input#passwordInput") { attribute("value") { hasText(password) } }
        }
    }

}


private fun registerUserRequest(email: String, pass: String = "Irrelevant", name: String = "Irrelevant"): Request {
    val postRegisterUserRequest = Request(Method.POST, "/register")
        .contentType("application/x-www-form-urlencoded")
        .form("email", email)
        .form("password", pass)
        .form("name", name)
    return postRegisterUserRequest
}

fun Request.contentType(contentType: String): Request = this.header("Content-Type", contentType)
