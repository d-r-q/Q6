package q6.core.users.api


data class RegisterUserRequest(
    val email: String,
    val password: String,
    val name: String
)
