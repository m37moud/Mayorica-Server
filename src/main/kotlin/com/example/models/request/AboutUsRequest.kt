package com.example.models.request

import com.example.models.AboutUs
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
@Serializable
data class AboutUsRequest(
    val country: String,
    val governorate: String,
    val address: String,
    val telephone: String,
    val latitude: Double,
    val longitude: Double,
)

fun AboutUsRequest.toModel(adminId: Int) = AboutUs(
    country = this.country,
    governorate = this.governorate,
    address = this.address,
    telephone = this.telephone,
    latitude = this.latitude,
    longitude = this.longitude,
    userAdminID = adminId,
    )
