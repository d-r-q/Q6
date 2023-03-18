package q6.core.users

import org.jetbrains.exposed.sql.SchemaUtils
import q6.core.users.api.UsersService
import q6.core.users.impl.UsersServiceImpl
import q6.platform.db.DbModule


class UsersModule(
    dbModule: DbModule
) {

    val usersService: UsersService = UsersServiceImpl(dbModule.db)

}