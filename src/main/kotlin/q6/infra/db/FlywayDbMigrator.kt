package q6.infra.db

import org.flywaydb.core.Flyway
import q6.platform.conf.AppConfig
import javax.sql.DataSource

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