package com.example.data.gallery.categories.color

import com.example.models.ColorCategory
import com.example.models.dto.ColorCategoryDto
import org.ktorm.schema.Column

interface ColorCategoryDataSource {

    //color category
    suspend fun getNumberOfCategories(): Int
    suspend fun getAllColorCategory(): List<ColorCategory>
    suspend fun getAllColorCategoryDto(): List<ColorCategoryDto>
    suspend fun getAllColorCategoryPageable(page: Int = 0, perPage: Int = 10): List<ColorCategory>
    suspend fun getAllColorCategoryPageable(
        query: String?,
        byColor: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ColorCategoryDto>

    suspend fun getColorCategoryById(categoryColorId: Int): ColorCategory?
    suspend fun getColorCategoryByIdDto(categoryColorId: Int): ColorCategoryDto?
    suspend fun getColorCategoryByName(categoryColorName: String): ColorCategory?
    suspend fun getColorCategoryByNameDto(categoryColorName: String): ColorCategoryDto?
    suspend fun getColorCategoryByValueDto(colorValue: String): ColorCategoryDto?
    suspend fun addColorCategory(colorCategory: ColorCategory): ColorCategoryDto
    suspend fun createColorCategory(colorCategory: ColorCategory): Int
    suspend fun updateColorCategory(id: Int, colorCategory: ColorCategory): Int
    suspend fun deleteColorCategory(categoryColorId: Int): Int
    suspend fun deleteAllColorCategory(): Int
    suspend fun saveAllColorCategory(colorCategories: Iterable<ColorCategory>): Int

}