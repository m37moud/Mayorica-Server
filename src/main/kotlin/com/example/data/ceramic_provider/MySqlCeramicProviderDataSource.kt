package com.example.data.ceramic_provider

import com.example.database.table.CeramicProviderEntity
import com.example.models.CeramicProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.util.*
import kotlin.Comparator

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
                set(it.created_at, ceramicProvider.created_at)
                set(it.updated_at, ceramicProvider.updated_at)

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
                set(it.created_at, ceramicProvider.created_at)
                set(it.updated_at, ceramicProvider.updated_at)
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

    override suspend fun getNearlyProvider(latitude: Double, longitude: Double): List<CeramicProvider> {
        return withContext(Dispatchers.IO) {
            val providers = db.from(CeramicProviderEntity)
                .select()
                .mapNotNull {
                    rowToCeramicProvider(it)
                }
            Collections.sort(providers, Comparator<CeramicProvider> { o1, o2 ->
                val dist1: Int = o1.calculationByDistance(o1.latitude,o1.longitude,latitude,longitude)
                val dist2: Int = o2.calculationByDistance(o2.latitude,o2.longitude,latitude,longitude)
                dist1.compareTo(dist2)
            })

            providers
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
            val created_at = row[CeramicProviderEntity.created_at] ?: ""
            val updated_at = row[CeramicProviderEntity.updated_at] ?: ""

            CeramicProvider(
                id = id,
                name = name,
                latitude = latitude,
                longitude = longitude,
                country = country,
                governorate = governorate,
                address = address,
                created_at = created_at,
                updated_at = updated_at

            )
        }
    }
}

