package q6.infra

import q6.app.Q6AppHttp4kPort
import q6.app.Q6Core
import q6.app.Q6Infra
import q6.platform.conf.AppConfig

val testAppConfig = AppConfig(
    mapOf(
        "q6.db.jdbcUrl" to getJdbcUrl(),
        "q6.profile" to "prod",
        "q6.rmq.port" to getRmqPort().toString()
    )
)

val q6Infra by lazy {
    Q6Infra(testAppConfig)
        .init()
}

val q6Core by lazy {
    Q6Core(testAppConfig, q6Infra)
}

val q6Http4kApp by lazy { Q6AppHttp4kPort(q6Core).app }

