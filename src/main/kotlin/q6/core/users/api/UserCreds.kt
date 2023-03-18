package q6.core.users.api

data class User(
    val id: UserId,
    val email: String,
    val password: String,
    val roles: Set<Role>
)
