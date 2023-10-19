package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object CeramicProviderEntity :Table<Nothing>("t_ceramic_provider") {
    val id = int("id").primaryKey()
    val name = varchar("name" , 255)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val country = varchar("country" , 255)
    val governorate = varchar("governorate")
    val address = varchar("address",1500)
}