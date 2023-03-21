package q6.infra.rmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory
import q6.platform.conf.AppConfig
import q6.platform.rmq.RabbitMqClient


class RmqModule(
    appConfig: AppConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val factory = ConnectionFactory().apply {
        host = appConfig["q6.rmq.host"]
        virtualHost = appConfig["q6.rmq.virtual-host"]
        port = appConfig["q6.rmq.port"]
        username = appConfig["q6.rmq.username"]
        password = appConfig["q6.rmq.password"]
    }

    private val connection: Connection = factory.newConnection()
    private val channel: Channel = connection.createChannel()

    val rmqClient = RabbitMqClient(channel)

    fun stop() {
        try {
            channel.close()
        } catch (e: Throwable) {
            log.warn("Channel closing failed", e)
        }

        try {
            connection.close()
        } catch (e: Throwable) {
            log.warn("Connection closing failed", e)
        }
    }
}