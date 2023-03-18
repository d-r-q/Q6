package q6.core.users.api

@JvmInline
value class UserId(val id: Long)

data class UserIdentity(
    val id: UserId,
    val identity: String,
    val roles: Set<Role>
)
