package q6.app

import org.slf4j.LoggerFactory
import q6.infra.db.DbModule
import q6.infra.rmq.RmqModule
import q6.platform.conf.AppConfig


class Q6Infra(
    appConfig: AppConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val dbModule = DbModule(appConfig)
    val rmqModule = RmqModule(appConfig)

    fun init(): Q6Infra {
        log.info("Starting Q6 infra")
        dbModule.init()
        log.info("Q6 infra is initialized")
        Runtime.getRuntime().addShutdownHook(object : Thread() {

            override fun run() {
                this@Q6Infra.stop()
                log.info("Q6 infra is stopped")
            }
        })
        return this
    }

    fun stop() {
        dbModule.stop()
        rmqModule.stop()
    }

}