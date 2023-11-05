package com.example.database.table

import org.ktorm.schema.*

object AboutUsEntity : Table<Nothing>("t_about_us") {
    val id = int("id").primaryKey()
    val country = varchar("country")
    val governorate = varchar("governorate")
    val address = varchar("address")
    val telephone = varchar("telephone")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}