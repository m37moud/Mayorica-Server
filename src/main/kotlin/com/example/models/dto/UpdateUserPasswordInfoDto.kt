package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPasswordInfoDto(
    val oldPassword: String,
    val newPassword: String,
)
