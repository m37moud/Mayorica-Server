package com.example.database

import com.example.config.AppConfig
import io.ktor.server.config.*
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database


private val logger = KotlinLogging.logger {}
/**
 * DataBase Helper to connect to our database
 * @property appConfig AppConfig Configuration of our service
 */
@Singleton
class DBHelper(
//    val appConfig: HoconApplicationConfig
    private val appConfig: AppConfig
) {
    private val initDatabaseData by lazy {
        appConfig.applicationConfiguration.propertyOrNull("database.initDatabaseData")?.getString()?.toBoolean() ?: false
    }
    init {
        logger.debug { "Init DataBaseService" }
        init()
    }
    private fun init() :Database {
        return Database.connect(
                url = appConfig.applicationConfiguration.propertyOrNull("database.database")?.getString() ?: "" ,//database,//"jdbc:mysql://localhost:3306/mayorica_db",
                driver = appConfig.applicationConfiguration.propertyOrNull("database.driver")?.getString()?: "", // "com.mysql.cj.jdbc.Driver",
                user = appConfig.applicationConfiguration.propertyOrNull("database.user")?.getString() ?: "", //"root",
                password = appConfig.applicationConfiguration.propertyOrNull("database.password")?.getString() ?: "", // "20102010"
            )
//        if (initDatabaseData)

    }

//    val db = Database.connect(
//        url = database,//"jdbc:mysql://localhost:3306/mayorica_db",
//        driver = driver, // "com.mysql.cj.jdbc.Driver",
//        user = user, //"root",
//        password = password, // "20102010"
//    )
}