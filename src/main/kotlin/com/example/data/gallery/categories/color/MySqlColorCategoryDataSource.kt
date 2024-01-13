package com.example.data.gallery.categories.color

import com.example.database.table.ColorCategoryEntity
import com.example.models.ColorCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime


private val logger = KotlinLogging.logger {}

@Singleton
class MySqlColorCategoryDataSource(private val db :Database) : ColorCategoryDataSource {
    override suspend fun getAllColorCategory(): List<ColorCategory> {
        logger.debug { "getAllColorCategory" }

        return withContext(Dispatchers.IO) {
            val colorCategoriesList = db.from(ColorCategoryEntity)
                .select()
                .mapNotNull { rowToColorCategory(it) }
            colorCategoriesList
        }
    }

    override suspend fun getAllColorCategoryPageable(page: Int, perPage: Int): List<ColorCategory> {
        logger.debug { "getAllColorCategoryPageable: $page, $perPage" }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val colorCategoriesList = db.from(ColorCategoryEntity)
                .select()
                .limit(myLimit)
                .offset(myOffset)
                .mapNotNull { rowToColorCategory(it) }
            colorCategoriesList
        }
    }

    override suspend fun getColorCategoryById(categoryColorId: Int): ColorCategory? {
        logger.debug { "getSizeCategoryById: $categoryColorId" }
        return withContext(Dispatchers.IO) {
            val colorCategory = db.from(ColorCategoryEntity)
                .select()
                .where { ColorCategoryEntity.id eq categoryColorId }
                .map { rowToColorCategory(it) }
                .firstOrNull()
            colorCategory
        }
    }

    override suspend fun getColorCategoryByName(categoryColorName: String): ColorCategory? {
        logger.debug { "getColorCategoryByName: $categoryColorName" }

        return withContext(Dispatchers.IO) {
            val colorCategory = db.from(ColorCategoryEntity)
                .select()
                .where { ColorCategoryEntity.color eq categoryColorName }
                .map { rowToColorCategory(it) }
                .firstOrNull()
            colorCategory
        }
    }

    override suspend fun createColorCategory(colorCategory: ColorCategory): Int {
        logger.debug { "createColorCategory: $colorCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.insert(ColorCategoryEntity) {
                set(it.color, colorCategory.color)
                set(it.userAdminID, colorCategory.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateColorCategory(colorCategory: ColorCategory): Int {
        logger.debug { "updateColorCategory: $colorCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.update(ColorCategoryEntity) {
                set(it.color, colorCategory.color)
                set(it.userAdminID, colorCategory.userAdminID)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq colorCategory.id
                }
            }
            result
        }
    }

    override suspend fun deleteColorCategory(categoryColorId: Int): Int {
        logger.debug { "deleteColorCategory: $categoryColorId" }

        return withContext(Dispatchers.IO) {
            val result = db.delete(ColorCategoryEntity) {
                it.id eq categoryColorId
            }
            result
        }
    }

    override suspend fun deleteAllColorCategory(): Int {
        logger.debug { "deleteAllColorCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(ColorCategoryEntity)
            result
        }
    }

    override suspend fun saveAllColorCategory(colorCategories: Iterable<ColorCategory>): Int {
        logger.debug { "saveAllColorCategory: $colorCategories" }
        return withContext(Dispatchers.IO) {
            var result = 0
            colorCategories.forEach {
                result = createColorCategory(it)
            }
            result
        }
    }



    private fun rowToColorCategory(row: QueryRowSet?): ColorCategory? {
        return if (row == null)
            null
        else {
            val id = row[ColorCategoryEntity.id] ?: -1
            val color = row[ColorCategoryEntity.color] ?: ""
            val userAdminID = row[ColorCategoryEntity.userAdminID] ?: -1
            val createdAt = row[ColorCategoryEntity.createdAt] ?: ""
            val updatedAt = row[ColorCategoryEntity.updatedAt] ?: ""

            ColorCategory(
                id = id,
                color = color,
                userAdminID = userAdminID,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }

}