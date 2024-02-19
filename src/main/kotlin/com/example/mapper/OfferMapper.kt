package com.example.mapper

import com.example.models.ProductOfferCreate
import com.example.models.dto.ProductOfferCreateDto

fun ProductOfferCreateDto.toEntity(adminId: Int) =
    ProductOfferCreate(
        offerTitle = offerTitle,
        offerDescription = offerDescription,
        offerImageUrl = offerImageUrl,
        offerEndDate = offerEndDate,
        userAdminId = adminId
    )