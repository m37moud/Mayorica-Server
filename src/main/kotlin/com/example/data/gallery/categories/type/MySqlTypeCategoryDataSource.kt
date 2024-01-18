package com.example.data.gallery.categories.type

import com.example.database.table.*
import com.example.models.TypeCategory
import com.example.models.TypeCategoryInfo
import com.example.models.dto.TypeCategoryDto
import com.example.models.dto.TypeCategoryMenu
import com.example.utils.AlreadyExistsException
import com.example.utils.ErrorException
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
class MySqlTypeCategoryDataSource(private val db: Database) : TypeCategoryDataSource {
    override suspend fun getAllTypeCategory(): List<TypeCategory> {
        logger.debug { "getAllTypeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(TypeCategoryEntity)
                .select()
                .mapNotNull { rowToTypeCategory(it) }
            typeCategoriesList
        }
    }

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

    override suspend fun getNumberOfCategories(): Int {
        logger.debug { "getAllTypeCategory" }

        return withContext(Dispatchers.IO) {
            val typeCategoriesList = db.from(TypeCategoryEntity)
                .select()
                .mapNotNull { rowToTypeCategory(it) }
            typeCategoriesList.size
        }
    }

    override suspend fun getAllTypeCategoryPageable(page: Int, perPage: Int): List<TypeCategory> {
        logger.debug { "getAllTypeCategoryPageable: $page, $perPage" }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val typeCategoriesList = db.from(TypeCategoryEntity)
                .select()
                .limit(myLimit)
                .offset(myOffset)
                .mapNotNull { rowToTypeCategory(it) }
            typeCategoriesList
        }
    }

    override suspend fun getAllTypeCategoryPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<TypeCategoryDto> {
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val typeCategoriesList = db.from(TypeCategoryEntity)
                .innerJoin(AdminUserEntity, on = TypeCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    TypeCategoryEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    TypeCategoryEntity.typeIcon,
                    TypeCategoryEntity.createdAt,
                    TypeCategoryEntity.updatedAt,
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
                        it += (TypeCategoryEntity.typeName like "%${query}%")
                    }

                }
                .mapNotNull { rowToTypeCategoryDto(it) }
            typeCategoriesList
        }

    }


    override suspend fun getTypeCategoryById(categoryTypeId: Int): TypeCategory? {
        logger.debug { "getTypeCategoryById: $categoryTypeId" }

        return withContext(Dispatchers.IO) {
            val typeCategory = db.from(TypeCategoryEntity)
                .select()
                .where { TypeCategoryEntity.id eq categoryTypeId }
                .map { rowToTypeCategory(it) }
                .firstOrNull()
            typeCategory
        }
    }

    override suspend fun getTypeCategoryByIdDto(categoryTypeId: Int): TypeCategoryDto? {
        logger.debug { "getTypeCategoryByIdDto: $categoryTypeId" }

        return withContext(Dispatchers.IO) {
            val typeCategory = db.from(TypeCategoryEntity)
                .innerJoin(AdminUserEntity, on = TypeCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    TypeCategoryEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    TypeCategoryEntity.typeIcon,
                    TypeCategoryEntity.createdAt,
                    TypeCategoryEntity.updatedAt,
                )
                .where { TypeCategoryEntity.id eq categoryTypeId }
                .map { rowToTypeCategoryDto(it) }
                .firstOrNull()
            typeCategory
        }
    }

    override suspend fun getTypeCategoryByName(categoryName: String): TypeCategory? {
        logger.debug { "getTypeCategoryByName: $categoryName" }

        return withContext(Dispatchers.IO) {
            val typeCategory = db.from(TypeCategoryEntity)
                .select()
                .where { TypeCategoryEntity.typeName eq categoryName }
                .map { rowToTypeCategory(it) }
                .firstOrNull()
            typeCategory
        }
    }

    override suspend fun getTypeCategoryByNameDto(categoryName: String): TypeCategoryDto? {
        logger.debug { "getTypeCategoryByName: $categoryName" }

        return withContext(Dispatchers.IO) {
            val typeCategory = db.from(TypeCategoryEntity)
                .innerJoin(AdminUserEntity, on = TypeCategoryEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    TypeCategoryEntity.id,
                    AdminUserEntity.username,
                    TypeCategoryEntity.typeName,
                    TypeCategoryEntity.typeIcon,
                    TypeCategoryEntity.createdAt,
                    TypeCategoryEntity.updatedAt,
                )
                .where { TypeCategoryEntity.typeName eq categoryName }
                .map { rowToTypeCategoryDto(it) }
                .firstOrNull()
            typeCategory
        }
    }

    override suspend fun addTypeCategory(typeCategory: TypeCategoryInfo): TypeCategoryDto {
        if (getTypeCategoryByName(typeCategory.typeName) != null) throw AlreadyExistsException("Type Category inserted before .")
        if (createTypeCategory(typeCategory) < 0) throw ErrorException("Failed to create New Type Category .")
        return getTypeCategoryByNameDto(typeCategory.typeName)
            ?: throw ErrorException("Type Category inserted failed .")
    }

    override suspend fun createTypeCategory(typeCategory: TypeCategoryInfo): Int {
        logger.debug { "createTypeCategory: $typeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.insert(TypeCategoryEntity) {
                set(it.typeName, typeCategory.typeName)
                set(it.typeIcon, typeCategory.iconUrl)
                set(it.userAdminID, typeCategory.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateTypeCategory(id: Int, typeInfo: TypeCategoryInfo): Int {
        logger.debug { "updateTypeCategory: $typeInfo" }

        return withContext(Dispatchers.IO) {
//            if (getTypeCategoryByName(typeInfo.typeName) != null)
//                throw AlreadyExistsException("that name (${typeInfo.typeName}) is already found ")

            val result = db.update(TypeCategoryEntity) {
                set(it.typeName, typeInfo.typeName)
                set(it.typeIcon, typeInfo.iconUrl)
                set(it.userAdminID, typeInfo.userAdminId)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq id
                }
            }
            result
        }
    }

    override suspend fun deleteTypeCategory(categoryTypeId: Int): Int {
        logger.debug { "deleteTypeCategory: $categoryTypeId" }

        return withContext(Dispatchers.IO) {
            val result = db.delete(TypeCategoryEntity) {
                it.id eq categoryTypeId
            }
            result
        }
    }

    override suspend fun deleteAllTypeCategory(): Int {
        logger.debug { "deleteAllTypeCategory" }

        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(TypeCategoryEntity)
            result
        }
    }


    override suspend fun saveAllTypeCategory(typeCategories: Iterable<TypeCategoryInfo>): Int {
        logger.debug { "saveAllTypeCategory: $typeCategories" }
        return withContext(Dispatchers.IO) {
            var result = 0
            typeCategories.forEach {
                result = createTypeCategory(it)
            }
            result
        }

    }


    private fun rowToTypeCategory(row: QueryRowSet?): TypeCategory? {
        return if (row == null)
            null
        else {
            val id = row[TypeCategoryEntity.id] ?: -1
            val typeName = row[TypeCategoryEntity.typeName] ?: ""
            val typeIcon = row[TypeCategoryEntity.typeIcon] ?: ""
            val userAdminID = row[TypeCategoryEntity.userAdminID] ?: -1
            val createdAt = row[TypeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[TypeCategoryEntity.updatedAt] ?: ""

            TypeCategory(
                id = id,
                typeName = typeName,
                typeIcon = typeIcon,
                userAdminID = userAdminID,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }

    private fun rowToTypeCategoryDto(row: QueryRowSet?): TypeCategoryDto? {
        return if (row == null)
            null
        else {
            val id = row[TypeCategoryEntity.id] ?: -1
            val adminUserName = row[AdminUserEntity.username] ?: ""
            val name = row[TypeCategoryEntity.typeName] ?: ""
            val icon = row[TypeCategoryEntity.typeIcon] ?: ""
            val createdAt = row[TypeCategoryEntity.createdAt] ?: ""
            val updatedAt = row[TypeCategoryEntity.updatedAt] ?: ""

            TypeCategoryDto(
                id = id,
                adminUserName = adminUserName,
                typeName = name,
                typeIcon = icon,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }


}