package com.example.models

data class ProductOfferCreate(
    val offerTitle: String,
    val offerDescription: String,
    val offerImageUrl: String?,
    val offerEndDate: String,
    val isHotOffer: Boolean = false,
    val userAdminId: Int,

)
