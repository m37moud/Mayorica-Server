package com.example.models

data class VideoLinkCreate(
    val idLink: String,
    val linkEnabled: Boolean = false,
    val userAdminId: Int,

)