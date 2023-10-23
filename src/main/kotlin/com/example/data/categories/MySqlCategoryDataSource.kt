package com.example.data.categories

import com.example.database.table.CeramicProviderEntity
import com.example.database.table.ColorCategoryEntity
import com.example.database.table.SizeCategoryEntity
import com.example.database.table.TypeCategoryEntity
import com.example.models.CeramicProvider
import com.example.models.ColorCategory
import com.example.models.SizeCategory
import com.example.models.TypeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.from
import org.ktorm.dsl.mapNotNull
import org.ktorm.dsl.select

class MySqlCategoryDataSource(private val db: Database) : CategoryDataSource {
    override suspend fun getAllTypeCategory(): List<TypeCategory> {
        return withContext(Dispatchers.IO) {
            val typeCategoriesList =db.from(TypeCategoryEntity)
                .select()
                .mapNotNull {  }
            typeCategoriesList
        }
    }

    override suspend fun getTypeCategoryById(categoryTypeId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getTypeCategoryByName(categoryName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTypeCategoryByName(typeCategory: TypeCategory) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTypeCategoryByName(categoryTypeId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSizeCategory(): List<TypeCategory> {
        TODO("Not yet implemented")
    }

    override suspend fun getSizeCategoryById(categorySizeId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getSizeCategoryByName(categorySizeName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSizeCategoryByName(sizeCategory: SizeCategory) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSizeCategoryByName(categorySizeId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllColorCategory(): List<TypeCategory> {
        TODO("Not yet implemented")
    }

    override suspend fun getColorCategoryById(categoryColorId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getColorCategoryByName(categoryColorName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateColorCategoryByName(colorCategory: ColorCategory) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteColorCategoryByName(categoryColorId: Int) {
        TODO("Not yet implemented")
    }

    private fun rowToTypeCategory(row: QueryRowSet?): TypeCategory? {
        return if (row == null)
            null
        else {
            val id = row[TypeCategoryEntity.id] ?: -1
            val typeName = row[TypeCategoryEntity.typeName] ?: ""
            val userAdminID = row[TypeCategoryEntity.adminId] ?: -1
            val createdAt = row[TypeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[TypeCategoryEntity.updatedAt] ?: ""

            TypeCategory(
                id = id,
                name = typeName,
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
            val userAdminID = row[SizeCategoryEntity.adminId] ?: -1
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
            val userAdminID = row[ColorCategoryEntity.adminId] ?: -1
            val createdAt = row[ColorCategoryEntity.createdAt] ?: ""
            val updatedAt = row[ColorCategoryEntity.updatedAt] ?: ""

            ColorCategory(
                id = id,
                typeCategoryId = typeCategoryId,
                sizeCategoryId = sizeCategoryId,
                size = size,
                userAdminID = userAdminID,
                createdAt = createdAt,
                updatedAt = updatedAt

            )
        }
    }



}