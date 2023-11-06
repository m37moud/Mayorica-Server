package com.example.data.gallery.products.hot_release

import com.example.database.table.*
import com.example.models.HotReleaseProduct
import com.example.models.Product
import com.example.models.response.ProductResponse
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

class MySqlHotReleaseDataSource(private val db: Database) : HotReleaseDataSource {
    override suspend fun getAllHotReleaseProduct(limit: Int): List<ProductResponse> {

        return withContext(Dispatchers.IO) {
            val myLimit = if (limit > 100) 100 else limit
            val hotProduct = db.from(HotReleaseProductEntity)
                .innerJoin(ProductEntity, on = HotReleaseProductEntity.productId eq ProductEntity.id)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .select(
                    ProductEntity.id,
                    ProductEntity.productName,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt,

                    )
                .limit(myLimit)
                .orderBy(HotReleaseProductEntity.createdAt.desc())
                .mapNotNull { rowToHotReleaseProductResponse(it) }
            hotProduct
        }
    }

    override suspend fun getHotReleaseProduct(productId: Int): HotReleaseProduct? {
        return withContext(Dispatchers.IO) {
            val result = db.from(HotReleaseProductEntity)
                .select()
                .where {
                    HotReleaseProductEntity.productId eq productId
                }
                .map { rowToHotReleaseProduct(it) }
                .firstOrNull()
            result
        }
    }

    override suspend fun addHotReleaseProduct(product: HotReleaseProduct): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(HotReleaseProductEntity) {
                set(it.productId, product.productId)
                set(it.userAdminID, product.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun deleteHotReleaseProduct(productId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(HotReleaseProductEntity) {
                it.productId eq productId
            }
            result
        }
    }

    private fun rowToHotReleaseProductResponse(row: QueryRowSet?): ProductResponse? {
        return if (row == null) {
            null
        } else {
            val id = row[ProductEntity.id] ?: -1
            val productName = row[ProductEntity.productName] ?: ""
            val typeCategory = row[TypeCategoryEntity.typeName] ?: ""
            val size = row[SizeCategoryEntity.size] ?: ""
            val color = row[ColorCategoryEntity.color] ?: ""
            val imageProduct = row[ProductEntity.image] ?: ""
            val createdAt = row[ProductEntity.createdAt]?.toDatabaseString() ?: ""
            val updatedAt = row[ProductEntity.updatedAt]?.toDatabaseString() ?: ""

            ProductResponse(
                id = id,
                productName = productName,
                typeCategoryName = typeCategory,
                sizeCategoryName = size,
                colorCategoryName = color,
                image = imageProduct,
                createdAt = createdAt,
                updatedAt = updatedAt

            )
        }
    }

    private fun rowToHotReleaseProduct(row: QueryRowSet?): HotReleaseProduct? {
        return if (row == null) {
            null
        } else {
            val id = row[HotReleaseProductEntity.id] ?: -1
            val productId = row[HotReleaseProductEntity.productId] ?: -1
            val userAdminId = row[HotReleaseProductEntity.userAdminID] ?: -1
            val createdAt = row[HotReleaseProductEntity.createdAt]?.toDatabaseString() ?: ""
            val updatedAt = row[HotReleaseProductEntity.updatedAt]?.toDatabaseString() ?: ""

            HotReleaseProduct(
                id = id,
                productId = productId,
                userAdminId = userAdminId,
                createdAt = createdAt,
                updatedAt = updatedAt

            )
        }
    }

}