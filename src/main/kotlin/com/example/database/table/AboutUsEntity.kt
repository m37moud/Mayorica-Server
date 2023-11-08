package com.example.database.table

import org.ktorm.schema.*

object AboutUsEntity : Table<Nothing>("t_about_us") {
    val id = int("id").primaryKey()
    val title = varchar("title")
    val information = varchar("information")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}