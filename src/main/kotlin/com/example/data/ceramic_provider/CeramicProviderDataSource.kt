package com.example.data.ceramic_provider

import com.example.models.CeramicProvider

interface CeramicProviderDataSource {
    suspend fun getAllCeramicProvider(): List<CeramicProvider>
    suspend fun getCeramicProviderByID(id: Int): CeramicProvider?
    suspend fun getCeramicProviderByName(providerName: String): CeramicProvider?
    suspend fun getCeramicProviderByCountry(country: String): List<CeramicProvider>
    suspend fun getCeramicProviderByGps(lat: Double, log: Double): CeramicProvider?

    //CRUD
    suspend fun addCeramicProvider(ceramicProvider: CeramicProvider): Int
    suspend fun updateCeramicProvider(providerId :Int ,ceramicProvider: CeramicProvider): Int
    suspend fun deleteCeramicProvider(id: Int): Int
}