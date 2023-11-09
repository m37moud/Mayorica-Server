package com.example.data.offers

import com.example.models.Offers

interface OffersDataSource {
    suspend fun getAllOffers(): List<Offers>
    suspend fun getAllAvailableOffers(): List<Offers>
    suspend fun getOffersById(id: Int): Offers?
    suspend fun getOfferByTitle(title: String): Offers?
    suspend fun getHotOffers(): Offers?

    suspend fun addOffers(offers: Offers): Int
    suspend fun updateOffers(offers: Offers): Int
    suspend fun deleteOffers(id: Int): Int
    suspend fun deleteAllOffers(): Int
}