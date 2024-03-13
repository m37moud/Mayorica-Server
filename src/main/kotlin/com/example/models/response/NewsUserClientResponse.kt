package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class NewsUserClientResponse(
    val id :Int =-1,
    val title :String,
    val image :String? = null,
    val newsDescription :String,
    val createdAt: String = "",

)
