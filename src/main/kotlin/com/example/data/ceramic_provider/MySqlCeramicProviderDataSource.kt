package com.example.data.ceramic_provider

import com.example.database.table.CeramicProviderEntity
import com.example.models.CeramicProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*

class MySqlCeramicProviderDataSource(private val db: Database) : CeramicProviderDataSource {
    override suspend fun getAllCeramicProvider(): List<CeramicProvider> {
        return withContext(Dispatchers.IO) {
            val providers = db.from(CeramicProviderEntity)
                .select()
                .mapNotNull {
                    rowToCeramicProvider(it)
                }
            providers
        }
    }

    override suspend fun getCeramicProviderByID(id: Int): CeramicProvider? {
        return withContext(Dispatchers.IO) {
            val provider = db.from(CeramicProviderEntity)
                .select()
                .where {
                    CeramicProviderEntity.id eq id
                }.map {
                    rowToCeramicProvider(it)
                }.firstOrNull()
            provider
        }
    }

    override suspend fun getCeramicProviderByName(providerName: String): CeramicProvider? {
        return withContext(Dispatchers.IO) {
            val provider = db.from(CeramicProviderEntity)
                .select()
                .where {
                    CeramicProviderEntity.name eq providerName
                }.map {
                    rowToCeramicProvider(it)
                }.firstOrNull()
            provider
        }
    }

    override suspend fun getCeramicProviderByCountry(country: String): List<CeramicProvider> {
        return withContext(Dispatchers.IO) {
            val providers = db.from(CeramicProviderEntity)
                .select()
                .where {
                    CeramicProviderEntity.country eq country
                }.mapNotNull {
                    rowToCeramicProvider(it)
                }
            providers
        }
    }

    override suspend fun getCeramicProviderByGps(lat: Double, log: Double): CeramicProvider? {
        return withContext(Dispatchers.IO) {
            val provider = db.from(CeramicProviderEntity)
                .select()
                .where {
                    (CeramicProviderEntity.latitude eq lat) and
                            (CeramicProviderEntity.longitude eq log)
                }.map {
                    rowToCeramicProvider(it)
                }.firstOrNull()
            provider
        }
    }

    override suspend fun addCeramicProvider(ceramicProvider: CeramicProvider): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(CeramicProviderEntity) {
                set(it.name, ceramicProvider.name)
                set(it.latitude, ceramicProvider.latitude)
                set(it.longitude, ceramicProvider.longitude)
                set(it.country, ceramicProvider.country)
                set(it.governorate, ceramicProvider.governorate)
                set(it.address, ceramicProvider.address)

            }
            result
        }
    }

    override suspend fun updateCeramicProvider(providerId: Int, ceramicProvider: CeramicProvider): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(CeramicProviderEntity) {
                set(it.name, ceramicProvider.name)
                set(it.latitude, ceramicProvider.latitude)
                set(it.longitude, ceramicProvider.longitude)
                set(it.country, ceramicProvider.country)
                set(it.governorate, ceramicProvider.governorate)
                set(it.address, ceramicProvider.address)
                where {
                    it.id eq providerId
                }
            }
            result
        }
    }

    override suspend fun deleteCeramicProvider(id: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(CeramicProviderEntity) {
                it.id eq id
            }
            result

        }
    }
}

private fun rowToCeramicProvider(row: QueryRowSet?): CeramicProvider? {
    return if (row == null)
        null
    else {
        val id = row[CeramicProviderEntity.id] ?: -1
        val name = row[CeramicProviderEntity.name] ?: ""
        val latitude = row[CeramicProviderEntity.latitude] ?: 0.0
        val longitude = row[CeramicProviderEntity.longitude] ?: 0.0
        val country = row[CeramicProviderEntity.country] ?: ""
        val governorate = row[CeramicProviderEntity.governorate] ?: ""
        val address = row[CeramicProviderEntity.address] ?: ""

        CeramicProvider(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            country = country,
            governorate = governorate,
            address = address

        )
    }
}