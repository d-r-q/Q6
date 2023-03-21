package q6.platform.rmq

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rabbitmq.client.Channel
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass


class RabbitMqClient(
    private val channel: Channel
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val objectMapper = jacksonObjectMapper()

    fun send(routingKey: String, msg: Any) {
        channel.basicPublish("amq.direct", routingKey, null, objectMapper.writeValueAsString(msg).toByteArray())
    }

    fun redeclareDomainEventsQueue(queueName: String) {
        channel.queueDelete(queueName)
        channel.queueDeclare(queueName, true, false, false, null)
        channel.queueBind(queueName, "amq.direct", queueName)
    }

    fun <T : Any> receive(queue: String, type: KClass<T>): T? {
        val message: T?
        while (true) {
            val response = channel.basicGet(queue, true)
            if (response?.body == null) {
                continue
            }
            log.trace("Received message bytes: {}", response.body)
            if (log.isDebugEnabled) {
                log.trace("Received message string: {}", String(response.body))
            }

            message = objectMapper.readValue(response.body, type.java)
            break
        }
        return message
    }

}