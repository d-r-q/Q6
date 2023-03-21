package q6.app

import q6.core.auth.AuthModule
import q6.core.users.UsersModule
import q6.platform.conf.AppConfig


class Q6Core(
    val appConfig: AppConfig,
    q6Infra: Q6Infra
) {

    val users = UsersModule(q6Infra.dbModule, q6Infra.rmqModule)
    val auth = AuthModule(users)


}
