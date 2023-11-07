package com.example.database.table

import org.ktorm.schema.*

object YouTubeLinkEntity: Table<Nothing>("t_youtube_link")  {
    val id = int("id").primaryKey()
    val idLink = varchar("idLink")
    val linkEnabled = boolean("linkEnabled")
    val userAdminId = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
