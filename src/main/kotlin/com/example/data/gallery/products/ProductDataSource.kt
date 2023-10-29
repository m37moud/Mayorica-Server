package com.example.data.gallery.products

import com.example.database.table.ProductEntity
import com.example.models.Product
import com.example.models.TypeCategory
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1

interface ProductDataSource {
    suspend fun getAllProduct(): List<Product>
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

    //    suspend fun getAllProductByType(categoryTypeId: Int = -1): List<Product>
//    suspend fun getAllProductBySize(categorySizeId: Int): List<Product>
//    suspend fun getAllProductByColor(categoryColorId: Int): List<Product>
    suspend fun getAllProductPageable(page: Int = 0, perPage: Int = 10): List<Product>
    suspend fun getAllProductPageableByCategories(
        page: Int = 0, perPage: Int = 10,
        categoryType: Int = -1,
        categorySize: Int = -1,
        categoryColor: Int = -1,
        sortField: Column<*> = ProductEntity.createdAt,
        sortDirection: Int = -1
    ): List<Product>

    //    suspend fun getAllProductPageableByType(page: Int = 0, perPage: Int = 10, categoryTypeId: Int): List<Product>
//    suspend fun getAllProductPageableBySize(page: Int = 0, perPage: Int = 10, categoryTypeId: Int): List<Product>
//    suspend fun getAllProductPageableByColor(page: Int = 0, perPage: Int = 10, categoryTypeId: Int): List<Product>
    suspend fun getProductById(productId: Int): Product?
    suspend fun getProductByName(productName: String): Product?
    suspend fun searchProductByName(productName: String): List<Product?>
    suspend fun createProduct(product: Product): Int
    suspend fun updateProduct(product: Product): Int
    suspend fun deleteProduct(productId: Int): Int
    suspend fun deleteAllProduct(): Int
    suspend fun saveAllProduct(productList: Iterable<Product>): Int


}