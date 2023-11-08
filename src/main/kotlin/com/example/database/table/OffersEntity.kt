package com.example.database.table

import com.example.database.table.ProductImageEntity.primaryKey
import org.ktorm.schema.*

object OffersEntity : Table<Nothing>("t_offers"){
    val id = int("id").primaryKey()
    val title = varchar("title")
    val offerDescription = varchar("offerDescription")
    val image = varchar("image")
    val isHotOffer = boolean("hotOffer")
    val userAdminID = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val endedAt = datetime("ended_at")
}