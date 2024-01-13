package com.example.data.gallery.categories.color

import com.example.models.ColorCategory

interface ColorCategoryDataSource {

    //color category

    suspend fun getAllColorCategory(): List<ColorCategory>
    suspend fun getAllColorCategoryPageable(page: Int = 0, perPage: Int = 10): List<ColorCategory>

    suspend fun getColorCategoryById(categoryColorId: Int): ColorCategory?
    suspend fun getColorCategoryByName(categoryColorName: String): ColorCategory?
    suspend fun createColorCategory(colorCategory: ColorCategory): Int
    suspend fun updateColorCategory(colorCategory: ColorCategory): Int
    suspend fun deleteColorCategory(categoryColorId: Int): Int
    suspend fun deleteAllColorCategory(): Int
    suspend fun saveAllColorCategory(colorCategories: Iterable<ColorCategory>): Int

}