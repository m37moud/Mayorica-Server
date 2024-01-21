package com.example.route.client_admin_side

import com.example.data.gallery.categories.size.SizeCategoryDataSource
import com.example.mapper.toEntity
import com.example.models.MyResponsePageable
import com.example.models.dto.SizeCategoryCreateDto
import com.example.models.options.getSizeCategoryOptions
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
private const val CATEGORY = "$ADMIN_CLIENT/category"
private const val SIZE_CATEGORIES = "$CATEGORIES/size"
private const val SIZE_CATEGORIES_TYPE_MENU = "$SIZE_CATEGORIES/typeMenu"
private const val SIZE_CATEGORIES_PAGEABLE = "$SIZE_CATEGORIES-pageable"

private const val SIZE_CATEGORY = "$CATEGORY/size"
private const val CREATE_SIZE_CATEGORY = "$SIZE_CATEGORY/create"
private const val UPDATE_SIZE_CATEGORY = "$SIZE_CATEGORY/update"
private const val DELETE_SIZE_CATEGORY = "$SIZE_CATEGORY/delete"

private val logger = KotlinLogging.logger {}

fun Route.sizeCategoryAdminRoute() {
    val sizeCategoryDataSource: SizeCategoryDataSource by inject()
    val storageService: StorageService by inject()
    val imageValidator: ImageValidator by inject()
    authenticate {
        //post size category //api/v1/admin-client/category/size/create
        post(CREATE_SIZE_CATEGORY) {
            logger.debug { "POST /$CREATE_SIZE_CATEGORY" }

            try {
                val multiPart = receiveMultipart<SizeCategoryCreateDto>(imageValidator)
                val userId = extractAdminId()
                val generateNewName = generateSafeFileName(multiPart.fileName)
                val url = "${multiPart.baseUrl}categories/images/${generateNewName}"
                val imageUrl = multiPart.image?.let { img ->
                    storageService.saveCategoryImages(
                        fileName = generateNewName,
                        fileUrl = url, fileBytes = img
                    )
                }
                val sizeCategoryDto = multiPart.data.copy(sizeImageUrl = imageUrl!!)
                val createdCategory = sizeCategoryDataSource
                    .addSizeCategory(sizeCategoryDto.toEntity(userId))
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = createdCategory,
                    message = "Size Category inserted successfully ."
                )
            } catch (exc: Exception) {
                throw ErrorException(exc.message ?: "Creation Failed .")
            }


        }
        //get all size category //api/v1/admin-client/categories/size
        get(SIZE_CATEGORIES) {
            try {
                logger.debug { "GET ALL /$SIZE_CATEGORIES" }

                val sizeCategoriesList = sizeCategoryDataSource.getAllSizeCategoryDto()
                if (sizeCategoriesList.isEmpty()) throw NotFoundException("size categories is empty")
                respondWithSuccessfullyResult(
                    message = "get all size categories successfully",
                    result = sizeCategoriesList
                )

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")
            }
        }
        //get all size category //api/v1/admin-client/categories/size-pageable
        get(SIZE_CATEGORIES_PAGEABLE) {
            try {
                // QueryParams: type categories?page=1&perPage=10
                val params = call.request.queryParameters
                val categoryOption = getSizeCategoryOptions(params)
                val typeCategoriesList =
                    sizeCategoryDataSource
                        .getAllSizeCategoryPageable(
                            query = categoryOption.query,
                            page = categoryOption.page!!,
                            perPage = categoryOption.perPage!!,
                            byTypeCategoryId = categoryOption.byTypeCategoryId,
                            sortField = categoryOption.sortFiled!!,
                            sortDirection = categoryOption.sortDirection!!
                        )
                if (typeCategoriesList.isEmpty()) throw NotFoundException("size categories is empty")
                val numberOfCategories = sizeCategoryDataSource.getNumberOfCategories()
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = MyResponsePageable(
                        page = categoryOption.page + 1,
                        perPage = numberOfCategories,
                        data = typeCategoriesList
                    ),
                    message = "get all size categories successfully"
                )

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")
            }
        }
        //get size category //api/v1/admin-client/category/size/{id}
        get("$SIZE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$SIZE_CATEGORY/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let {
                    sizeCategoryDataSource.getSizeCategoryByIdDto(it)?.let { typeCategory ->

                        respondWithSuccessfullyResult(
                            statusCode = HttpStatusCode.OK,
                            result = typeCategory,
                            message = "size category is found ."
                        )
                    } ?: throw NotFoundException("no size category found .")


                } ?: throw MissingParameterException("Missing parameters .")


            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")

            }
        }
        //put size category //api/v1/admin-client/category/size/update/{id}
        put("$UPDATE_SIZE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$UPDATE_SIZE_CATEGORY/{id}" }
                val multiPart = receiveMultipart<SizeCategoryCreateDto>(imageValidator)
                val userId = extractAdminId()
                call.parameters["id"]?.toIntOrNull()?.let { typeId ->
                    val tempType = sizeCategoryDataSource.getSizeCategoryById(typeId)
                        ?: throw NotFoundException("no type category found .")
                    val newName = multiPart.data.sizeName
                    logger.debug { "check if ($newName) the new name if not repeat" }
                    val checkCategoryName = sizeCategoryDataSource.getSizeCategoryByName(newName)
                    val oldImageName = tempType.sizeImage.substringAfterLast("/")
                    val responseFileName = multiPart.fileName
                    logger.debug { "check oldImage ($oldImageName) and response (${responseFileName}) image new name if not repeat" }

                    if (checkCategoryName != null && oldImageName == multiPart.fileName) {
                        throw AlreadyExistsException("that name ($newName) is already found ")
                    }
                    /**
                     * get old image url to delete
                     */
                    logger.debug { "oldImageName is  : $oldImageName" }
                    logger.debug { "try to delete old icon from storage first extract oldImageName " }

                    storageService.deleteCategoryImages(oldImageName)

                    logger.info { "old icon is deleted successfully from storage" }
                    logger.debug { "try to save new icon in storage" }


                    val generateNewName = generateSafeFileName(responseFileName)
                    val url = "${multiPart.baseUrl}categories/icons/${generateNewName}"

                    val imageUrl = multiPart.image?.let { img ->
                        storageService.saveCategoryImages(
                            fileName = generateNewName,
                            fileUrl = url, fileBytes = img
                        )
                    }
                    val typeCategoryDto = multiPart.data.copy(sizeImageUrl = imageUrl!!)
                    logger.debug { "try to save category info in db" }

                    sizeCategoryDataSource
                        .updateSizeCategory(typeId, typeCategoryDto.toEntity(userId))
                    logger.debug { "category info save successfully in db" }

                    val updatedCategory = sizeCategoryDataSource.getSizeCategoryByNameDto(newName)
                        ?: throw NotFoundException("category name ($newName) is not found ")

                    respondWithSuccessfullyResult(
                        result = updatedCategory,
                        message = "type category updated successfully ."
                    )

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")

            }
        }
        //delete size category //api/v1/admin-client/category/size/delete/{id}
        delete("$DELETE_SIZE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$DELETE_SIZE_CATEGORY/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let {
                    sizeCategoryDataSource.getSizeCategoryById(it)?.let { sizeCategory ->
                        val oldImageName = sizeCategory.sizeImage.substringAfterLast("/")
                        logger.debug { "try to delete oldImageName $oldImageName" }

                        storageService.deleteCategoryImages(fileName = oldImageName)

                        sizeCategoryDataSource.deleteSizeCategory(it)

                        respondWithSuccessfullyResult(
                            result = true,
                            message = "size category deleted successfully ."
                        )

                    }

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")

            }
        }
        //get all size category //api/v1/admin-client/categories/size/typeMenu

        get(SIZE_CATEGORIES_TYPE_MENU) {
            logger.debug { "GET ALL /$SIZE_CATEGORIES_TYPE_MENU" }
            try {
                val typeMenu = sizeCategoryDataSource.getAllTypeCategoryMenu()
                if (typeMenu.isEmpty()) throw NotFoundException("size categories is empty")
                respondWithSuccessfullyResult(
                    message = "get all type menu categories successfully",
                    result = typeMenu
                )
            } catch (e: Exception) {
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }

        }

    }


}