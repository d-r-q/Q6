package q6.core.users.api


interface UsersService {

    fun registerUser(registerUserRequest: RegisterUserRequest): UserId

    fun findByEmail(email: String): User?
    fun findById(userId: UserId): User?

}