package com.example.data.gallery.categories.size

import com.example.models.SizeCategory

interface SizeCategoryDataSource {
    //size category
    suspend fun getAllSizeCategory(): List<SizeCategory>
    suspend fun getAllSizeCategoryByTypeId(typeCategoryId: Int): List<SizeCategory>
    suspend fun getAllSizeCategoryPageable(page: Int = 0, perPage: Int = 10): List<SizeCategory>

    suspend fun getSizeCategoryById(categorySizeId: Int): SizeCategory?
    suspend fun getSizeCategoryByName(categorySizeName: String): SizeCategory?
    suspend fun createSizeCategory(sizeCategory: SizeCategory): Int
    suspend fun updateSizeCategory(sizeCategory: SizeCategory): Int
    suspend fun deleteSizeCategory(categorySizeId: Int): Int
    suspend fun deleteAllSizeCategory(): Int
    suspend fun saveAllSizeCategory(sizeCategories: Iterable<SizeCategory>): Int

}