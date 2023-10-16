package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object UserOrderEntity : Table<Nothing>("user_request"){
    val id =int("id").primaryKey()
    val full_name = varchar("full_name")
    val id_number= varchar("id_number")
    val department= varchar("department")
    val country= varchar("country")
    val governorate= varchar("governorate")
    val approve_state= int("approve_state")
    val created_at = varchar("created_at")
    val updated_at= varchar("updated_at")

}