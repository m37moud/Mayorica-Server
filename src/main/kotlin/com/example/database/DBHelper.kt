package com.example.database

import org.ktorm.database.Database

class DBHelper(
    val database: String,
    val driver: String,
    val user: String,
    val password: String

) {
    fun init() :Database {
        return Database.connect(


                url = database,//"jdbc:mysql://localhost:3306/mayorica_db",
                driver = driver, // "com.mysql.cj.jdbc.Driver",
                user = user, //"root",
                password = password, // "20102010"
            )
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