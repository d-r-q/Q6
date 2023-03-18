package pro.azhidkov.q6.cases.app

import io.github.ulfs.assertj.jsoup.Assertions
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.cookie.cookie
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import pro.azhidkov.q6.infra.app
import pro.azhidkov.q6.infra.system
import q6.app.platform.AUTH_TOKEN_COOKIE
import q6.core.users.api.RegisterUserRequest
import kotlin.test.assertEquals

class AuthenticationCases {

    @Test
    fun `When unauthenticated user opens login page, login page should be returned`() {
        // Given
        val getLoginPageRequest = Request(Method.GET, "/login")

        // When
        val response = app(getLoginPageRequest)

        // Then
        assertEquals(200, response.status.code)
        val body = Jsoup.parse(response.bodyString())
        Assertions.assertThatSpec(body) {
            node("#loginForm") { exists() }
        }
    }

    @Test
    fun `When unauthenticated user opens restricted page, he should be redirected to login page`() {
        // Given
        val getRestrictedPageRequest = Request(Method.GET, "/app/main")

        // When
        val getRestrictedPageResponse = app(getRestrictedPageRequest)

        // Then
        assertEquals(302, getRestrictedPageResponse.status.code)
        assertEquals("/login", getRestrictedPageResponse.header("Location"))
    }

    @Test
    fun `When authenticated user opens restricted page, it should be returned`() {
        // Given
        val asergeevLogin = "asergeev@ya.ru"
        val asergeevPass = "password"
        system.users.usersService.registerUser(RegisterUserRequest(asergeevLogin, asergeevPass, "Алексндр Сергеев"))
        val client = Q6Client.login(app, asergeevLogin, asergeevPass)
        val getRestrictedPageRequest = client.authenticate(Request(Method.GET, "/app/main"))

        // When
        val getRestrictedPageResponse = app(getRestrictedPageRequest)

        // Then
        assertEquals(200, getRestrictedPageResponse.status.code)
        val body = Jsoup.parse(getRestrictedPageResponse.bodyString())
        Assertions.assertThatSpec(body) {
            this.node("#overview") { containsText("Обзор финансовых дел") }
        }
    }

}