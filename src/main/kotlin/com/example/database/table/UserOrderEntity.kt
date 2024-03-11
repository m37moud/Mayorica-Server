package com.example.database.table

import org.ktorm.schema.*

object UserOrderEntity : Table<Nothing>("t_user_request"){
    val id =int("id").primaryKey()
    val fullName = varchar("full_name")
    val idNumber= varchar("id_number")
    val orderNumber= varchar("order_number")
    val department= varchar("department")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val country= varchar("country")
    val governorate= varchar("governorate")
    val address= varchar("address")
    val approveState= int("approve_state")
    val sellerId= int("sellerId")
    val createdAt = datetime("created_at")
    val updatedAt= datetime("updated_at")

}