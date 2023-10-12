package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object AdminUserEntity : Table<Nothing>("admin_users") {
    val id = int("id").primaryKey()
    val full_name = varchar("full_name")
    val username = varchar("username")
    val password = varchar("password")
    val salt = varchar("salt")
    val role = varchar("role")
    val created_at = varchar("created_at")
    val updated_at = varchar("updated_at")
}