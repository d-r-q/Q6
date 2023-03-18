package q6.core.auth.api

import q6.core.auth.api.LoginRequest
import q6.core.users.api.UserIdentity
import java.util.*


interface AuthService {

    fun authenticate(loginRequest: LoginRequest): Result<String>

    fun exchange(token: String): UserIdentity?
}