package com.example.mapper

import com.example.models.CeramicProvider
import com.example.models.request.ceramic_provider.CeramicProviderRequest
import com.example.models.response.CeramicProviderResponse
import com.example.utils.toDatabaseString
import java.time.LocalDateTime

fun CeramicProviderRequest.toEntity(userAdminId: Int) = CeramicProvider(
    userAdminID = userAdminId,
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    city = this.governorate,
    address = this.address,
    createdAt = LocalDateTime.now().toDatabaseString()


)


fun CeramicProviderRequest.toModelUpdate() = CeramicProvider(
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    city = this.governorate,
    address = this.address,
    updatedAt = LocalDateTime.now().toDatabaseString()


)

fun CeramicProvider.toUserResponse() = CeramicProviderResponse(
    id, name, latitude, longitude, country, city, address
)

fun List<CeramicProvider>.toUserResponse() = map { it.toUserResponse() }