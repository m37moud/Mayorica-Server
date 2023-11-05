package com.example.database.table

import com.example.database.table.ProductRateEntity.primaryKey
import org.ktorm.schema.*

object ProductLikesEntity  : Table<Nothing>("t_product_likes"){
    val id = int("id").primaryKey()
    val fullName = varchar("full_name")
    val email = varchar("email")
    val productId = int("productId")
    val likes = int("likes")
    val unLikes = int("unLike")
    val message = varchar("like_message")
    val created_at = datetime("created_at")
    val updated_at = datetime("updated_at")
}