package q6.platform.conf

import org.http4k.cloudnative.env.Environment
import java.io.File
import kotlin.reflect.KClass


class AppConfig(config: Map<String, String>) {

    private val codeConfig: Environment = Environment.from(config)
    private val systemEnv: Environment = Environment.ENV
    private val jvmFlags: Environment = Environment.JVM_PROPERTIES
    private val filesystem: Environment =
        File("q6.properties").takeIf { it.exists() }?.let { Environment.from(it) } ?: Environment.EMPTY
    private val profileJar: Environment =
        "q6-test.properties".takeIfResourceExists()?.let { Environment.fromResource(it) } ?: Environment.EMPTY
    private val jar: Environment = Environment.fromResource("q6.properties")

    private val consolidated: Environment = codeConfig overrides
            jvmFlags overrides
            systemEnv overrides
            filesystem overrides
            jar overrides
            profileJar

    inline operator fun <reified T : Any> get(key: String): T =
        this[key, T::class]

    inline fun <reified T : Any> getOrDefault(key: String, default: T): T =
        this.getOrDefault(key, default, T::class)

    fun <T : Any> getOrDefault(key: String, default: T, type: KClass<T>): T = getOrNull(key, type) ?: default

    operator fun <T : Any> get(key: String, type: KClass<T>): T = getOrNull(key, type)
        ?: error("Value for key=$key not found")

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrNull(key: String, type: KClass<T>): T? = when (type) {
        String::class -> consolidated[key]
        Array<String>::class -> consolidated[key]?.split(",")?.map(String::trim)?.toTypedArray()
        Int::class -> consolidated[key]?.toInt()
        else -> error("Unsupported value type: $type")
    } as T?

}

private fun String.takeIfResourceExists(): String? =
    AppConfig::class.java.getResourceAsStream(this)?.let { this }