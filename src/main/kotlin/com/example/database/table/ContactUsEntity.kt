package com.example.database.table

import com.example.database.table.UserOrderEntity.primaryKey
import org.ktorm.schema.*

object ContactUsEntity  : Table<Nothing>("t_contact_us"){
    val id =int("id").primaryKey()
    val country = varchar("country")
    val governorate = varchar("governorate")
    val address = varchar("address")
    val telephone = varchar("telephone")
    val email = varchar("email")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val fbLink = varchar("fb_link")
    val youtubeLink = varchar("youtube_link")
    val instagramLink = varchar("instagram_link")
    val linkedInLink = varchar("linkedIn_link")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt= datetime("updated_at")
}