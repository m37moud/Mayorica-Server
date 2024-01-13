package com.example.data.gallery.categories.size

import com.example.database.table.SizeCategoryEntity
import com.example.models.SizeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}
@Singleton
class MySqlSizeCategoryDataSource(private val db: Database) : SizeCategoryDataSource {

    override suspend fun getAllSizeCategory(): List<SizeCategory> {
        logger.debug { "getAllSizeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(SizeCategoryEntity)
                .select()
                .mapNotNull { rowToSizeCategory(it) }
            typeCategoriesList
        }
    }

    override suspend fun getAllSizeCategoryByTypeId(typeCategoryId: Int): List<SizeCategory> {
        logger.debug { "getAllSizeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(SizeCategoryEntity)
                .select()

                .where { SizeCategoryEntity.typeCategoryId eq typeCategoryId }
                .mapNotNull { rowToSizeCategory(it) }
            typeCategoriesList
        }
    }

    override suspend fun getAllSizeCategoryPageable(page: Int, perPage: Int): List<SizeCategory> {
        logger.debug { "getAllSizeCategoryPageable: $page, $perPage" }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val sizeCategoriesList = db.from(SizeCategoryEntity)
                .select()
                .limit(myLimit)
                .offset(myOffset)
                .mapNotNull { rowToSizeCategory(it) }
            sizeCategoriesList
        }
    }

    override suspend fun getSizeCategoryById(categorySizeId: Int): SizeCategory? {
        logger.debug { "getSizeCategoryById: $categorySizeId" }
        return withContext(Dispatchers.IO) {
            val sizeCategory = db.from(SizeCategoryEntity)
                .select()
                .where { SizeCategoryEntity.id eq categorySizeId }
                .map { rowToSizeCategory(it) }
                .firstOrNull()
            sizeCategory
        }

    }

    override suspend fun getSizeCategoryByName(categorySizeName: String): SizeCategory? {
        logger.debug { "getSizeCategoryByName: $categorySizeName" }

        return withContext(Dispatchers.IO) {
            val sizeCategory = db.from(SizeCategoryEntity)
                .select()
                .where { SizeCategoryEntity.size eq categorySizeName }
                .map { rowToSizeCategory(it) }
                .firstOrNull()
            sizeCategory
        }
    }

    override suspend fun createSizeCategory(sizeCategory: SizeCategory): Int {
        logger.debug { "createSizeCategory: $sizeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.insert(SizeCategoryEntity) {
                set(it.typeCategoryId, sizeCategory.typeCategoryId)
                set(it.size, sizeCategory.size)
                set(it.sizeImage, sizeCategory.sizeImage)
                set(it.userAdminID, sizeCategory.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateSizeCategory(sizeCategory: SizeCategory): Int {
        logger.debug { "updateSizeCategory: $sizeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.update(SizeCategoryEntity) {
                set(it.typeCategoryId, sizeCategory.typeCategoryId)
                set(it.size, sizeCategory.size)
                set(it.userAdminID, sizeCategory.userAdminID)

                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq sizeCategory.id
                }
            }
            result
        }
    }

    override suspend fun deleteSizeCategory(categorySizeId: Int): Int {
        logger.debug { "deleteSizeCategory: $categorySizeId" }

        return withContext(Dispatchers.IO) {
            val result = db.delete(SizeCategoryEntity) {
                it.id eq categorySizeId
            }
            result
        }
    }

    override suspend fun deleteAllSizeCategory(): Int {
        logger.debug { "deleteAllSizeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(SizeCategoryEntity)
            result
        }
    }

    override suspend fun saveAllSizeCategory(sizeCategories: Iterable<SizeCategory>): Int {
        logger.debug { "saveAllSizeCategory: $sizeCategories" }
        return withContext(Dispatchers.IO) {
            var result = 0
            sizeCategories.forEach {
                result = createSizeCategory(it)
            }
            result
        }
    }

    private fun rowToSizeCategory(row: QueryRowSet?): SizeCategory? {
        return if (row == null)
            null
        else {
            val id = row[SizeCategoryEntity.id] ?: -1
            val typeCategoryId = row[SizeCategoryEntity.typeCategoryId] ?: -1
            val size = row[SizeCategoryEntity.size] ?: ""
            val sizeImage = row[SizeCategoryEntity.sizeImage] ?: ""
            val userAdminID = row[SizeCategoryEntity.userAdminID] ?: -1
            val createdAt = row[SizeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[SizeCategoryEntity.updatedAt] ?: ""

            SizeCategory(
                id = id,
                typeCategoryId = typeCategoryId,
                size = size,
                sizeImage = sizeImage,
                userAdminID = userAdminID,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }

}