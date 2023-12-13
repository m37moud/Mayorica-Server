package com.example.database

import io.ktor.server.config.*
import org.ktorm.database.Database

class DBHelper(
//    val database: String,
//    val driver: String,
//    val user: String,
//    val password: String,
    val appConfig: HoconApplicationConfig

) {
    private val initDatabaseData by lazy {
        appConfig.propertyOrNull("database.initDatabaseData")?.getString()?.toBoolean() ?: false
    }
    fun init() :Database {
        return Database.connect(
                url = appConfig.propertyOrNull("database.database")?.getString() ?: "" ,//database,//"jdbc:mysql://localhost:3306/mayorica_db",
                driver = appConfig.propertyOrNull("database.driver")?.getString()?: "", // "com.mysql.cj.jdbc.Driver",
                user = appConfig.propertyOrNull("database.user")?.getString() ?: "", //"root",
                password = appConfig.propertyOrNull("database.password")?.getString() ?: "", // "20102010"
            )
//        if (initDatabaseData)

    }

//    val db = Database.connect(
//
//
//        url = database,//"jdbc:mysql://localhost:3306/mayorica_db",
//        driver = driver, // "com.mysql.cj.jdbc.Driver",
//        user = user, //"root",
//        password = password, // "20102010"
//    )
}