package com.example.database.table

import com.example.database.table.TypeCategoryEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object SizeCategoryEntity : Table<Nothing>("t_size_category") {
    val id = int("id").primaryKey()
    val typeCategoryId = int("typeCategory_id")
    val size = varchar("size")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
