package q6.infra.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import q6.platform.conf.AppConfig


class DbModule(
    private val appConfig: AppConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val dataSource: HikariDataSource by lazy {
        HikariConfig().let {
            it.jdbcUrl = appConfig["q6-db-jdbcurl"]
            it.username = appConfig["q6-db-username"]
            it.password = appConfig["q6-db-password"]
            it.addDataSourceProperty("cachePrepStmts", "true")
            it.addDataSourceProperty("prepStmtCacheSize", "250")
            it.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            HikariDataSource(it)
        }
    }

    val db: Database by lazy { Database.connect(dataSource) }

    fun init() {
        FlywayDbMigrator(dataSource, appConfig).migrateDb()
    }

    fun stop() {
        try {
            dataSource.close()
        } catch (e: Throwable) {
            log.warn("DataSource closing failed", e)
        }
    }

}
