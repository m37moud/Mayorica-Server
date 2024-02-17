package com.example.data.offers

import com.example.database.table.*
import com.example.models.Offer
import com.example.models.dto.OfferDto
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger { }

@Singleton
class MySqlOffersDataSource(private val db: Database) : OffersDataSource {
    override suspend fun getNumberOfOffers(): Int {
        logger.debug { "getNumberOfOffers call" }

        return withContext(Dispatchers.IO) {
            val offersList = db.from(OffersEntity)
                .select()
                .mapNotNull { rowToOffers(it) }
            offersList.size
        }
    }

    override suspend fun getAllOffers(): List<Offer> {
        return withContext(Dispatchers.IO) {
            val result = db.from(OffersEntity)
                .select()
                .orderBy(OffersEntity.createdAt.desc())
                .mapNotNull { rowToOffers(it) }
            result
        }

    }

    override suspend fun getAllOffersPageable(
        query: String?,
        page: Int,
        perPage: Int,
        isHot: Boolean?,
        sortField: Column<*>,
        sortDirection: Int
    ): List<OfferDto> {
        logger.debug { "getAllOffersPageable call " }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val offersList = db.from(OffersEntity)
                .innerJoin(AdminUserEntity, on = OffersEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    OffersEntity.id,
                    AdminUserEntity.username,
                    OffersEntity.title,
                    OffersEntity.offerDescription,
                    OffersEntity.image,
                    OffersEntity.isHotOffer,
                    OffersEntity.createdAt,
                    OffersEntity.updatedAt,
                    OffersEntity.endedAt,
                )
                .limit(myLimit)
                .offset(myOffset)
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )
                .whereWithConditions {
                    if (!query.isNullOrEmpty()) {
                        it += (OffersEntity.title like "%${query}%")
                    }
                    if (isHot != null) {
                        it += (OffersEntity.isHotOffer eq isHot)
                    }
                }
                .mapNotNull { rowToOfferDto(it) }
            offersList
        }


    }

    override suspend fun getAllAvailableOffers(): List<Offer> {
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

    override suspend fun getLastAvailableOffer(): Offer? {
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


    override suspend fun getOffersById(id: Int): Offer? {
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

    override suspend fun getOfferByTitle(title: String): Offer? {
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

    override suspend fun getHotOffers(): Offer? {
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

    override suspend fun getRandomHotOffers(): Offer? {
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

    override suspend fun addOffers(offer: Offer): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(OffersEntity) {
                set(it.title, offer.title)
                set(it.offerDescription, offer.offerDescription)
                set(it.image, offer.image)
                set(it.isHotOffer, offer.isHotOffer)
                set(it.userAdminID, offer.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
                val stringDate = offer.endedAt
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val localDateTime = LocalDateTime.parse(stringDate, formatter)
                set(it.endedAt, localDateTime)
            }
            result
        }

    }

    override suspend fun updateOffers(offer: Offer): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(OffersEntity) {
                set(it.title, offer.title)
                set(it.offerDescription, offer.offerDescription)
                set(it.image, offer.image)
                set(it.isHotOffer, offer.isHotOffer)
                set(it.userAdminID, offer.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
                val stringDate = offer.endedAt
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
                val localDateTime = LocalDateTime.parse(stringDate, formatter)
                set(it.endedAt, localDateTime)
                where {
                    it.id eq offer.id
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

    private fun rowToOffers(row: QueryRowSet?): Offer? {
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



            Offer(
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

    private fun rowToOfferDto(row: QueryRowSet?): OfferDto? {
        return if (row == null) {
            null
        } else {
            val id = row[OffersEntity.id] ?: -1
            val adminUserName = row[AdminUserEntity.username] ?: ""

            val title = row[OffersEntity.title] ?: ""
            val offerDescription = row[OffersEntity.offerDescription] ?: ""
            val image = row[OffersEntity.image]
            val isHotOffer = row[OffersEntity.isHotOffer] ?: false

            val createdAt = row[OffersEntity.createdAt] ?: ""
            val updatedAt = row[OffersEntity.updatedAt] ?: ""
            val endedAt = row[OffersEntity.updatedAt] ?: ""



            OfferDto(
                id = id,
                adminUserName = adminUserName,
                title = title,
                offerDescription = offerDescription,
                image = image,
                isHotOffer = isHotOffer,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString(),
                endedAt = endedAt.toString()


            )

        }
    }

}