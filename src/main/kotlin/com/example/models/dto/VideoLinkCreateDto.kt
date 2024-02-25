package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoLinkCreateDto(
    val idLink: String,
    val linkEnabled: Boolean = false
)