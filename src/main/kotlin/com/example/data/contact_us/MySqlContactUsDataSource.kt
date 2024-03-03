package com.example.data.contact_us

import com.example.database.table.AboutUsEntity
import com.example.database.table.ContactUsEntity
import com.example.models.AboutUs
import com.example.models.ContactUs
import com.example.models.ContactUsCreate
import com.example.utils.UnknownErrorException
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

@Singleton
class MySqlContactUsDataSource(private val db: Database) : ContactUsDataSource {
    override suspend fun getContactUsInfo(): ContactUs? {
        return withContext(Dispatchers.IO) {
            val result = db.from(ContactUsEntity)
                .select()
                .orderBy(ContactUsEntity.createdAt.desc())
                .map { rowToContactUs(it) }
                .firstOrNull()
            result
        }
    }

    override suspend fun addContactUsInfo(contactUs: ContactUsCreate): ContactUs {
        val result = getContactUsInfo()?.let { contactUsItem ->
            updateContactUsInfo(id = contactUsItem.id, contactUs = contactUs)

        } ?: createContactUsInfo(contactUs)
        if (result < 0) throw UnknownErrorException("Failed to Insert Contact Us Information")
        return getContactUsInfo() ?: ContactUs()
    }

    suspend fun createContactUsInfo(contactUs: ContactUsCreate): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(ContactUsEntity) {
                set(it.country, contactUs.country)
                set(it.governorate, contactUs.governorate)
                set(it.address, contactUs.address)
                set(it.telephone, contactUs.telephone)
                set(it.email, contactUs.email)
                set(it.mapLabel, contactUs.mapLabel)
                set(it.latitude, contactUs.latitude)
                set(it.longitude, contactUs.longitude)
                set(it.webLink, contactUs.webLink)
                set(it.fbLink, contactUs.fbLink)
                set(it.youtubeLink, contactUs.youtubeLink)
                set(it.instagramLink, contactUs.instagramLink)
                set(it.linkedInLink, contactUs.linkedInLink)
                set(it.userAdminID, contactUs.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())

            }
            result
        }
    }


    override suspend fun updateContactUsInfo(id: Int, contactUs: ContactUsCreate): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(ContactUsEntity) {
                set(it.country, contactUs.country)
                set(it.governorate, contactUs.governorate)
                set(it.address, contactUs.address)
                set(it.telephone, contactUs.telephone)
                set(it.email, contactUs.email)
                set(it.mapLabel, contactUs.mapLabel)
                set(it.latitude, contactUs.latitude)
                set(it.longitude, contactUs.longitude)
                set(it.webLink, contactUs.webLink)
                set(it.fbLink, contactUs.fbLink)
                set(it.youtubeLink, contactUs.youtubeLink)
                set(it.instagramLink, contactUs.instagramLink)
                set(it.linkedInLink, contactUs.linkedInLink)
                set(it.userAdminID, contactUs.userAdminId)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq id
                }

            }
            result
        }

    }

    override suspend fun deleteContactUsInfo(): Int {
        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(ContactUsEntity)
            result
        }

    }

    private fun rowToContactUs(row: QueryRowSet?): ContactUs? {
        return if (row == null) {
            null
        } else {
            val id = row[ContactUsEntity.id] ?: -1
            val country = row[ContactUsEntity.country] ?: ""
            val governorate = row[ContactUsEntity.governorate] ?: ""
            val address = row[ContactUsEntity.address] ?: ""
            val telephone = row[ContactUsEntity.telephone] ?: ""
            val email = row[ContactUsEntity.email] ?: ""
            val mapLabel = row[ContactUsEntity.mapLabel] ?: ""
            val latitude = row[ContactUsEntity.latitude] ?: 0.0
            val longitude = row[ContactUsEntity.longitude] ?: 0.0
            val webLink = row[ContactUsEntity.webLink] ?: ""
            val fbLink = row[ContactUsEntity.fbLink] ?: ""
            val youtubeLink = row[ContactUsEntity.youtubeLink] ?: ""
            val instagramLink = row[ContactUsEntity.instagramLink] ?: ""
            val linkedInLink = row[ContactUsEntity.linkedInLink] ?: ""
            val userAdminID = row[ContactUsEntity.userAdminID] ?: -1
            val createdAt = row[ContactUsEntity.createdAt] ?: LocalDateTime.now()
            val updatedAt = row[ContactUsEntity.updatedAt] ?: LocalDateTime.now()



            ContactUs(
                id = id,
                country = country,
                governorate = governorate,
                address = address,
                telephone = telephone,
                email = email,
                mapLabel = mapLabel,
                latitude = latitude,
                longitude = longitude,
                webLink = webLink,
                fbLink = fbLink,
                youtubeLink = youtubeLink,
                instagramLink = instagramLink,
                linkedInLink = linkedInLink,
                userAdminID = userAdminID,
                createdAt = createdAt.toDatabaseString(),
                updatedAt = updatedAt.toDatabaseString()

            )

        }
    }

}