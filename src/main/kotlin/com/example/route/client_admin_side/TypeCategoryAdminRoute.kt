package com.example.route.client_admin_side

import com.example.data.gallery.categories.type.TypeCategoryDataSource
import com.example.mapper.toEntity
import com.example.models.MyResponsePageable
import com.example.models.dto.TypeCategoryCreateDto
import com.example.models.options.getTypeCategoryOptions
import com.example.service.storage.StorageService
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.NotFoundException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val CATEGORIES = "${ADMIN_CLIENT}/categories"
private const val TYPE_CATEGORIES = "$CATEGORIES/type"
private const val TYPE_CATEGORIES_PAGEABLE = "$TYPE_CATEGORIES-pageable"
private const val CATEGORY = "${ADMIN_CLIENT}/category"
private const val TYPE_CATEGORY = "$CATEGORY/type"
private const val CREATE_TYPE_CATEGORY = "$TYPE_CATEGORY/create"
private const val UPDATE_TYPE_CATEGORY = "$TYPE_CATEGORY/update"
private const val DELETE_TYPE_CATEGORY = "$TYPE_CATEGORY/delete"
private val logger = KotlinLogging.logger {}

fun Route.typeCategoryAdminRoute() {
    val typeCategoryDataSource: TypeCategoryDataSource by inject()
    val storageService: StorageService by inject()
    val imageValidator: ImageValidator by inject()
    authenticate {
        /**
         * create new category
         */
        //post type category //api/v1/admin-client/category/type/create
        post(CREATE_TYPE_CATEGORY) {
            logger.debug { "POST /$CREATE_TYPE_CATEGORY" }
            val multiPart = receiveMultipart<TypeCategoryCreateDto>(imageValidator)
            val userId = extractAdminId()
            val generateNewName = generateSafeFileName(multiPart.fileName)
            val url = "${multiPart.baseUrl}categories/icons/${generateNewName}"
            val imageUrl = multiPart.image?.let { img ->
                storageService.saveCategoryIcons(
                    fileName = generateNewName,
                    fileUrl = url, fileBytes = img
                )
            }
            val typeCategoryDto = multiPart.data.copy(iconUrl = imageUrl!!)
            val createdCategory = typeCategoryDataSource
                .addTypeCategory(typeCategoryDto.toEntity(userId))
            respondWithSuccessfullyResult(
                statusCode = HttpStatusCode.OK,
                result = createdCategory,
                message = "Type Category inserted successfully ."
            )


        }
        //get all type category //api/v1/admin-client/categories/type
        get(TYPE_CATEGORIES) {
            try {
                logger.debug { "GET ALL /$TYPE_CATEGORIES" }

                val typeCategoriesList = typeCategoryDataSource.getAllTypeCategory()
                if (typeCategoriesList.isEmpty()) throw NotFoundException("type categories is empty")
                respondWithSuccessfullyResult(
                    message = "get all type categories successfully",
                    result = typeCategoriesList
                )

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "Failed ")
            }
        }
        //get all type category //api/v1/admin-client/categories/type-pageable
        get(TYPE_CATEGORIES_PAGEABLE) {
            try {
                // QueryParams: type categories?page=1&perPage=10
                val params = call.request.queryParameters
                val categoryOption = getTypeCategoryOptions(params)
                val typeCategoriesList =
                    typeCategoryDataSource
                        .getAllTypeCategoryPageable(
                            query = categoryOption.query,
                            page = categoryOption.page!!,
                            perPage = categoryOption.perPage!!,
                            sortField = categoryOption.sortFiled!!,
                            sortDirection = categoryOption.sortDirection!!
                        )
                if (typeCategoriesList.isEmpty()) throw NotFoundException("type categories is empty")
                val numberOfCategories = typeCategoryDataSource.getNumberOfCategories()
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = MyResponsePageable(
                        page = categoryOption.page + 1,
                        perPage = numberOfCategories,
                        data = typeCategoriesList
                    ),
                    message = "get all type categories successfully"
                )

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "Failed ")
            }
        }

        //get type category //api/v1/admin-client/category/type/{id}
        get("$TYPE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$TYPE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()

                id?.let {
                    typeCategoryDataSource.getTypeCategoryByIdDto(it)?.let { typeCategory ->

                        respondWithSuccessfullyResult(
                            statusCode = HttpStatusCode.OK,
                            result = typeCategory,
                            message = "type category is found ."
                        )
                    } ?: throw NotFoundException("no type category found .")


                } ?: throw MissingParameterException("Missing parameters .")


            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "Failed ")

            }
        }
        //put type category //api/v1/admin-client/category/type/update/{id}
        put("$UPDATE_TYPE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$TYPE_CATEGORY/{id}" }
                val multiPart = receiveMultipart<TypeCategoryCreateDto>(imageValidator)
                val userId = extractAdminId()
                call.parameters["id"]?.toIntOrNull()?.let { typeId ->
                    val tempType = typeCategoryDataSource.getTypeCategoryById(typeId)
                        ?: throw NotFoundException("no type category found .")
                    val newName = multiPart.data.typeName
                    logger.debug { "check if ($newName) the new name if not repeat" }
                    val checkCategoryName = typeCategoryDataSource.getTypeCategoryByName(newName)
                    val oldImageName = tempType.typeIcon.substringAfterLast("/")
                    val responseFileName =multiPart.fileName
                    logger.debug { "check oldImage ($oldImageName) and response (${responseFileName}) image new name if not repeat" }

                    if (checkCategoryName != null && oldImageName == multiPart.fileName) {
                        throw AlreadyExistsException("that name ($newName) is already found ")
                    }
                    /**
                     * get old image url to delete
                     */
                    logger.debug { "oldImageName is  : $oldImageName" }
                    logger.debug { "try to delete old icon from storage first extract oldImageName " }

                    storageService.deleteCategoryIcons(oldImageName)

                    logger.info { "old icon is deleted successfully from storage" }
                    logger.debug { "try to save new icon in storage" }


                    val generateNewName = generateSafeFileName(responseFileName)
                    val url = "${multiPart.baseUrl}categories/icons/${generateNewName}"

                    val imageUrl = multiPart.image?.let { img ->
                        storageService.saveCategoryIcons(
                            fileName = generateNewName,
                            fileUrl = url,
                            fileBytes = img
                        )
                    }
                    val typeCategoryDto = multiPart.data.copy(iconUrl = imageUrl!!)
                    logger.debug { "try to save category info in db" }

                    typeCategoryDataSource
                        .updateTypeCategory(typeId, typeCategoryDto.toEntity(userId))
                    logger.debug { "category info save successfully in db" }

                    val updatedCategory = typeCategoryDataSource.getTypeCategoryByNameDto(newName)
                        ?: throw NotFoundException("category name ($newName) is not found ")

                    respondWithSuccessfullyResult(
                        result = updatedCategory,
                        message = "type category updated successfully ."
                    )

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "Failed ")

            }
        }

        //delete type category //api/v1/admin-client/category/type/delete/{id}
        delete("$DELETE_TYPE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$DELETE_TYPE_CATEGORY/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let {
                    typeCategoryDataSource.getTypeCategoryById(it)?.let { typeCategory ->
                        val oldImageName = typeCategory.typeIcon.substringAfterLast("/")
                        logger.debug { "try to delete oldImageName $oldImageName" }

                        storageService.deleteCategoryIcons(fileName = oldImageName)

                        typeCategoryDataSource.deleteTypeCategory(it)

                        respondWithSuccessfullyResult(
                            result = true,
                            message = "type category deleted successfully ."
                        )

                    }

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "Failed ")

            }
        }
    }


}