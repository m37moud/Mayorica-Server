package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class YoutubeLink(
    val id: Int = -1,
    val idLink: String,
    val linkEnabled: Boolean = false,
    val userAdminId: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",
)
