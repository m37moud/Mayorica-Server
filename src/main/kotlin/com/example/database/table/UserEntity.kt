package com.example.database.table

import com.example.database.table.AdminUserEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserEntity :Table<Nothing>("t_users"){
    val id = int("id").primaryKey()
    val full_name = varchar("full_name")
    val username = varchar("username")
    val password = varchar("password")
    val salt = varchar("salt")
    val permission = int("permission")
    val userAdminId = int("admin_id")
    val created_at = datetime("created_at")
    val updated_at = datetime("updated_at")
}