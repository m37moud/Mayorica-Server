package com.example.data.gallery.categories

import com.example.models.TypeCategory
import com.example.models.dto.TypeCategoryDto
import org.ktorm.schema.Column

interface TypeCategoryDataSource {
    suspend fun getAllTypeCategory(): List<TypeCategory>
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
    suspend fun getTypeCategoryByName(categoryName: String): TypeCategory?
    suspend fun createTypeCategory(typeCategory: TypeCategory): Int
    suspend fun updateTypeCategory(typeCategory: TypeCategory): Int
    suspend fun deleteTypeCategory(categoryTypeId: Int): Int
    suspend fun deleteAllTypeCategory(): Int
    suspend fun saveAllTypeCategory(typeCategories: Iterable<TypeCategory>): Int


}