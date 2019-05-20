package com.makki.eschool

import io.ktor.application.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import com.makki.eschool.base.ApiException
import com.makki.eschool.database.DatabaseWrapper
import com.makki.eschool.routing.applyRouting
import com.zaxxer.hikari.HikariDataSource
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.response.respond
import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Application")

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(StatusPages) {
        exception<ApiException> { ex ->
            call.respond(mapOf("error_code" to ex.code, "error_message" to ex.msg))
        }
    }

    initLocalDb()
    applyRouting()
}

/**
 * Initialize Hikari for the application - used on Heroku or Postgresql
 */
private fun Application.initDB() {
    logger.info("Server is running, initializing DB")

    val ds = HikariDataSource()
    ds.username = "api_user"
    ds.password = "api_user"
    ds.jdbcUrl = "jdbc:postgresql://localhost:3306/ESchoolDb"
    ds.driverClassName = "org.postgresql.Driver"
    Database.connect(ds)

    logger.info("DB initialization ended")
}

private fun Application.initLocalDb() {
    val db = DatabaseWrapper(Database.connect("jdbc:h2:~/test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver"))
    db.init()
}