package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileInfoDto(
    val fullName :String ,
    val userName :String ,
)
