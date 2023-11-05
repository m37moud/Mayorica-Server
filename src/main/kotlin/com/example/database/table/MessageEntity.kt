package com.example.database.table

import com.example.database.table.UserOrderEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object MessageEntity : Table<Nothing>("t_message"){
    val id =int("id").primaryKey()
    val fullName = varchar("full_name")
    val email = varchar("email")
    val message = varchar("message")
    val created_at = datetime("created_at")
    val updated_at= datetime("updated_at")
}