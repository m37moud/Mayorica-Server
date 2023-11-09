package com.example.models.request

import com.example.models.AboutUs
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
@Serializable
data class AboutUsRequest(
    val title: String,
    val information: String,
)

fun AboutUsRequest.toModel(adminId: Int) = AboutUs(
    title = this.title,
    information = this.information,
    userAdminID = adminId,
    )
