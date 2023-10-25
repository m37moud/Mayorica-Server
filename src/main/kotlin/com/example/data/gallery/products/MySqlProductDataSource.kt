package com.example.data.gallery.products

import com.example.database.table.ColorCategoryEntity
import com.example.database.table.ProductEntity
import com.example.models.ColorCategory
import com.example.models.Product
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet

class MySqlProductDataSource(private val db :Database) :ProductDataSource {
    override suspend fun getAllProduct(): List<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProductPageable(page: Int, perPage: Int): List<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: Int): Product? {
        TODO("Not yet implemented")
    }

    override suspend fun getProductByName(productName: String): Product? {
        TODO("Not yet implemented")
    }

    override suspend fun createProduct(product: Product): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateProduct(product: Product): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productId: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllProduct(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun saveAllProduct(typeCategories: Iterable<Product>): Int {
        TODO("Not yet implemented")
    }


    private fun rowToProduct(row: QueryRowSet?): Product? {
        return if (row == null)
            null
        else {
            val id = row[ProductEntity.id] ?: -1
            val typeCategoryId = row[ProductEntity.typeCategoryId] ?: -1
            val sizeCategoryId = row[ProductEntity.sizeCategoryId] ?: -1
            val colorCategoryId = row[ProductEntity.colorCategoryId] ?: -1
            val productName = row[ProductEntity.productName] ?: ""
            val userAdminID = row[ProductEntity.userAdminID] ?: -1
            val createdAt = row[ProductEntity.createdAt] ?: ""
            val updatedAt = row[ProductEntity.updatedAt] ?: ""

            Product(
                id = id,
                typeCategoryId = typeCategoryId,
                sizeCategoryId = sizeCategoryId,
                colorCategoryId = color,
                userAdminID = userAdminID,
                createdAt = createdAt,
                updatedAt = updatedAt

            )
        }
    }
}