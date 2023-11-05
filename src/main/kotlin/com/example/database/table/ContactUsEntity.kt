package com.example.database.table

import com.example.database.table.UserOrderEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ContactUsEntity  : Table<Nothing>("t_contact_us"){
    val id =int("id").primaryKey()
    val fbLink = varchar("fb_link")
    val youtubeLink = varchar("youtube_link")
    val instagramLink = varchar("instagram_link")
    val linkedInLink = varchar("linkedIn_link")
    val userAdminID = int("admin_id")
    val created_at = datetime("created_at")
    val updated_at= datetime("updated_at")
}