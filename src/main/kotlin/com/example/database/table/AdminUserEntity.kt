package com.example.database.table

import org.ktorm.schema.*

object AdminUserEntity : Table<Nothing>("t_admin_users") {
    val id = int("id").primaryKey()
    val full_name = varchar("full_name")
    val username = varchar("username")
    val password = varchar("password")
    val salt = varchar("salt")
    val role = varchar("user_role")
    val created_at = datetime("created_at")
    val updated_at = datetime("updated_at")
}