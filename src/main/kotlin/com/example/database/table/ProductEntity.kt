package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ProductEntity : Table<Nothing>("t_product") {
    val id = int("id").primaryKey()
    val typeCategoryId = int("typeCategory_id")
    val sizeCategoryId = int("sizeCategory_id")
    val colorCategoryId = int("colorCategory_id")
    val userAdminID = int("admin_id")
    val productName = varchar("product_name")
    val image = varchar("image")
    val createdAt = varchar("created_at")
    val updatedAt = varchar("updated_at")
    val deleted = boolean("deleted")

}