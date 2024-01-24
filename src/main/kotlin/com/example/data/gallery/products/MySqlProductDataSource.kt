package com.example.data.gallery.products

import com.example.database.table.*
import com.example.models.CeramicProductInfo
import com.example.models.Product
import com.example.models.dto.ProductDto
import com.example.models.response.ProductResponse
import com.example.utils.AlreadyExistsException
import com.example.utils.ErrorException
import com.example.utils.NotFoundException
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Singleton
class MySqlProductDataSource(private val db: Database) : ProductDataSource {
    override suspend fun getNumberOfProduct(): Int {
        logger.debug { "getNumberOfProduct: called" }

        return withContext(Dispatchers.IO) {
            val productList = db.from(ProductEntity)
                .select()
                .orderBy(ProductEntity.createdAt.asc())
                .mapNotNull { rowToProduct(it) }
            productList.size
        }
    }

    override suspend fun getAllProduct(): List<Product> {
        return withContext(Dispatchers.IO) {
            val productList = db.from(ProductEntity)
                .select()
                .orderBy(ProductEntity.createdAt.asc())
                .mapNotNull { rowToProduct(it) }
            productList
        }
    }

    override suspend fun getAllProductDto(): List<ProductDto> {
        return withContext(Dispatchers.IO) {
            val productList = db.from(ProductEntity)
                .innerJoin(AdminUserEntity, on = ProductEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .innerJoin(ColorCategoryEntity, on = ProductEntity.colorCategoryId eq ColorCategoryEntity.id)
                .select(
                    ProductEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ColorCategoryEntity.color,
                    ProductEntity.productName,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt
                )
                .orderBy(ProductEntity.createdAt.asc())
                .mapNotNull { rowToProductDto(it) }
            productList
        }
    }

    override suspend fun getAllProductByOrder(sortField: Column<*>, sortDirection: Int): List<Product> {
        return withContext(Dispatchers.IO) {


            val productList = db.from(ProductEntity)
                .select()
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )

                .mapNotNull { rowToProduct(it) }
            productList
        }
    }

    override suspend fun getAllProductByCategory(
        category: String,
        categoryValue: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<Product> {
        return withContext(Dispatchers.IO) {

            val productList = db.from(ProductEntity)
                .select()
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )
                .whereWithConditions {
                    when (category) {

                        "type" -> ProductEntity.typeCategoryId eq categoryValue
                        "size" -> ProductEntity.sizeCategoryId eq categoryValue
                        "color" -> ProductEntity.colorCategoryId eq categoryValue

                    }
                }
                .mapNotNull { rowToProduct(it) }
            productList
        }
    }

    override suspend fun getAllProductByCategories(
        categoryType: Int,
        categorySize: Int,
        categoryColor: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<Product> {
        return withContext(Dispatchers.IO) {
            logger.debug { "getAllProductByCategories /$sortField $sortDirection" }

            val productList = db.from(ProductEntity)
                .select()
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc() else sortField.desc()
                )
                .whereWithConditions {
                    if (categoryType > 0) it += ProductEntity.typeCategoryId eq categoryType
                    if (categorySize > 0) it += ProductEntity.sizeCategoryId eq categorySize
                    if (categoryColor > 0) it += ProductEntity.colorCategoryId eq categoryColor

                }
                .mapNotNull { rowToProduct(it) }
            productList
        }
    }

    override suspend fun getAllProductResponseByCategories(
        categoryType: Int,
        categorySize: Int,
        categoryColor: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ProductResponse> {
        return withContext(Dispatchers.IO) {
            logger.debug { "getAllProductByCategories /$sortField $sortDirection" }

            val productList = db.from(ProductEntity)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .innerJoin(ColorCategoryEntity, on = ProductEntity.colorCategoryId eq ColorCategoryEntity.id)

                .select(
                    ProductEntity.id,
                    ProductEntity.productName,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ColorCategoryEntity.color,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt,
                )
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc() else sortField.desc()
                )
                .whereWithConditions {
                    if (categoryType > 0) it += ProductEntity.typeCategoryId eq categoryType
                    if (categorySize > 0) it += ProductEntity.sizeCategoryId eq categorySize
                    if (categoryColor > 0) it += ProductEntity.colorCategoryId eq categoryColor

                }
                .mapNotNull { rowToProductResponse(it) }
            productList
        }
    }


    override suspend fun getAllProductPageable(page: Int, perPage: Int): List<Product> {
        logger.debug { "getAllProductPageable /$page $perPage" }

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

    override suspend fun getAllProductPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ProductDto> {
        logger.debug { "getAllProductPageable /$page $perPage" }
        val myLimit = if (perPage > 100) 100 else perPage
        val myOffset = (page * perPage)
        return withContext(Dispatchers.IO) {

            val productList = db.from(ProductEntity)
                .innerJoin(AdminUserEntity, on = ProductEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .innerJoin(ColorCategoryEntity, on = ProductEntity.colorCategoryId eq ColorCategoryEntity.id)
                .select(
                    ProductEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ColorCategoryEntity.color,
                    ProductEntity.productName,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt
                )
                .limit(myLimit)
                .offset(myOffset)
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )
                .whereWithConditions {
                    if (!query.isNullOrEmpty()) {
                        it += (ProductEntity.productName like "%${query}%")
                    }
                }
                .mapNotNull { rowToProductDto(it) }
            productList
        }
    }

    override suspend fun getAllProductPageableByCategories(
        page: Int, perPage: Int,
        categoryType: Int,
        categorySize: Int,
        categoryColor: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<Product> {
        return withContext(Dispatchers.IO) {
            logger.debug { "getAllProductPageableByCategories /$sortField $sortDirection" }

            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val productList = db.from(ProductEntity)
                .select()
                .limit(myLimit)
                .offset(myOffset)
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )
                .whereWithConditions {
                    if (categoryType > 0) it += ProductEntity.typeCategoryId eq categoryType
                    if (categorySize > 0) it += ProductEntity.sizeCategoryId eq categorySize
                    if (categoryColor > 0) it += ProductEntity.colorCategoryId eq categoryColor

                }
                .mapNotNull { rowToProduct(it) }
            productList
        }
    }

    override suspend fun getAllProductResponsePageableByCategories(
        page: Int, perPage: Int,
        categoryType: Int,
        categorySize: Int,
        categoryColor: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ProductResponse> {
        return withContext(Dispatchers.IO) {
            logger.debug { "getAllProductPageableByCategories /$sortField $sortDirection" }

            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val productList = db.from(ProductEntity)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .innerJoin(ColorCategoryEntity, on = ProductEntity.colorCategoryId eq ColorCategoryEntity.id)
                .select(
                    ProductEntity.id,
                    ProductEntity.productName,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ColorCategoryEntity.color,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt
                )
                .limit(myLimit)
                .offset(myOffset)
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )
                .whereWithConditions {
                    if (categoryType > 0) it += ProductEntity.typeCategoryId eq categoryType
                    if (categorySize > 0) it += ProductEntity.sizeCategoryId eq categorySize
                    if (categoryColor > 0) it += ProductEntity.colorCategoryId eq categoryColor

                }
                .mapNotNull { rowToProductResponse(it) }
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

    override suspend fun getProductByIdDto(productId: Int): ProductDto? {
        logger.debug { "getProductByIdDto" }
        return withContext(Dispatchers.IO) {
            val product = db.from(ProductEntity)
                .innerJoin(AdminUserEntity, on = ProductEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .innerJoin(ColorCategoryEntity, on = ProductEntity.colorCategoryId eq ColorCategoryEntity.id)
                .select(
                    ProductEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ColorCategoryEntity.color,
                    ProductEntity.productName,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt
                )
                .where { ProductEntity.id eq productId }
                .map { rowToProductDto(it) }
                .firstOrNull()
            product
        }
    }

    override suspend fun getProductResponseById(productId: Int): ProductResponse? {
        return withContext(Dispatchers.IO) {
            val product = db.from(ProductEntity)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .innerJoin(ColorCategoryEntity, on = ProductEntity.colorCategoryId eq ColorCategoryEntity.id)

                .select(
                    ProductEntity.id,
                    ProductEntity.productName,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ColorCategoryEntity.color,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt,
                )
                .where { ProductEntity.id eq productId }
                .map { rowToProductResponse(it) }
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

    override suspend fun getProductByNameDto(productName: String): ProductDto? {
        return withContext(Dispatchers.IO) {
            val product = db.from(ProductEntity)
                .innerJoin(AdminUserEntity, on = ProductEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = ProductEntity.typeCategoryId eq TypeCategoryEntity.id)
                .innerJoin(SizeCategoryEntity, on = ProductEntity.sizeCategoryId eq SizeCategoryEntity.id)
                .innerJoin(ColorCategoryEntity, on = ProductEntity.colorCategoryId eq ColorCategoryEntity.id)
                .select(
                    ProductEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    ColorCategoryEntity.color,
                    ProductEntity.productName,
                    ProductEntity.image,
                    ProductEntity.createdAt,
                    ProductEntity.updatedAt
                )
                .where { ProductEntity.productName eq productName }
                .map { rowToProductDto(it) }
                .firstOrNull()
            product
        }
    }

    override suspend fun searchProductByName(productName: String): List<Product?> {
        return withContext(Dispatchers.IO) {
            val product = db.from(ProductEntity)
                .select()
                .where { ProductEntity.productName like "%${productName}%" }
                .map { rowToProduct(it) }

            product
        }
    }

    override suspend fun addCeramicProduct(product: CeramicProductInfo): ProductDto {
        if (getProductByName(product.productName) != null) throw AlreadyExistsException("this Category inserted before .")
        if (createProduct(product) < 0) throw ErrorException("Failed to create New Size Category .")
        return getProductByNameDto(product.productName)
            ?: throw NotFoundException("failed to get Ceramic Product after created.")

    }

    override suspend fun createProduct(product: CeramicProductInfo): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(ProductEntity) {
                set(it.typeCategoryId, product.typeCategoryId)
                set(it.sizeCategoryId, product.sizeCategoryId)
                set(it.colorCategoryId, product.colorCategoryId)
                set(it.userAdminID, product.userAdminId)
                set(it.productName, product.productName)
                set(it.image, product.productImageUrl)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
                set(it.deleted, false)
            }
            result
        }
    }

    override suspend fun updateProduct(id: Int, product: CeramicProductInfo): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(ProductEntity) {
                set(it.typeCategoryId, product.typeCategoryId)
                set(it.sizeCategoryId, product.sizeCategoryId)
                set(it.colorCategoryId, product.colorCategoryId)
                set(it.userAdminID, product.userAdminId)
                set(it.productName, product.productName)
                set(it.image, product.productImageUrl)
                set(it.updatedAt, LocalDateTime.now())
                set(it.deleted, false)
                where {
                    it.id eq id
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

    override suspend fun saveAllProduct(productList: Iterable<CeramicProductInfo>) = withContext(Dispatchers.IO) {
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
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString(),
                deleted = deleted

            )
        }
    }

    private fun rowToProductResponse(row: QueryRowSet?): ProductResponse? {
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

    private fun rowToProductDto(row: QueryRowSet?): ProductDto? {
        return if (row == null) {
            null
        } else {

            val id = row[ProductEntity.id] ?: -1
            val adminUserName = row[AdminUserEntity.username] ?: ""
            val typeCategory = row[TypeCategoryEntity.typeName] ?: ""
            val size = row[SizeCategoryEntity.size] ?: ""
            val color = row[ColorCategoryEntity.color] ?: ""
            val productName = row[ProductEntity.productName] ?: ""

            val imageProduct = row[ProductEntity.image] ?: ""
            val createdAt = row[ProductEntity.createdAt] ?: ""
            val updatedAt = row[ProductEntity.updatedAt] ?: ""
            val deleted = row[ProductEntity.deleted] ?: false

            ProductDto(
                id = id,
                adminUserName = adminUserName,
                typeCategoryName = typeCategory,
                sizeCategoryName = size,
                colorCategoryName = color,
                productName = productName,
                image = imageProduct,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString(),
                deleted = deleted

            )
        }
    }

}