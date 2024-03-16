package com.example.data.ceramic_provider

import com.example.models.CeramicProvider
import com.example.models.Country
import com.example.models.ProviderInformation
import com.example.models.dto.ProviderDto
import org.ktorm.schema.Column

interface CeramicProviderDataSource {
    suspend fun getNumberOfProvider() :Int
    suspend fun getAllCeramicProvider(): List<CeramicProvider>
    suspend fun getAllProviderPageable(
        page: Int,
        perPage: Int,

    ): List<CeramicProvider>

    suspend fun getAllCeramicProviderDto(): List<ProviderDto>
    suspend fun getAllCeramicProviderPageable(
        page: Int,
        perPage: Int,
        searchQuery: String?,
//        byCountry: String?,
//        byGovernorate: String?,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ProviderDto>

    suspend fun getNumberOfProviders(): Int
    suspend fun getAllLocations() : List<Country>


    suspend fun getCeramicProviderByID(id: Int): CeramicProvider?
    suspend fun getCeramicProviderByIdDto(id: Int): ProviderDto?
    suspend fun getCeramicProviderByName(providerName: String): CeramicProvider?
    suspend fun getCeramicProviderByNameDto(providerName: String): ProviderDto?
    suspend fun getCeramicProviderByCountry(country: String): List<CeramicProvider>
    suspend fun getCeramicProviderByCountryDto(country: String): List<ProviderDto>
    suspend fun getCeramicProviderBySearching(searchValue: String): List<CeramicProvider>
    suspend fun getCeramicProviderByGovernorate(governorate: String): List<CeramicProvider>
    suspend fun getCeramicProviderByGps(lat: Double, log: Double): CeramicProvider?

    //CRUD
    suspend fun addCeramicProvider(ceramicProvider: CeramicProvider): Int
    suspend fun updateCeramicProvider(providerId: Int,adminUserId:Int, ceramicProvider: ProviderInformation): Int
    suspend fun deleteCeramicProvider(id: Int): Int


    // user client no auth need
    suspend fun getNearlyProvider(latitude: Double, longitude: Double): List<CeramicProvider>
}