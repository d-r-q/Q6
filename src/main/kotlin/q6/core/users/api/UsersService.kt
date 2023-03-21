package q6.core.users.api


const val USER_REGISTERED_EVENTS_QUEUE = "UserRegistered"

interface UsersService {

    fun registerUser(registerUserRequest: RegisterUserRequest): UserId

    fun findByEmail(email: String): User?

    fun findById(userId: UserId): User?

}