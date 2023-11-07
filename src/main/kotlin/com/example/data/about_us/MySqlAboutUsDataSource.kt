package com.example.data.about_us

import com.example.database.table.AboutUsEntity
import com.example.database.table.AdminUserEntity
import com.example.database.table.ProductEntity
import com.example.models.AboutUs
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

class MySqlAboutUsDataSource(private val db: Database) : AboutUsDataSource {

    override suspend fun getAboutUsInfo(): AboutUs? {
        return withContext(Dispatchers.IO) {
            val result = db.from(AboutUsEntity)
                .select()
                .orderBy(AboutUsEntity.createdAt.desc())
                .map { rowToAboutUs(it) }
                .firstOrNull()
            result
        }
    }

    override suspend fun createAboutUs(aboutUs: AboutUs): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(AboutUsEntity) {
                set(it.country, aboutUs.country)
                set(it.governorate, aboutUs.governorate)
                set(it.address, aboutUs.address)
                set(it.telephone, aboutUs.telephone)
                set(it.email, aboutUs.email)
                set(it.latitude, aboutUs.latitude)
                set(it.longitude, aboutUs.longitude)
                set(it.userAdminID, aboutUs.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateAboutUs(aboutUs: AboutUs): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(AboutUsEntity) {
                set(it.country, aboutUs.country)
                set(it.governorate, aboutUs.governorate)
                set(it.address, aboutUs.address)
                set(it.telephone, aboutUs.telephone)
                set(it.email, aboutUs.email)
                set(it.latitude, aboutUs.latitude)
                set(it.longitude, aboutUs.longitude)
                set(it.userAdminID, aboutUs.userAdminID)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq aboutUs.id
                }
            }
            result
        }
    }

    override suspend fun deleteAboutUs(aboutUsId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(AboutUsEntity) {
                it.id eq aboutUsId
            }
            result
        }
    }

    private fun rowToAboutUs(row: QueryRowSet?): AboutUs? {
        return if (row == null) {
            null
        } else {
            val id = row[AboutUsEntity.id] ?: -1
            val country = row[AboutUsEntity.country] ?: ""
            val governorate = row[AboutUsEntity.governorate] ?: ""
            val address = row[AboutUsEntity.address] ?: ""
            val telephone = row[AboutUsEntity.telephone] ?: ""
            val email = row[AboutUsEntity.email] ?: ""
            val latitude = row[AboutUsEntity.latitude] ?: 0.0
            val longitude = row[AboutUsEntity.latitude] ?: 0.0
            val userAdminID = row[AboutUsEntity.userAdminID] ?: -1
            val createdAt = row[AboutUsEntity.createdAt] ?: LocalDateTime.now()
            val updatedAt = row[AboutUsEntity.updatedAt] ?: LocalDateTime.now()



            AboutUs(
                id = id,
                country = country,
                governorate = governorate,
                address = address,
                telephone = telephone,
                email = email,
                latitude = latitude,
                longitude = longitude,
                userAdminID = userAdminID,
                createdAt = createdAt.toDatabaseString(),
                updatedAt = updatedAt.toDatabaseString()


            )

        }
    }
}