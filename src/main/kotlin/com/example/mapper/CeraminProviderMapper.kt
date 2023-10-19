package com.example.mapper

import com.example.models.request.ceramic_provider.CeramicProviderRequest

fun CeramicProviderRequest.toModel = CeramicProvider(
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    country - this.country,
    governorate = this.governorate,
    address = this.address
)