package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object TypeCategoryEntity : Table<Nothing>("t_type_category") {
    val id = int("id").primaryKey()
    val typeName = varchar("type_name")
    val typeIcon = varchar("type_icon")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}