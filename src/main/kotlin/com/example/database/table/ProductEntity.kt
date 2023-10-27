package com.example.database.table

import org.ktorm.schema.*

object ProductEntity : Table<Nothing>("t_product") {
    val id = int("id").primaryKey()
    val typeCategoryId = int("typeCategory_id")
    val sizeCategoryId = int("sizeCategory_id")
    val colorCategoryId = int("colorCategory_id")
    val userAdminID = int("admin_id")
    val productName = varchar("product_name")
    val image = varchar("image")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val deleted = boolean("deleted")

}