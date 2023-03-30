package pro.azhidkov.q6.infra

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import java.io.IOException
import java.net.ConnectException
import java.net.Socket

val rabbitMqContainer: GenericContainer<*> by lazy {
    GenericContainer("rabbitmq:3-management-alpine")
        .withExposedPorts(5672, 15672)
        .withReuse(true)
        .withEnv(
            mapOf(
                "RABBITMQ_DEFAULT_USER" to "eventbus",
                "RABBITMQ_DEFAULT_PASS" to "password",
                "RABBITMQ_DEFAULT_VHOST" to "eventbus",
            )
        )
        .waitingFor(HttpWaitStrategy().forPort(15672).forPath("/#/").forStatusCode(200))
        .apply { start() }
}

const val providedRmqPort = 5683

fun getRmqPort(): Int = if (isOpen(providedRmqPort)) providedRmqPort else rabbitMqContainer.getMappedPort(5672)

@Suppress("SameParameterValue")
private fun isOpen(port: Int): Boolean {
    try {
        Socket("localhost", port).use {
            return true
        }
    } catch (e: ConnectException) {
        return false
    } catch (e: IOException) {
        throw IllegalStateException("Error while trying to check open port", e)
    }
}