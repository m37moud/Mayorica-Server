package com.example.data.gallery.products

import com.example.models.Product
import com.example.models.TypeCategory

interface ProductDataSource {
    suspend fun getAllProduct(): List<Product>
    suspend fun getAllProductPageable(page: Int = 0, perPage: Int = 10): List<Product>
    suspend fun getProductById(productId: Int): Product?
    suspend fun getProductByName(productName: String): Product?
    suspend fun createProduct(product: Product): Int
    suspend fun updateProduct(product: Product): Int
    suspend fun deleteProduct(productId: Int): Int
    suspend fun deleteAllProduct(): Int
    suspend fun saveAllProduct(productList: Iterable<Product>): Int


}