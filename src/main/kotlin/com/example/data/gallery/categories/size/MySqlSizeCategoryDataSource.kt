package com.example.data.gallery.categories.size

import com.example.database.table.AdminUserEntity
import com.example.database.table.SizeCategoryEntity
import com.example.database.table.TypeCategoryEntity
import com.example.models.SizeCategory
import com.example.models.SizeCategoryInfo
import com.example.models.dto.SizeCategoryDto
import com.example.models.dto.TypeCategoryMenu
import com.example.utils.AlreadyExistsException
import com.example.utils.ErrorException
import com.example.utils.NotFoundException
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
class MySqlSizeCategoryDataSource(private val db: Database) : SizeCategoryDataSource {
    override suspend fun getAllTypeCategoryMenu(): List<TypeCategoryMenu> {
        logger.debug { "getAllTypeCategoryMenu" }
        return withContext(Dispatchers.IO) {
            val result = db.from(TypeCategoryEntity)
                .select(
                    TypeCategoryEntity.id,
                    TypeCategoryEntity.typeName
                )
                .mapNotNull {
                    TypeCategoryMenu(
                        typeId = it[TypeCategoryEntity.id] ?: -1,
                        typeName = it[TypeCategoryEntity.typeName] ?: ""
                    )
                }
            result
        }

    }

    override suspend fun getAllSizeCategory(): List<SizeCategory> {
        logger.debug { "getAllSizeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(SizeCategoryEntity)
                .select()
                .mapNotNull { rowToSizeCategory(it) }
            typeCategoriesList
        }
    }

    override suspend fun getAllSizeCategoryDto(): List<SizeCategoryDto> {
        logger.debug { "getAllSizeCategoryDto" }
        return withContext(Dispatchers.IO) {
            val result = db.from(SizeCategoryEntity)
                .innerJoin(AdminUserEntity, on = SizeCategoryEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = SizeCategoryEntity.typeCategoryId eq TypeCategoryEntity.id)
                .select(
                    SizeCategoryEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    SizeCategoryEntity.sizeImage,
                    SizeCategoryEntity.createdAt,
                    SizeCategoryEntity.updatedAt
                )
                .mapNotNull { rowToSizeCategoryDto(it) }
            result
        }

    }

    override suspend fun getNumberOfCategories(): Int {
        logger.debug { "getAllTypeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(TypeCategoryEntity)
                .select()
                .mapNotNull { rowToSizeCategory(it) }
            typeCategoriesList.size
        }
    }

    override suspend fun getAllSizeCategoryByTypeId(typeCategoryId: Int): List<SizeCategory> {
        logger.debug { "getAllSizeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(SizeCategoryEntity)
                .select()

                .where { SizeCategoryEntity.typeCategoryId eq typeCategoryId }
                .mapNotNull { rowToSizeCategory(it) }
            typeCategoriesList
        }
    }

    override suspend fun getAllSizeCategoryPageable(page: Int, perPage: Int): List<SizeCategory> {
        logger.debug { "getAllSizeCategoryPageable: $page, $perPage" }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val sizeCategoriesList = db.from(SizeCategoryEntity)
                .select()
                .limit(myLimit)
                .offset(myOffset)
                .mapNotNull { rowToSizeCategory(it) }
            sizeCategoriesList
        }
    }

    override suspend fun getAllSizeCategoryPageable(
        query: String?,
        page: Int,
        perPage: Int,
        byTypeCategoryId: Int?,
        sortField: Column<*>,
        sortDirection: Int
    ): List<SizeCategoryDto> {
        logger.debug { "getAllSizeCategoryPageable: $page, $perPage" }
        val myLimit = if (perPage > 100) 100 else perPage
        val myOffset = (page * perPage)
        return withContext(Dispatchers.IO) {
            val result = db.from(SizeCategoryEntity)
                .innerJoin(AdminUserEntity, on = SizeCategoryEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = SizeCategoryEntity.typeCategoryId eq TypeCategoryEntity.id)
                .select(
                    SizeCategoryEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    SizeCategoryEntity.sizeImage,
                    SizeCategoryEntity.createdAt,
                    SizeCategoryEntity.updatedAt
                )
                .limit(myLimit).offset(myOffset)
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                ).whereWithConditions {
                    if (!query.isNullOrEmpty()) {
                        it += (SizeCategoryEntity.size like "%${query}%")
                    }
                    if (byTypeCategoryId != null) {
                        it += (SizeCategoryEntity.typeCategoryId eq byTypeCategoryId)
                    }

                }
                .mapNotNull { rowToSizeCategoryDto(it) }
            result
        }
    }

    override suspend fun getSizeCategoryById(categorySizeId: Int): SizeCategory? {
        logger.debug { "getSizeCategoryById: $categorySizeId" }
        return withContext(Dispatchers.IO) {
            val sizeCategory = db.from(SizeCategoryEntity)
                .select()
                .where { SizeCategoryEntity.id eq categorySizeId }
                .map { rowToSizeCategory(it) }
                .firstOrNull()
            sizeCategory
        }

    }

    override suspend fun getSizeCategoryByIdDto(categorySizeId: Int): SizeCategoryDto? {
        logger.debug { "getSizeCategoryByIdDto: $categorySizeId" }
        return withContext(Dispatchers.IO) {
            val sizeCategory = db.from(SizeCategoryEntity)
                .innerJoin(AdminUserEntity, on = SizeCategoryEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = SizeCategoryEntity.typeCategoryId eq TypeCategoryEntity.id)
                .select(
                    SizeCategoryEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    SizeCategoryEntity.sizeImage,
                    SizeCategoryEntity.createdAt,
                    SizeCategoryEntity.updatedAt
                )
                .where { SizeCategoryEntity.id eq categorySizeId }
                .map { rowToSizeCategoryDto(it) }
                .firstOrNull()
            sizeCategory
        }

    }

    override suspend fun getSizeCategoryByName(categorySizeName: String): SizeCategory? {
        logger.debug { "getSizeCategoryByName: $categorySizeName" }

        return withContext(Dispatchers.IO) {
            val sizeCategory = db.from(SizeCategoryEntity)
                .select()
                .where { SizeCategoryEntity.size eq categorySizeName }
                .map { rowToSizeCategory(it) }
                .firstOrNull()
            sizeCategory
        }
    }

    override suspend fun getSizeCategoryByNameDto(categorySizeName: String): SizeCategoryDto? {
        logger.debug { "getSizeCategoryByNameDto: $categorySizeName" }

        return withContext(Dispatchers.IO) {
            val result = db.from(SizeCategoryEntity)
                .innerJoin(AdminUserEntity, on = SizeCategoryEntity.userAdminID eq AdminUserEntity.id)
                .innerJoin(TypeCategoryEntity, on = SizeCategoryEntity.typeCategoryId eq TypeCategoryEntity.id)

                .select(
                    SizeCategoryEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    SizeCategoryEntity.size,
                    SizeCategoryEntity.sizeImage,
                    SizeCategoryEntity.createdAt,
                    SizeCategoryEntity.updatedAt
                )
                .where { SizeCategoryEntity.size eq categorySizeName }

                .map { rowToSizeCategoryDto(it) }
                .firstOrNull()
            result
        }
    }

    override suspend fun addSizeCategory(sizeCategory: SizeCategoryInfo): SizeCategoryDto {
        if (getSizeCategoryByName(sizeCategory.sizeName) != null) throw AlreadyExistsException("this Category inserted before .")
        if (createSizeCategory(sizeCategory) < 0) throw ErrorException("Failed to create New Size Category .")
        return getSizeCategoryByNameDto(sizeCategory.sizeName)
            ?: throw NotFoundException("failed to get The Category after created.")
    }

    override suspend fun createSizeCategory(sizeCategory: SizeCategoryInfo): Int {
        logger.debug { "createSizeCategory: $sizeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.insert(SizeCategoryEntity) {
                set(it.typeCategoryId, sizeCategory.typeId)
                set(it.size, sizeCategory.sizeName)
                set(it.sizeImage, sizeCategory.sizeImageUrl)
                set(it.userAdminID, sizeCategory.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateSizeCategory(id: Int, sizeCategory: SizeCategoryInfo): Int {
        logger.debug { "updateSizeCategory: $sizeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.update(SizeCategoryEntity) {
                set(it.typeCategoryId, sizeCategory.typeId)
                set(it.size, sizeCategory.sizeName)
                set(it.userAdminID, sizeCategory.userAdminId)

                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq id
                }
            }
            result
        }
    }

    override suspend fun deleteSizeCategory(categorySizeId: Int): Int {
        logger.debug { "deleteSizeCategory: $categorySizeId" }

        return withContext(Dispatchers.IO) {
            val result = db.delete(SizeCategoryEntity) {
                it.id eq categorySizeId
            }
            result
        }
    }

    override suspend fun deleteAllSizeCategory(): Int {
        logger.debug { "deleteAllSizeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(SizeCategoryEntity)
            result
        }
    }

    override suspend fun saveAllSizeCategory(sizeCategories: Iterable<SizeCategoryInfo>): Int {
        logger.debug { "saveAllSizeCategory: $sizeCategories" }
        return withContext(Dispatchers.IO) {
            var result = 0
            sizeCategories.forEach {
                result = createSizeCategory(it)
            }
            result
        }
    }

    private fun rowToSizeCategory(row: QueryRowSet?): SizeCategory? {
        return if (row == null)
            null
        else {
            val id = row[SizeCategoryEntity.id] ?: -1
            val typeCategoryId = row[SizeCategoryEntity.typeCategoryId] ?: -1
            val size = row[SizeCategoryEntity.size] ?: ""
            val sizeImage = row[SizeCategoryEntity.sizeImage] ?: ""
            val userAdminID = row[SizeCategoryEntity.userAdminID] ?: -1
            val createdAt = row[SizeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[SizeCategoryEntity.updatedAt] ?: ""

            SizeCategory(
                id = id,
                typeCategoryId = typeCategoryId,
                size = size,
                sizeImage = sizeImage,
                userAdminID = userAdminID,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }

    private fun rowToSizeCategoryDto(row: QueryRowSet?): SizeCategoryDto? {
        return if (row == null)
            null
        else {
            val id = row[SizeCategoryEntity.id] ?: -1
            val adminUserName = row[AdminUserEntity.username] ?: ""
            val typeCategoryName = row[TypeCategoryEntity.typeName] ?: ""
            val size = row[SizeCategoryEntity.size] ?: ""
            val sizeImage = row[SizeCategoryEntity.sizeImage] ?: ""
            val createdAt = row[SizeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[SizeCategoryEntity.updatedAt] ?: ""

            SizeCategoryDto(
                id = id,
                adminUserName = adminUserName,
                typeCategoryName = typeCategoryName,
                size = size,
                sizeImage = sizeImage,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }
}