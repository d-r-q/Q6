package q6.core.auth.impl

import q6.core.auth.api.AuthService
import q6.core.auth.api.LoginRequest
import q6.core.auth.api.UserNotFount
import q6.core.users.api.UserIdentity
import q6.core.users.api.UsersService
import q6.platform.auth.PasswordEncoder
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AuthServiceImpl(
    private val usersService: UsersService
) : AuthService {

    private val tokens = ConcurrentHashMap<String, UserIdentity>()

    override fun authenticate(loginRequest: LoginRequest): Result<String> {
        val user = usersService.findByEmail(loginRequest.email)
            ?: return Result.failure(UserNotFount(loginRequest.email))
        if (!PasswordEncoder.verify(loginRequest.password, user.password).verified) {
            return Result.failure(InvalidPassword(loginRequest.email))
        }

        val userIdentity = UserIdentity(user.id, user.email, user.roles)
        val token = UUID.randomUUID().toString()
        tokens[token] = userIdentity
        return Result.success(token)
    }

    override fun exchange(token: String): UserIdentity? = tokens[token]

}