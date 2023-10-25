package com.example.data.gallery.categories

import com.example.database.table.CeramicProviderEntity
import com.example.database.table.ColorCategoryEntity
import com.example.database.table.SizeCategoryEntity
import com.example.database.table.TypeCategoryEntity
import com.example.models.CeramicProvider
import com.example.models.ColorCategory
import com.example.models.SizeCategory
import com.example.models.TypeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.*

private val logger = KotlinLogging.logger {}

class MySqlCategoryDataSource(private val db: Database) : CategoryDataSource {
    override suspend fun getAllTypeCategory(): List<TypeCategory> {
        logger.debug { "getAllTypeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(TypeCategoryEntity)
                .select()
                .mapNotNull { rowToTypeCategory(it) }
            typeCategoriesList
        }
    }

    override suspend fun getAllTypeCategoryPageable(page: Int, perPage: Int): List<TypeCategory> {
        logger.debug { "getAllTypeCategoryPageable: $page, $perPage" }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val typeCategoriesList = db.from(TypeCategoryEntity)
                .select()
                .limit(myLimit)
                .offset(myOffset)
                .mapNotNull { rowToTypeCategory(it) }
            typeCategoriesList
        }
    }



    override suspend fun getTypeCategoryById(categoryTypeId: Int): TypeCategory? {
        logger.debug { "getTypeCategoryById: $categoryTypeId" }

        return withContext(Dispatchers.IO) {
            val typeCategory = db.from(TypeCategoryEntity)
                .select()
                .where { TypeCategoryEntity.id eq categoryTypeId }
                .map { rowToTypeCategory(it) }
                .firstOrNull()
            typeCategory
        }
    }

    override suspend fun getTypeCategoryByName(categoryName: String): TypeCategory? {
        logger.debug { "getTypeCategoryByName: $categoryName" }

        return withContext(Dispatchers.IO) {
            val typeCategory = db.from(TypeCategoryEntity)
                .select()
                .where { TypeCategoryEntity.typeName eq categoryName }
                .map { rowToTypeCategory(it) }
                .firstOrNull()
            typeCategory
        }
    }

    override suspend fun createTypeCategory(typeCategory: TypeCategory): Int {
        logger.debug { "createTypeCategory: $typeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.insert(TypeCategoryEntity) {
                set(it.typeName, typeCategory.typeName)
                set(it.userAdminID, typeCategory.userAdminID)
                set(it.createdAt, typeCategory.createdAt)
                set(it.updatedAt, typeCategory.updatedAt)
            }
            result
        }
    }

    override suspend fun updateTypeCategory(typeCategory: TypeCategory): Int {
        logger.debug { "updateTypeCategory: $typeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.update(TypeCategoryEntity) {
                set(it.typeName, typeCategory.typeName)
                set(it.userAdminID, typeCategory.userAdminID)
                set(it.createdAt, typeCategory.createdAt)
                set(it.updatedAt, typeCategory.updatedAt)
                where {
                    it.id eq typeCategory.id
                }
            }
            result
        }
    }

    override suspend fun deleteTypeCategory(categoryTypeId: Int): Int {
        logger.debug { "deleteTypeCategory: $categoryTypeId" }

        return withContext(Dispatchers.IO) {
            val result = db.delete(TypeCategoryEntity) {
                it.id eq categoryTypeId
            }
            result
        }
    }

    override suspend fun deleteAllTypeCategory(): Int {
        logger.debug { "deleteAllTypeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(TypeCategoryEntity)
            result
        }
    }


    override suspend fun saveAllTypeCategory(typeCategories: Iterable<TypeCategory>): Int {
        logger.debug { "saveAllTypeCategory: $typeCategories" }
        return withContext(Dispatchers.IO) {
            var result = 0
            typeCategories.forEach {
                result = createTypeCategory(it)
            }
            result
        }

    }

    override suspend fun getAllSizeCategory(): List<SizeCategory> {
        logger.debug { "getAllSizeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(SizeCategoryEntity)
                .select()
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
                set(it.userAdminID, sizeCategory.userAdminID)
                set(it.createdAt, sizeCategory.createdAt)
                set(it.updatedAt, sizeCategory.updatedAt)
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
                set(it.createdAt, sizeCategory.createdAt)
                set(it.updatedAt, sizeCategory.updatedAt)
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
                result += createSizeCategory(it)
            }
            result
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
                set(it.typeCategoryId, colorCategory.typeCategoryId)
                set(it.sizeCategoryId, colorCategory.sizeCategoryId)
                set(it.color, colorCategory.color)
                set(it.userAdminID, colorCategory.userAdminID)
                set(it.createdAt, colorCategory.createdAt)
                set(it.updatedAt, colorCategory.updatedAt)
            }
            result
        }
    }

    override suspend fun updateColorCategory(colorCategory: ColorCategory): Int {
        logger.debug { "updateColorCategory: $colorCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.update(ColorCategoryEntity) {
                set(it.typeCategoryId, colorCategory.typeCategoryId)
                set(it.sizeCategoryId, colorCategory.sizeCategoryId)
                set(it.color, colorCategory.color)
                set(it.userAdminID, colorCategory.userAdminID)
                set(it.createdAt, colorCategory.createdAt)
                set(it.updatedAt, colorCategory.updatedAt)
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
                result += createColorCategory(it)
            }
            result
        }
    }

    private fun rowToTypeCategory(row: QueryRowSet?): TypeCategory? {
        return if (row == null)
            null
        else {
            val id = row[TypeCategoryEntity.id] ?: -1
            val typeName = row[TypeCategoryEntity.typeName] ?: ""
            val userAdminID = row[TypeCategoryEntity.userAdminID] ?: -1
            val createdAt = row[TypeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[TypeCategoryEntity.updatedAt] ?: ""

            TypeCategory(
                id = id,
                typeName = typeName,
                userAdminID = userAdminID,
                createdAt = createdAt,
                updatedAt = updatedAt

            )
        }
    }

    private fun rowToSizeCategory(row: QueryRowSet?): SizeCategory? {
        return if (row == null)
            null
        else {
            val id = row[SizeCategoryEntity.id] ?: -1
            val typeCategoryId = row[SizeCategoryEntity.typeCategoryId] ?: -1
            val size = row[SizeCategoryEntity.size] ?: ""
            val userAdminID = row[SizeCategoryEntity.userAdminID] ?: -1
            val createdAt = row[SizeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[SizeCategoryEntity.updatedAt] ?: ""

            SizeCategory(
                id = id,
                typeCategoryId = typeCategoryId,
                size = size,
                userAdminID = userAdminID,
                createdAt = createdAt,
                updatedAt = updatedAt

            )
        }
    }

    private fun rowToColorCategory(row: QueryRowSet?): ColorCategory? {
        return if (row == null)
            null
        else {
            val id = row[ColorCategoryEntity.id] ?: -1
            val typeCategoryId = row[ColorCategoryEntity.typeCategoryId] ?: -1
            val sizeCategoryId = row[ColorCategoryEntity.sizeCategoryId] ?: -1
            val color = row[ColorCategoryEntity.color] ?: ""
            val userAdminID = row[ColorCategoryEntity.userAdminID] ?: -1
            val createdAt = row[ColorCategoryEntity.createdAt] ?: ""
            val updatedAt = row[ColorCategoryEntity.updatedAt] ?: ""

            ColorCategory(
                id = id,
                typeCategoryId = typeCategoryId,
                sizeCategoryId = sizeCategoryId,
                color = color,
                userAdminID = userAdminID,
                createdAt = createdAt,
                updatedAt = updatedAt

            )
        }
    }


}