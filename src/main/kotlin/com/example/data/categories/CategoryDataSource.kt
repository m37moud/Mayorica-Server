package com.example.data.categories

import com.example.models.ColorCategory
import com.example.models.SizeCategory
import com.example.models.TypeCategory

interface CategoryDataSource {
    suspend fun getAllTypeCategory(): List<TypeCategory>
    suspend fun getTypeCategoryById(categoryTypeId: Int)
    suspend fun getTypeCategoryByName(categoryName: String)
    suspend fun updateTypeCategoryByName(typeCategory: TypeCategory)
    suspend fun deleteTypeCategoryByName(categoryTypeId: Int)

    //size category
    suspend fun getAllSizeCategory(): List<TypeCategory>
    suspend fun getSizeCategoryById(categorySizeId: Int)
    suspend fun getSizeCategoryByName(categorySizeName: String)
    suspend fun updateSizeCategoryByName(sizeCategory: SizeCategory)
    suspend fun deleteSizeCategoryByName(categorySizeId: Int)

    //color category

    suspend fun getAllColorCategory(): List<TypeCategory>
    suspend fun getColorCategoryById(categoryColorId: Int)
    suspend fun getColorCategoryByName(categoryColorName: String)
    suspend fun updateColorCategoryByName(colorCategory: ColorCategory)
    suspend fun deleteColorCategoryByName(categoryColorId: Int)


}