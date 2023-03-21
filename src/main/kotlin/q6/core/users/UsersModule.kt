package q6.core.users

import q6.core.users.api.UsersService
import q6.core.users.impl.UsersServiceImpl
import q6.infra.db.DbModule
import q6.infra.rmq.RmqModule


class UsersModule(
    dbModule: DbModule,
    rmqModule: RmqModule
) {

    val usersService: UsersService = UsersServiceImpl(dbModule.db, rmqModule.rmqClient)

}