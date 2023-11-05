package com.example.database.table

import com.example.database.table.MobileAppEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ProductImageEntity : Table<Nothing>("t_product_image"){
    val id = int("id").primaryKey()
    val productId = int("productId")
    val image = varchar("image")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

}