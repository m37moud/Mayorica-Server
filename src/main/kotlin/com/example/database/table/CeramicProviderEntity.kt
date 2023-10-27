package com.example.database.table

import org.ktorm.schema.*

object CeramicProviderEntity :Table<Nothing>("t_ceramic_provider") {
    val id = int("id").primaryKey()
    val userAdminID = int("admin_id")
    val name = varchar("provider_name" )
    val latitude = double("latitude")
    val longitude = double("longitude")
    val country = varchar("country" )
    val governorate = varchar("governorate")
    val address = varchar("address")
    val created_at = varchar("created_at")
    val updated_at = varchar("updated_at")
}