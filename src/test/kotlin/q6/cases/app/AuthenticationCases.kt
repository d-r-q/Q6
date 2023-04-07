package q6.cases.app

import io.github.ulfs.assertj.jsoup.Assertions
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import q6.core.users.api.RegisterUserRequest
import q6.infra.cleanDb
import q6.infra.q6Core
import q6.infra.q6Http4kApp
import q6.infra.q6Infra
import kotlin.test.assertEquals

const val asergeevLogin = "asergeev@ya.ru"
const val asergeevPass = "password"

class AuthenticationCases {

    @BeforeEach
    fun setup() {
        q6Infra.dbModule.cleanDb()
    }

    @Test
    fun `When unauthenticated user opens login page, login page should be returned`() {
        // Given
        val getLoginPageRequest = Request(Method.GET, "/login")

        // When
        val response = q6Http4kApp(getLoginPageRequest)

        // Then
        assertLoginPageOpened(response)
    }

    @Test
    fun `When unauthenticated user opens restricted page, he should be redirected to login page`() {
        // Given
        val getRestrictedPageRequest = Request(Method.GET, "/app/main")

        // When
        val getRestrictedPageResponse = q6Http4kApp(getRestrictedPageRequest)

        // Then
        assertEquals(302, getRestrictedPageResponse.status.code)
        assertEquals("/login", getRestrictedPageResponse.header("Location"))
    }

    @Test
    fun `When authenticated user opens restricted page, it should be returned`() {
        // Given
        q6Core.users.usersService.registerUser(RegisterUserRequest(asergeevLogin, asergeevPass, "Алексндр Сергеев"))
        val client = Q6Client.login(q6Http4kApp, asergeevLogin, asergeevPass)
        val getRestrictedPageRequest = client.authenticate(Request(Method.GET, "/app/main"))

        // When
        val getRestrictedPageResponse = q6Http4kApp(getRestrictedPageRequest)

        // Then
        assertEquals(200, getRestrictedPageResponse.status.code)
        val body = Jsoup.parse(getRestrictedPageResponse.bodyString())
        Assertions.assertThatSpec(body) {
            this.node("#overview") { containsText("Обзор финансовых дел") }
        }
    }

    @Test
    fun `When authenticated user opens login page, login page should be returned`() {
        // Given
        q6Core.users.usersService.registerUser(RegisterUserRequest(asergeevLogin, asergeevPass, "Алексндр Сергеев"))
        val client = Q6Client.login(q6Http4kApp, asergeevLogin, asergeevPass)
        val getLoginPageRequest = client.authenticate(Request(Method.GET, "/login"))

        // When
        val response = q6Http4kApp(getLoginPageRequest)

        // Then
        assertLoginPageOpened(response)
    }

    private fun assertLoginPageOpened(response: Response) {
        assertEquals(200, response.status.code)
        val body = Jsoup.parse(response.bodyString())
        Assertions.assertThatSpec(body) {
            node("#loginForm") { exists() }
        }
    }

}