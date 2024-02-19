package com.example.data.offers

import com.example.models.Offer
import com.example.models.ProductOfferCreate
import com.example.models.dto.OfferDto
import org.ktorm.schema.Column

interface OffersDataSource {
    suspend fun getNumberOfOffers(): Int

    suspend fun getAllOffers(): List<Offer>
    suspend fun getAllOffersPageable(
        query: String?,
        page: Int,
        perPage: Int,
        isHot: Boolean?,
        sortField: Column<*>,
        sortDirection: Int
    ): List<OfferDto>

    suspend fun getAllAvailableOffers(): List<Offer>
    suspend fun getLastAvailableOffer(): Offer?
    suspend fun getOffersById(id: Int): Offer?
    suspend fun getOffersByIdDto(id: Int): OfferDto?
    suspend fun getOfferByTitle(title: String): Offer?
    suspend fun getOfferByTitleDto(title: String): OfferDto?
    suspend fun getHotOffers(): Offer?
    suspend fun getRandomHotOffers(): Offer?

    suspend fun addOffers(offer: ProductOfferCreate): OfferDto?
    suspend fun createOffers(offer: ProductOfferCreate): Int
    suspend fun updateOffers(id: Int, offer: ProductOfferCreate): Int
    suspend fun deleteOffers(id: Int): Int
    suspend fun deleteAllOffers(): Int

    suspend fun addToHotOffer(id: Int): Int
    suspend fun removeFromHotOffer(id: Int): Int
}