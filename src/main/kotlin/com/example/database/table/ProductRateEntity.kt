package com.example.database.table

import com.example.database.table.ProductImageEntity.primaryKey
import org.ktorm.schema.*

object ProductRateEntity : Table<Nothing>("t_product_rate") {
    val id = int("id").primaryKey()
    val fullName = varchar("full_name")
    val email = varchar("email")
    val productId = int("productId")
    val rate = double("rate")
    val rateMessage = varchar("rateMessage")
    val created_at = datetime("created_at")
    val updated_at = datetime("updated_at")
}