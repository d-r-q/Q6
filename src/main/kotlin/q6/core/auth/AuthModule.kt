package q6.core.auth

import q6.core.auth.impl.AuthServiceImpl
import q6.core.users.UsersModule

class AuthModule(
    usersModule: UsersModule
) {

    val authService = AuthServiceImpl(usersModule.usersService)

}
