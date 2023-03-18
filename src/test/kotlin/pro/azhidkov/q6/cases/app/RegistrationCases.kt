package pro.azhidkov.q6.cases.app

import io.github.ulfs.assertj.jsoup.Assertions
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.body.form
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import pro.azhidkov.q6.infra.q6Http4kApp
import kotlin.test.assertEquals

class RegistrationCases {

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
            node("#registerForm") { exists() }
        }
    }

    @Test
    fun `User should be able to login after registration`() {
        // Given
        val email = "newUser@ya.ru"
        val pass = "password"
        val postRegisterUserRequest = Request(Method.POST, "/register")
            .contentType("application/x-www-form-urlencoded")
            .form("email", email)
            .form("password", pass)
            .form("name", "Irrelevant")

        // When
        val registerResponse = q6Http4kApp(postRegisterUserRequest)

        // Then
        assertEquals(200, registerResponse.status.code)
        Assertions.assertThatSpec(Jsoup.parse(registerResponse.bodyString())) {
            node("#registrationSuccess") { exists() }
        }

        // And when
        val postCredsRequest = Request(Method.POST, "/login")
            .contentType("application/x-www-form-urlencoded")
            .form("email", email)
            .form("password", pass)

        // When
        val loginResponse = q6Http4kApp(postCredsRequest)

        // Then
        assertEquals(302, loginResponse.status.code)
        assertEquals("/app/main", loginResponse.header("Location"))
    }

}

fun Request.contentType(contentType: String): Request = this.header("Content-Type", contentType)
