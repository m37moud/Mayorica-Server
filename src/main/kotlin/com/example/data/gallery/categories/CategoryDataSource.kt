package com.example.data.gallery.categories

import com.example.models.ColorCategory
import com.example.models.SizeCategory
import com.example.models.TypeCategory
import kotlinx.coroutines.flow.Flow

interface CategoryDataSource {
    suspend fun getAllTypeCategory(): List<TypeCategory>
    suspend fun getAllTypeCategoryPageable(page: Int = 0, perPage: Int = 10): List<TypeCategory>
    suspend fun getTypeCategoryById(categoryTypeId: Int): TypeCategory?
    suspend fun getTypeCategoryByName(categoryName: String): TypeCategory?
    suspend fun createTypeCategory(typeCategory: TypeCategory): Int
    suspend fun updateTypeCategory(typeCategory: TypeCategory): Int
    suspend fun deleteTypeCategory(categoryTypeId: Int): Int
    suspend fun deleteAllTypeCategory(): Int
    suspend fun saveAllTypeCategory(typeCategories: Iterable<TypeCategory>): Int

    //size category
    suspend fun getAllSizeCategory(): List<SizeCategory>
    suspend fun getAllSizeCategoryPageable(page: Int = 0, perPage: Int = 10): List<SizeCategory>

    suspend fun getSizeCategoryById(categorySizeId: Int): SizeCategory?
    suspend fun getSizeCategoryByName(categorySizeName: String): SizeCategory?
    suspend fun createSizeCategory(sizeCategory: SizeCategory): Int
    suspend fun updateSizeCategory(sizeCategory: SizeCategory): Int
    suspend fun deleteSizeCategory(categorySizeId: Int): Int
    suspend fun deleteAllSizeCategory(): Int
    suspend fun saveAllSizeCategory(sizeCategories: Iterable<SizeCategory>): Int


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