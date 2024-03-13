package com.example.mapper

import com.example.models.News
import com.example.models.Offer
import com.example.models.ProductOfferCreate
import com.example.models.dto.ProductOfferCreateDto
import com.example.models.response.OfferUserClientResponse

fun ProductOfferCreateDto.toEntity(adminId: Int) =
    ProductOfferCreate(
        offerTitle = offerTitle,
        offerDescription = offerDescription,
        offerImageUrl = offerImageUrl,
        offerEndDate = offerEndDate,
        userAdminId = adminId
    )


fun Offer.toUserResponse() = OfferUserClientResponse(
    id, title, offerDescription, image, isHotOffer, createdAt, endedAt
)
fun List<Offer>.toUserResponse() = map { it.toUserResponse() }
