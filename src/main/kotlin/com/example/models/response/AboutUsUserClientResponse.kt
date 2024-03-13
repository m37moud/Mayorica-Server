package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class AboutUsUserClientResponse(
    val id: Int = -1,
    val title :String,
    val information :String,
)
