package com.example.database.table

import com.example.database.table.SizeCategoryEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ColorCategoryEntity :Table<Nothing>("t_color_category") {
    val id = int("id").primaryKey()
    val typeCategoryId = int("typeCategory_id")
    val sizeCategoryId = int("sizeCategory_id")
    val color = varchar("size")
    val userAdminID = int("admin_id")
    val createdAt = varchar("created_at")
    val updatedAt = varchar("updated_at")
}