package com.example.data.about_us

import com.example.database.table.AboutUsEntity
import com.example.database.table.AdminUserEntity
import com.example.database.table.ContactUsEntity
import com.example.database.table.ProductEntity
import com.example.models.AboutUs
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

class MySqlAboutUsDataSource(private val db: Database) : AboutUsDataSource {

    override suspend fun getAllAboutUsInfo(): List<AboutUs> {
        return withContext(Dispatchers.IO) {
            val result = db.from(AboutUsEntity)
                .select()
                .orderBy(AboutUsEntity.createdAt.asc())
                .mapNotNull { rowToAboutUs(it) }
            result
        }
    }

    override suspend fun getAboutUsInfoById(id: Int): AboutUs? {
        return withContext(Dispatchers.IO) {
            val result = db.from(AboutUsEntity)
                .select()
                .orderBy(AboutUsEntity.createdAt.desc())
                .where {
                    AboutUsEntity.id eq id
                }
                .map { rowToAboutUs(it) }
                .firstOrNull()
            result
        }
    }

    override suspend fun createAboutUs(aboutUs: AboutUs): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(AboutUsEntity) {
                set(it.title, aboutUs.title)
                set(it.information, aboutUs.information)
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
                set(it.title, aboutUs.title)
                set(it.information, aboutUs.information)
                set(it.userAdminID, aboutUs.userAdminID)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq aboutUs.id
                }
            }
            result
        }
    }

    override suspend fun deleteAboutUs(id: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(AboutUsEntity){
                it.id eq id
            }
            result
        }
    }
    override suspend fun deleteAllAboutUs(): Int {
        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(AboutUsEntity)
            result
        }
    }
    private fun rowToAboutUs(row: QueryRowSet?): AboutUs? {
        return if (row == null) {
            null
        } else {
            val id = row[AboutUsEntity.id] ?: -1
            val title = row[AboutUsEntity.title] ?: ""
            val information = row[AboutUsEntity.information] ?: ""
            val userAdminID = row[AboutUsEntity.userAdminID] ?: -1
            val createdAt = row[AboutUsEntity.createdAt] ?: LocalDateTime.now()
            val updatedAt = row[AboutUsEntity.updatedAt] ?: LocalDateTime.now()



            AboutUs(
                id = id,
                title = title,
                information = information,
                userAdminID = userAdminID,
                createdAt = createdAt.toDatabaseString(),
                updatedAt = updatedAt.toDatabaseString()


            )

        }
    }
}