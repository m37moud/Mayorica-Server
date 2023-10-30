package com.example.database

import org.ktorm.database.Database

object DBHelper {
    val db = Database.connect(


        url = "jdbc:mysql://localhost:3306/mayorica_db",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = "20102010"
    )
}