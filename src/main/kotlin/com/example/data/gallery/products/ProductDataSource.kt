package com.example.data.gallery.products

import com.example.database.table.ProductEntity
import com.example.models.CeramicProductInfo
import com.example.models.Product
import com.example.models.TypeCategory
import com.example.models.dto.ColorCategoryMenu
import com.example.models.dto.ProductDto
import com.example.models.dto.SizeCategoryMenu
import com.example.models.dto.TypeCategoryMenu
import com.example.models.response.ProductResponse
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1

interface ProductDataSource {

    suspend fun getAllTypeCategoryMenu(): List<TypeCategoryMenu>
    suspend fun getAllSizeCategoryMenu(): List<SizeCategoryMenu>
    suspend fun getAllColorCategoryMenu(): List<ColorCategoryMenu>


    suspend fun getNumberOfProduct(): Int

    suspend fun getAllProduct(): List<Product>
    suspend fun getAllProductDto(): List<ProductDto>
    suspend fun getAllProductByOrder(
        sortField: Column<*> = ProductEntity.createdAt,
        sortDirection: Int = -1
    ): List<Product>

    suspend fun getAllProductByCategory(
        category: String,
        categoryValue: Int,
        sortField: Column<*> = ProductEntity.createdAt,
        sortDirection: Int = -1
    ): List<Product>

    suspend fun getAllProductByCategories(
        categoryType: Int = -1,
        categorySize: Int = -1,
        categoryColor: Int = -1,
        sortField: Column<*> = ProductEntity.createdAt,
        sortDirection: Int = -1
    ): List<Product>

    suspend fun getAllProductResponseByCategories(
        categoryType: Int = -1,
        categorySize: Int = -1,
        categoryColor: Int = -1,
        sortField: Column<*> = ProductEntity.createdAt,
        sortDirection: Int = -1
    ): List<ProductResponse>

    //    suspend fun getAllProductByType(categoryTypeId: Int = -1): List<Product>
//    suspend fun getAllProductBySize(categorySizeId: Int): List<Product>
//    suspend fun getAllProductByColor(categoryColorId: Int): List<Product>
    suspend fun getAllProductPageable(
        page: Int = 0, perPage: Int = 10
    ): List<Product>
    suspend fun getAllProductPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ProductDto>

    suspend fun getAllProductPageableByCategories(
        page: Int = 0, perPage: Int = 10,
        categoryType: Int = -1,
        categorySize: Int = -1,
        categoryColor: Int = -1,
        sortField: Column<*> = ProductEntity.createdAt,
        sortDirection: Int = -1
    ): List<Product>

    /**
     * return ProductResponse
     */
    suspend fun getAllProductResponsePageableByCategories(
        page: Int = 0, perPage: Int = 10,
        categoryType: Int = -1,
        categorySize: Int = -1,
        categoryColor: Int = -1,
        sortField: Column<*> = ProductEntity.createdAt,
        sortDirection: Int = -1
    ): List<ProductResponse>

    //    suspend fun getAllProductPageableByType(page: Int = 0, perPage: Int = 10, categoryTypeId: Int): List<Product>
//    suspend fun getAllProductPageableBySize(page: Int = 0, perPage: Int = 10, categoryTypeId: Int): List<Product>
//    suspend fun getAllProductPageableByColor(page: Int = 0, perPage: Int = 10, categoryTypeId: Int): List<Product>
    suspend fun getProductById(productId: Int): Product?
    suspend fun getProductByIdDto(productId: Int): ProductDto?
    suspend fun getProductResponseById(productId: Int): ProductResponse?
    suspend fun getProductByName(productName: String): Product?
    suspend fun getProductByNameDto(productName: String): ProductDto?
    suspend fun searchProductByName(productName: String): List<Product?>
    suspend fun addCeramicProduct(product: CeramicProductInfo): ProductDto
    suspend fun createProduct(product: CeramicProductInfo): Int
    suspend fun updateProduct(id: Int, product: CeramicProductInfo): Int
    suspend fun deleteProduct(productId: Int): Int
    suspend fun deleteAllProduct(): Int
    suspend fun saveAllProduct(productList: Iterable<CeramicProductInfo>): Int


}