package com.example.data.gallery.categories.type

import com.example.models.TypeCategory
import com.example.models.TypeCategoryInfo
import com.example.models.dto.TypeCategoryDto
import com.example.models.dto.TypeCategoryMenu
import org.ktorm.schema.Column

interface TypeCategoryDataSource {
    suspend fun getAllTypeCategory(): List<TypeCategory>
    suspend fun getAllTypeCategoryMenu(): List<TypeCategoryMenu>
    suspend fun getNumberOfCategories(): Int

    suspend fun getAllTypeCategoryPageable(page: Int = 0, perPage: Int = 10): List<TypeCategory>
    suspend fun getAllTypeCategoryPageable(
        query: String?,
        page: Int = 0,
        perPage: Int = 10,
        sortField: Column<*>,
        sortDirection: Int
    ): List<TypeCategoryDto>

    suspend fun getTypeCategoryById(categoryTypeId: Int): TypeCategory?
    suspend fun getTypeCategoryByIdDto(categoryTypeId: Int): TypeCategoryDto?
    suspend fun getTypeCategoryByName(categoryName: String): TypeCategory?
    suspend fun getTypeCategoryByNameDto(categoryName: String): TypeCategoryDto?
    suspend fun addTypeCategory(typeCategory: TypeCategoryInfo): TypeCategoryDto
    suspend fun createTypeCategory(typeCategory: TypeCategoryInfo): Int
    suspend fun updateTypeCategory(id: Int, typeInfo: TypeCategoryInfo): Int
    suspend fun deleteTypeCategory(categoryTypeId: Int): Int
    suspend fun deleteAllTypeCategory(): Int
    suspend fun saveAllTypeCategory(typeCategories: Iterable<TypeCategoryInfo>): Int


}