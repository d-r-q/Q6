package q6.app

import org.slf4j.LoggerFactory
import q6.core.auth.AuthModule
import q6.core.users.UsersModule
import q6.platform.conf.AppConfig
import q6.platform.db.DbModule


class Q6Core(
    config: Map<String, String> = emptyMap(),
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val appConfig = AppConfig(config)
    val dbModule = DbModule(appConfig)

    val users = UsersModule(dbModule)
    val auth = AuthModule(users)

    fun init(): Q6Core {
        log.info("Starting Q6 core")
        dbModule.init()
        log.info("Q6 core is initialized")
        Runtime.getRuntime().addShutdownHook(object : Thread() {

            override fun run() {
                dbModule.stop()
                log.info("Q6 core is stopped")
            }
        })
        return this
    }

}
