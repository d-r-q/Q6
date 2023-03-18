package q6.platform.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import q6.platform.conf.AppConfig
import javax.sql.DataSource


class DbModule(
    private val appConfig: AppConfig
) {

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
        dataSource.close()
    }

}

class FlywayDbMigrator(
    private val dataSource: DataSource,
    private val config: AppConfig
) {

    fun migrateDb() {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations(*(config.getOrDefault<Array<String>>("q6.db.flyway.locations", emptyArray())))
            .load()
        flyway.migrate()
    }

}

