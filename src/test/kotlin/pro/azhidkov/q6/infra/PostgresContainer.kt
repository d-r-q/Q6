package pro.azhidkov.q6.infra

import org.testcontainers.containers.PostgreSQLContainer


val pgContainer: PostgreSQLContainer<*> by lazy {
    PostgreSQLContainer("postgres:15")
        .withExposedPorts(5432)
        .withUsername("q6")
        .withPassword("password")
        .withDatabaseName("postgres")
        .withTmpFs(mapOf("/var" to "rw"))
        .withEnv("PGDATA", "/var/lib/postgresql/data-no-mounted")
        .withCommand("-c max_connections=400")
        .withReuse(true)
        .withInitScript("db/q6-db-init.sql")
        .apply {
            start()
            // Сначала подключаемся к postgres, пересоздаём qyoga для обнуления фикстуры и подключаемся к ней
            this.withDatabaseName("q6")
        }
}
