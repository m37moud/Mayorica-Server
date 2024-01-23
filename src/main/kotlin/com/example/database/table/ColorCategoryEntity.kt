package com.example.database.table

import com.example.database.table.SizeCategoryEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ColorCategoryEntity :Table<Nothing>("t_color_category") {
    val id = int("id").primaryKey()
    val color = varchar("color")
    val colorValue = varchar("colorValue")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}