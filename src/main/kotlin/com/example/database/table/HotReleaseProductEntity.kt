package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

object HotReleaseProductEntity : Table<Nothing>("t_hot_release_product") {
    val id = int("id").primaryKey()
    val productId = int("productId")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}