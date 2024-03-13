package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class YoutubeLinkUserClientResponse(
    val id: Int = -1,
    val idLink: String,
    val linkEnabled: Boolean = false,
)
