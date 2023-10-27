package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class UserAdminResponse(
    val id : Int,
    val role :String
)
