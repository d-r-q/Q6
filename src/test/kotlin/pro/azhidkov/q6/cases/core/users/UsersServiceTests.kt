package pro.azhidkov.q6.cases.core.users

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assume.assumeNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pro.azhidkov.q6.infra.q6Core
import q6.core.users.api.RegisterUserRequest
import q6.core.users.api.Role
import kotlin.test.assertNotNull


class UsersServiceTests {

    private val usersService = q6Core.users.usersService

    @BeforeEach
    fun setup() {
        q6Core.dbModule.dataSource.connection.prepareStatement("TRUNCATE TABLE users CASCADE;").execute()
    }

    @Test
    fun `User password should not be extractable after registration`() {
        // Given
        val password = "password"
        val email = "irrelevant"
        val registerUserRequest = RegisterUserRequest(email, password, email)
        usersService.registerUser(registerUserRequest)

        // When
        val user = usersService.findByEmail(email)

        // Then
        assertNotNull(user)
        assertThat(user.password, not(equalTo(password)))
    }

    @Test
    fun `Registered user should have ROLE_USER`() {
        // Given
        val userId = usersService.registerUser(RegisterUserRequest("irrelevant", "irrelevant", "irrelevant"))

        // When
        val user = usersService.findById(userId)

        // Then
        assumeNotNull(user)
        assertThat(user?.roles).contains(Role.ROLE_USER)
    }

}