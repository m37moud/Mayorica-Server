package com.example.data.gallery.categories.size

import com.example.models.SizeCategory
import com.example.models.SizeCategoryInfo
import com.example.models.dto.SizeCategoryDto
import com.example.models.dto.TypeCategoryDto
import com.example.models.dto.TypeCategoryMenu
import org.ktorm.schema.Column

interface SizeCategoryDataSource {
    suspend fun getAllTypeCategoryMenu(): List<TypeCategoryMenu>
    //size category
    suspend fun getAllSizeCategory(): List<SizeCategory>
    suspend fun getAllSizeCategoryDto(): List<SizeCategoryDto>
    suspend fun getNumberOfCategories(): Int


    suspend fun getAllSizeCategoryByTypeId(typeCategoryId: Int): List<SizeCategory>
    suspend fun getAllSizeCategoryPageable(
        page: Int = 0,
        perPage: Int = 10
    ): List<SizeCategory>

    suspend fun getAllSizeCategoryPageable(
        query: String?,
        page: Int = 0,
        perPage: Int = 10,
        byTypeCategoryId: Int?,
        sortField: Column<*>,
        sortDirection: Int
    ): List<SizeCategoryDto>

    suspend fun getSizeCategoryById(categorySizeId: Int): SizeCategory?
    suspend fun getSizeCategoryByIdDto(categorySizeId: Int): SizeCategoryDto?
    suspend fun getSizeCategoryByName(categorySizeName: String): SizeCategory?
    suspend fun getSizeCategoryByNameDto(categorySizeName: String): SizeCategoryDto?
    suspend fun addSizeCategory(sizeCategory: SizeCategoryInfo): SizeCategoryDto
    suspend fun createSizeCategory(sizeCategory: SizeCategoryInfo): Int

    suspend fun updateSizeCategory(id: Int, sizeCategory: SizeCategoryInfo): Int
    suspend fun deleteSizeCategory(categorySizeId: Int): Int
    suspend fun deleteAllSizeCategory(): Int
    suspend fun saveAllSizeCategory(sizeCategories: Iterable<SizeCategoryInfo>): Int

}