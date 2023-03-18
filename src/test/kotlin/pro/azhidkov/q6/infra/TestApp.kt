package pro.azhidkov.q6.infra

import q6.app.Q6AppHttp4kPort
import q6.app.Q6Core


val q6Core by lazy {
    val system = Q6Core(
        mapOf(
            "q6.db.jdbcUrl" to getJdbcUrl(),
            "q6.profile" to "prod"
        )
    )
    system.init()
}

val q6Http4kApp by lazy { Q6AppHttp4kPort(q6Core).app }

