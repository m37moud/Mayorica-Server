package com.example.database.table

import com.example.database.table.YouTubeLinkEntity.primaryKey
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object NewsEntity : Table<Nothing>("t_news"){
    val id = int("id").primaryKey()
    val title = varchar("title")
    val image = varchar("image")
    val newsDescription = varchar("newsDescription")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}