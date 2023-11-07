package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class News(
    val id :Int =-1,
    val title :String,
    val image :String,
    val newsDescription :String,
    val userAdminId: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",
)
