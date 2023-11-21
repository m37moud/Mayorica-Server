package com.example.mapper

import com.example.models.CeramicProvider
import com.example.models.request.ceramic_provider.CeramicProviderRequest
import com.example.utils.toDatabaseString
import java.time.LocalDateTime

fun CeramicProviderRequest.toModelCreate(userAdminId : Int) = CeramicProvider(
    userAdminID = userAdminId,
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    governorate = this.governorate,
    address = this.address,
    createdAt = LocalDateTime.now().toDatabaseString()


)


fun CeramicProviderRequest.toModelUpdate() = CeramicProvider(
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    governorate = this.governorate,
    address = this.address,
    updatedAt = LocalDateTime.now().toDatabaseString()


)