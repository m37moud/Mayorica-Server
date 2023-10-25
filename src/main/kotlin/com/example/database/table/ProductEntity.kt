package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ProductEntity : Table<Nothing>("t_product") {
    val id = int("id").primaryKey()
    val typeCategoryId = int("typeCategoryId")
    val sizeCategoryId = int("sizeCategoryId")
    val colorCategoryId = int("colorCategoryId")
    val productName = varchar("product_name")
    val image = varchar("image")
    val createdAt = varchar("created_at")
    val updatedAt = varchar("updated_at")
    val deleted = boolean("deleted")

}