package pro.azhidkov.q6.infra

import org.slf4j.LoggerFactory
import q6.infra.db.DbModule
import java.sql.DriverManager
import java.sql.SQLException

const val providedDbUrl = "jdbc:postgresql://localhost:54311/q6"

object TestDb

private val log = LoggerFactory.getLogger(TestDb::class.java)

fun getJdbcUrl(): String {
    try {
        val con = DriverManager.getConnection(providedDbUrl.replace("q6", "postgres"), "q6", "password")
        log.info("Provided db found, recreating it")
        con.prepareStatement(
            """
            DROP DATABASE IF EXISTS q6;
            CREATE DATABASE q6;
        """.trimIndent()
        )
            .execute()
        log.info("Provided db found, recreated")
        return providedDbUrl
    } catch (e: SQLException) {
        log.info("Provided Db not found: ${e.message}")
    }

    return pgContainer.jdbcUrl
}

fun DbModule.cleanDb() {
    dataSource.connection.prepareStatement("TRUNCATE TABLE users CASCADE;").execute()
}