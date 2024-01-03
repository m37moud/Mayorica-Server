package com.example.mapper

import com.example.models.ProviderInformation
import com.example.models.dto.ProviderCreateDto

fun ProviderCreateDto.toModel() = ProviderInformation(
    name ?: "",
    latitude ?: 0.0,
    longitude ?: 0.0,
    country ?: "",
    governorate ?: "",
    address ?: ""

)