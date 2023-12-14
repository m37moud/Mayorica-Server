package com.example.data.offers

import com.example.database.table.AboutUsEntity
import com.example.database.table.OffersEntity
import com.example.models.AboutUs
import com.example.models.Offers
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
@Singleton
class MySqlOffersDataSource(private val db: Database) : OffersDataSource {
    override suspend fun getAllOffers(): List<Offers> {
        return withContext(Dispatchers.IO) {
            val result = db.from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .mapNotNull { rowToOffers(it) }
            result
        }

    }

    override suspend fun getAllAvailableOffers(): List<Offers> {
        return withContext(Dispatchers.IO) {
            val result = db
                .from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .where {
                    OffersEntity.endedAt greaterEq LocalDateTime.now()
                }
                .mapNotNull { rowToOffers(it) }
            result
        }

    }

    override suspend fun getLastAvailableOffer(): Offers? {
        return withContext(Dispatchers.IO) {
            val result = db
                .from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .where {
                    OffersEntity.endedAt greaterEq LocalDateTime.now()
                }
                .mapNotNull { rowToOffers(it) }
                .firstOrNull()
            result
        }

    }


    override suspend fun getOffersById(id: Int): Offers? {
        return withContext(Dispatchers.IO) {
            val result = db.from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .where {
                    OffersEntity.id eq id
                }
                .map { rowToOffers(it) }
                .firstOrNull()
            result
        }

    }

    override suspend fun getOfferByTitle(title: String): Offers? {
        return withContext(Dispatchers.IO) {
            val result = db.from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .where {
                    OffersEntity.title eq title
                }
                .map { rowToOffers(it) }
                .firstOrNull()
            result
        }

    }

    override suspend fun getHotOffers(): Offers? {
        return withContext(Dispatchers.IO) {
            val result = db.from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .where {
                    OffersEntity.isHotOffer eq true
                }
                .map { rowToOffers(it) }
                .firstOrNull()
            result
        }

    }

    override suspend fun getRandomHotOffers(): Offers? {
        return withContext(Dispatchers.IO) {
            val result = db.from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .where {
                    (OffersEntity.isHotOffer eq true) and (OffersEntity.endedAt greaterEq LocalDateTime.now())
                }
                .map { rowToOffers(it) }

            result.randomOrNull()//[Random.nextInt(0, result.size - 1)]
        }

    }

    override suspend fun addOffers(offers: Offers): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(OffersEntity) {
                set(it.title, offers.title)
                set(it.offerDescription, offers.offerDescription)
                set(it.image, offers.image)
                set(it.isHotOffer, offers.isHotOffer)
                set(it.userAdminID, offers.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
                val stringDate = offers.endedAt
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val localDateTime = LocalDateTime.parse(stringDate, formatter)
                set(it.endedAt, localDateTime)
            }
            result
        }

    }

    override suspend fun updateOffers(offers: Offers): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(OffersEntity) {
                set(it.title, offers.title)
                set(it.offerDescription, offers.offerDescription)
                set(it.image, offers.image)
                set(it.isHotOffer, offers.isHotOffer)
                set(it.userAdminID, offers.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
                val stringDate = offers.endedAt
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
                val localDateTime = LocalDateTime.parse(stringDate, formatter)
                set(it.endedAt, localDateTime)
                where {
                    it.id eq offers.id
                }
            }
            result
        }

    }

    override suspend fun deleteOffers(id: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(OffersEntity) {
                it.id eq id
            }
            result
        }
    }

    override suspend fun deleteAllOffers(): Int {
        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(OffersEntity)
            result
        }

    }

    private fun rowToOffers(row: QueryRowSet?): Offers? {
        return if (row == null) {
            null
        } else {
            val id = row[OffersEntity.id] ?: -1
            val title = row[OffersEntity.title] ?: ""
            val offerDescription = row[OffersEntity.offerDescription] ?: ""
            val image = row[OffersEntity.image] ?: ""
            val isHotOffer = row[OffersEntity.isHotOffer] ?: false
            val userAdminID = row[OffersEntity.userAdminID] ?: -1
            val createdAt = row[OffersEntity.createdAt] ?: LocalDateTime.now()
            val updatedAt = row[OffersEntity.updatedAt] ?: LocalDateTime.now()
            val endedAt = row[OffersEntity.updatedAt] ?: LocalDateTime.now()



            Offers(
                id = id,
                title = title,
                offerDescription = offerDescription,
                image = image,
                isHotOffer = isHotOffer,
                userAdminID = userAdminID,
                createdAt = createdAt.toDatabaseString(),
                updatedAt = updatedAt.toDatabaseString(),
                endedAt = endedAt.toDatabaseString()


            )

        }
    }

}