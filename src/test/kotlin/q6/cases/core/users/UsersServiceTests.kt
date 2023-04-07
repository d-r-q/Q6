package q6.cases.core.users

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assume.assumeNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import q6.core.users.api.RegisterUserRequest
import q6.core.users.api.Role
import q6.core.users.api.USER_REGISTERED_EVENTS_QUEUE
import q6.core.users.api.UserRegisteredEvent
import q6.infra.cleanDb
import q6.infra.q6Core
import q6.infra.q6Infra
import java.util.concurrent.CompletableFuture
import kotlin.test.assertNotNull


class UsersServiceTests {

    private val usersService = q6Core.users.usersService
    private val rmqClient = q6Infra.rmqModule.rmqClient

    @BeforeEach
    fun setup() {
        q6Infra.dbModule.cleanDb()
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

    @Test
    fun `UserService should publish domain event about user registration`() {
        // Given
        val registerUserRequest = RegisterUserRequest("irrelevant", "irrelevant", "irrelevant")
        rmqClient.redeclareDomainEventsQueue(USER_REGISTERED_EVENTS_QUEUE)

        val messageFuture: CompletableFuture<UserRegisteredEvent?> = CompletableFuture.supplyAsync {
            rmqClient.receive(USER_REGISTERED_EVENTS_QUEUE, UserRegisteredEvent::class)
        }

        // When
        val userId = usersService.registerUser(registerUserRequest.copy(email = "new"))
        usersService.registerUser(registerUserRequest)

        // Then
        messageFuture.join()
        val message = messageFuture.get()
        assertThat(message, notNullValue())
        assertThat(message!!.userId, equalTo(userId.id.toString()))
    }

}