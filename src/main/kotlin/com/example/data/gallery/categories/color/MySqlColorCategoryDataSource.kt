package com.example.data.gallery.categories.color

import com.example.database.table.AdminUserEntity
import com.example.database.table.ColorCategoryEntity
import com.example.database.table.TypeCategoryEntity
import com.example.models.ColorCategory
import com.example.models.dto.ColorCategoryDto
import com.example.models.dto.TypeCategoryDto
import com.example.utils.AlreadyExistsException
import com.example.utils.ErrorException
import com.example.utils.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import java.time.LocalDateTime


private val logger = KotlinLogging.logger {}

@Singleton
class MySqlColorCategoryDataSource(private val db: Database) : ColorCategoryDataSource {
    override suspend fun getNumberOfCategories(): Int {
        logger.debug { "getNumberOfCategories" }

        return withContext(Dispatchers.IO) {
            val result = db.from(ColorCategoryEntity)
                .select()
                .mapNotNull { rowToColorCategory(it) }
            result.size
        }
    }

    override suspend fun getAllColorCategory(): List<ColorCategory> {
        logger.debug { "getAllColorCategory" }

        return withContext(Dispatchers.IO) {
            val colorCategoriesList = db.from(ColorCategoryEntity)
                .select()
                .mapNotNull { rowToColorCategory(it) }
            colorCategoriesList
        }
    }

    override suspend fun getAllColorCategoryDto(): List<ColorCategoryDto> {
        logger.debug { "getAllColorCategoryDto" }

        return withContext(Dispatchers.IO) {
            val colorCategoriesList = db.from(ColorCategoryEntity)
                .innerJoin(AdminUserEntity, on = ColorCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    ColorCategoryEntity.id,
                    AdminUserEntity.username,
                    ColorCategoryEntity.color,
                    ColorCategoryEntity.colorValue,
                    ColorCategoryEntity.createdAt,
                    ColorCategoryEntity.updatedAt,
                )
                .mapNotNull { rowToColorCategoryDto(it) }
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

    override suspend fun getAllColorCategoryPageable(
        query: String?,
        byColor: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ColorCategoryDto> {
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val typeCategoriesList = db.from(ColorCategoryEntity)
                .innerJoin(AdminUserEntity, on = ColorCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    ColorCategoryEntity.id,
                    AdminUserEntity.username,
                    ColorCategoryEntity.color,
                    ColorCategoryEntity.colorValue,
                    ColorCategoryEntity.createdAt,
                    ColorCategoryEntity.updatedAt,
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
                        it += (TypeCategoryEntity.typeName like "%${query}%")
                    }
                    if (!byColor.isNullOrEmpty()) {

                        it += (ColorCategoryEntity.colorValue eq "$byColor")
                    }

                }
                .mapNotNull { rowToColorCategoryDto(it) }
            typeCategoriesList
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

    override suspend fun getColorCategoryByIdDto(categoryColorId: Int): ColorCategoryDto? {
        logger.debug { "getColorCategoryByIdDto: $categoryColorId" }
        return withContext(Dispatchers.IO) {
            val colorCategory = db.from(ColorCategoryEntity)
                .innerJoin(AdminUserEntity, on = ColorCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    ColorCategoryEntity.id,
                    AdminUserEntity.username,
                    ColorCategoryEntity.color,
                    ColorCategoryEntity.colorValue,
                    ColorCategoryEntity.createdAt,
                    ColorCategoryEntity.updatedAt,
                )
                .where { ColorCategoryEntity.id eq categoryColorId }
                .map { rowToColorCategoryDto(it) }
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

    override suspend fun getColorCategoryByNameDto(categoryColorName: String): ColorCategoryDto? {
        logger.debug { "getColorCategoryByNameDto: $categoryColorName" }

        return withContext(Dispatchers.IO) {
            val colorCategory = db.from(ColorCategoryEntity)
                .innerJoin(AdminUserEntity, on = ColorCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    ColorCategoryEntity.id,
                    AdminUserEntity.username,
                    ColorCategoryEntity.color,
                    ColorCategoryEntity.colorValue,
                    ColorCategoryEntity.createdAt,
                    ColorCategoryEntity.updatedAt,
                )
                .where { ColorCategoryEntity.color eq categoryColorName }
                .map { rowToColorCategoryDto(it) }
                .firstOrNull()
            colorCategory
        }
    }

    override suspend fun getColorCategoryByValueDto(colorValue: String): ColorCategoryDto? {
        logger.debug { "getColorCategoryByValueDto: $colorValue" }

        return withContext(Dispatchers.IO) {
            val colorCategory = db.from(ColorCategoryEntity)
                .innerJoin(AdminUserEntity, on = ColorCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    ColorCategoryEntity.id,
                    AdminUserEntity.username,
                    ColorCategoryEntity.color,
                    ColorCategoryEntity.colorValue,
                    ColorCategoryEntity.createdAt,
                    ColorCategoryEntity.updatedAt,
                )
                .where { ColorCategoryEntity.colorValue eq colorValue }
                .map { rowToColorCategoryDto(it) }
                .firstOrNull()
            colorCategory
        }
    }

    override suspend fun addColorCategory(colorCategory: ColorCategory): ColorCategoryDto {
        if (getColorCategoryByName(colorCategory.colorName) != null) throw AlreadyExistsException("this Category inserted before .")
        if (createColorCategory(colorCategory) < 0) throw ErrorException("Failed to create New Type Category .")
        return getColorCategoryByNameDto(colorCategory.colorName)
            ?: throw NotFoundException("failed to get The Category after created.")

    }

    override suspend fun createColorCategory(colorCategory: ColorCategory): Int {
        logger.debug { "createColorCategory: $colorCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.insert(ColorCategoryEntity) {
                set(it.color, colorCategory.colorName)
                set(it.colorValue, colorCategory.colorValue)
                set(it.userAdminID, colorCategory.userAdminID)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateColorCategory(id: Int, colorCategory: ColorCategory): Int {
        logger.debug { "updateColorCategory: $colorCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.update(ColorCategoryEntity) {
                set(it.color, colorCategory.colorName)
                set(it.colorValue, colorCategory.colorValue)
                set(it.userAdminID, colorCategory.userAdminID)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq id
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
            val colorValue = row[ColorCategoryEntity.colorValue] ?: ""
            val userAdminID = row[ColorCategoryEntity.userAdminID] ?: -1
            val createdAt = row[ColorCategoryEntity.createdAt] ?: ""
            val updatedAt = row[ColorCategoryEntity.updatedAt] ?: ""

            ColorCategory(
                id = id,
                colorName = color,
                colorValue = colorValue,
                userAdminID = userAdminID,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }

    private fun rowToColorCategoryDto(row: QueryRowSet?): ColorCategoryDto? {
        return if (row == null)
            null
        else {
            val id = row[ColorCategoryEntity.id] ?: -1
            val userAdminName = row[AdminUserEntity.username] ?: ""
            val colorName = row[ColorCategoryEntity.color] ?: ""
            val colorValue = row[ColorCategoryEntity.colorValue] ?: ""
            val createdAt = row[ColorCategoryEntity.createdAt] ?: ""
            val updatedAt = row[ColorCategoryEntity.updatedAt] ?: ""

            ColorCategoryDto(
                id = id,
                adminUserName = userAdminName,
                colorName = colorName,
                colorValue = colorValue,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }

}