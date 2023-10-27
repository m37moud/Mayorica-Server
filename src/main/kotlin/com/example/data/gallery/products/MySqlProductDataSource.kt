package com.example.data.gallery.products

import com.example.database.table.ProductEntity
import com.example.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*

class MySqlProductDataSource(private val db: Database) : ProductDataSource {
    override suspend fun getAllProduct(): List<Product> {
        return withContext(Dispatchers.IO) {
            val productList = db.from(ProductEntity)
                .select()
                .orderBy(ProductEntity.createdAt.asc())
                .mapNotNull { rowToProduct(it) }
            productList
        }
    }

    override suspend fun getAllProductPageable(page: Int, perPage: Int): List<Product> {
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val productList = db.from(ProductEntity)
                .select()
                .limit(myLimit)
                .offset(myOffset)
                .orderBy(ProductEntity.createdAt.asc())
                .mapNotNull { rowToProduct(it) }
            productList
        }
    }

    override suspend fun getProductById(productId: Int): Product? {
        return withContext(Dispatchers.IO) {
            val product = db.from(ProductEntity)
                .select()
                .where { ProductEntity.id eq productId }
                .map { rowToProduct(it) }
                .firstOrNull()
            product
        }
    }

    override suspend fun getProductByName(productName: String): Product? {
        return withContext(Dispatchers.IO) {
            val product = db.from(ProductEntity)
                .select()
                .where { ProductEntity.productName eq productName }
                .map { rowToProduct(it) }
                .firstOrNull()
            product
        }
    }

    override suspend fun createProduct(product: Product): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(ProductEntity) {
                set(it.typeCategoryId, product.typeCategoryId)
                set(it.sizeCategoryId, product.sizeCategoryId)
                set(it.colorCategoryId, product.colorCategoryId)
                set(it.userAdminID, product.userAdminID)
                set(it.productName, product.productName)
                set(it.image, product.image)
                set(it.createdAt, product.createdAt)
                set(it.updatedAt, product.updatedAt)
                set(it.deleted, product.deleted)
            }
            result
        }
    }

    override suspend fun updateProduct(product: Product): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(ProductEntity) {
                set(it.typeCategoryId, product.typeCategoryId)
                set(it.sizeCategoryId, product.sizeCategoryId)
                set(it.colorCategoryId, product.colorCategoryId)
                set(it.userAdminID, product.userAdminID)
                set(it.productName, product.productName)
                set(it.image, product.image)
//              set(it.createdAt , product.createdAt)
                set(it.updatedAt, product.updatedAt)
                set(it.deleted, product.deleted)
                where {
                    it.id eq product.id
                }
            }
            result
        }
    }

    override suspend fun deleteProduct(productId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(ProductEntity) {
                it.id eq productId
            }
            result
        }

    }

    override suspend fun deleteAllProduct() = withContext(Dispatchers.IO) {
        return@withContext db.deleteAll(ProductEntity)
    }

    override suspend fun saveAllProduct(productList: Iterable<Product>) = withContext(Dispatchers.IO) {
        var result = 0
        productList.forEach { result = createProduct(it) }
        return@withContext result
    }


    private fun rowToProduct(row: QueryRowSet?): Product? {
        return if (row == null)
            null
        else {
            val id = row[ProductEntity.id] ?: -1
            val typeCategoryId = row[ProductEntity.typeCategoryId] ?: -1
            val sizeCategoryId = row[ProductEntity.sizeCategoryId] ?: -1
            val colorCategoryId = row[ProductEntity.colorCategoryId] ?: -1
            val userAdminID = row[ProductEntity.userAdminID] ?: -1
            val productName = row[ProductEntity.productName] ?: ""
            val image = row[ProductEntity.image] ?: ""
            val createdAt = row[ProductEntity.createdAt] ?: ""
            val updatedAt = row[ProductEntity.updatedAt] ?: ""
            val deleted = row[ProductEntity.deleted] ?: false


            Product(
                id = id,
                typeCategoryId = typeCategoryId,
                sizeCategoryId = sizeCategoryId,
                colorCategoryId = colorCategoryId,
                userAdminID = userAdminID,
                productName = productName,
                image = image,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deleted = deleted

            )
        }
    }
}