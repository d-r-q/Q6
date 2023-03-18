package pro.azhidkov.q6.infra

import q6.app.Q6App


val system by lazy {
    val system = Q6App(
        mapOf(
            "q6.db.jdbcUrl" to getJdbcUrl()
        )
    )
    system.init()
}

val app by lazy { system.web.app }

