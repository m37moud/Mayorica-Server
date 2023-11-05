package com.example.database.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object YouTubeLinkEntity: Table<Nothing>("t_youtube_link")  {
    val id = int("id").primaryKey()
    val idLink = varchar("idLink")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
