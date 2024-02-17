package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Offer(
    val id: Int = -1,
    val title :String,
    val offerDescription :String,
    val image :String? = null,
    val isHotOffer :Boolean,
    val userAdminID: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",
    val endedAt: String = "",

)
