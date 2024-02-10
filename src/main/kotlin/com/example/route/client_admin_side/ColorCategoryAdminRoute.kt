package com.example.route.client_admin_side

import com.example.data.gallery.categories.color.ColorCategoryDataSource
import com.example.mapper.toEntity
import com.example.models.MyResponsePageable
import com.example.models.options.getColorCategoryOptions
import com.example.models.request.categories.ColorCategoryRequest
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val CATEGORIES = "${ADMIN_CLIENT}/categories"
private const val CATEGORY = "$ADMIN_CLIENT/category"
private const val COLOR_CATEGORIES = "$CATEGORIES/color"
private const val COLOR_CATEGORIES_PAGEABLE = "$COLOR_CATEGORIES-pageable"

private const val COLOR_CATEGORY = "$CATEGORY/color"
private const val CREATE_COLOR_CATEGORY = "$COLOR_CATEGORY/create"
private const val UPDATE_COLOR_CATEGORY = "$COLOR_CATEGORY/update"
private const val DELETE_COLOR_CATEGORY = "$COLOR_CATEGORY/delete"

private val logger = KotlinLogging.logger { }
fun Route.colorCategoryAdminRoute() {
    val colorCategoryDataSource: ColorCategoryDataSource by inject()

    authenticate {

        //post color category //api/v1/admin-client/category/color/create
        post(CREATE_COLOR_CATEGORY) {
            logger.debug { "POST /$CREATE_COLOR_CATEGORY" }
            val colorCategoryRequest = call.receive<ColorCategoryRequest>()
            val userId = extractAdminId()
            val createdCategory = colorCategoryDataSource
                .addColorCategory(
                    colorCategoryRequest.toEntity(userId)
                )
            respondWithSuccessfullyResult(
                statusCode = HttpStatusCode.OK,
                result = createdCategory,
                message = "Color Category inserted successfully ."
            )

        }
        //get all color category //api/v1/admin-client/categories/color
        get(COLOR_CATEGORIES) {
            try {
                logger.debug { "GET ALL /$COLOR_CATEGORIES" }

                val categoriesList = colorCategoryDataSource.getAllColorCategoryDto()
                if (categoriesList.isEmpty()) throw NotFoundException("categories is empty")
                respondWithSuccessfullyResult(
                    message = "get all categories successfully",
                    result = categoriesList
                )

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred")
            }
        }
        //get all color category //api/v1/admin-client/categories/color-pageable
        get(COLOR_CATEGORIES_PAGEABLE) {
            logger.debug { "GET ALL /$COLOR_CATEGORIES_PAGEABLE" }

            try {
                // QueryParams: type categories?page=1&perPage=10
                val params = call.request.queryParameters
                val categoryOption = getColorCategoryOptions(params)
                val colorCategoriesList =
                    colorCategoryDataSource
                        .getAllColorCategoryPageable(
                            query = categoryOption.query,
                            byColor = categoryOption.byColor,
                            page = categoryOption.page!!,
                            perPage = categoryOption.perPage!!,
                            sortField = categoryOption.sortFiled!!,
                            sortDirection = categoryOption.sortDirection!!
                        )
                if (colorCategoriesList.isEmpty()) throw NotFoundException("color categories is empty")
                val numberOfCategories = colorCategoryDataSource.getNumberOfCategories()
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = MyResponsePageable(
                        page = categoryOption.page + 1,
                        perPage = numberOfCategories,
                        data = colorCategoriesList
                    ),
                    message = "get all color categories successfully"
                )

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred ")
            }
        }
        //get color category //api/v1/admin-client/category/color/{id}
        get("$COLOR_CATEGORY/{id}") {
            try {
                logger.debug { "get /$COLOR_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()

                id?.let {
                    colorCategoryDataSource.getColorCategoryByIdDto(it)?.let { typeCategory ->

                        respondWithSuccessfullyResult(
                            statusCode = HttpStatusCode.OK,
                            result = typeCategory,
                            message = "color category is found ."
                        )
                    } ?: throw NotFoundException("no color category found .")


                } ?: throw MissingParameterException("Missing parameters .")


            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred  ")

            }
        }
        //put color category //api/v1/admin-client/category/color/update/{id}
        put("$UPDATE_COLOR_CATEGORY/{id}") {
            try {
                logger.debug { "get /$UPDATE_COLOR_CATEGORY/{id}" }

                val colorCategoryRequest = call.receive<ColorCategoryRequest>()
                val userId = extractAdminId()
                call.parameters["id"]?.toIntOrNull()?.let { colorId ->
                    val checkCategory =
                        colorCategoryDataSource.getColorCategoryByNameDto(colorCategoryRequest.colorName)
                    if (checkCategory != null) {
                        throw AlreadyExistsException("that name (${colorCategoryRequest.colorName}) is already found ")
                    }

                    val updateResult = colorCategoryDataSource
                        .updateColorCategory(colorId, colorCategoryRequest.toEntity(userId))
                    if (updateResult > 0) {
                        logger.debug { "category info save successfully in db" }

                        val updatedCategory =
                            colorCategoryDataSource.getColorCategoryByNameDto(colorCategoryRequest.colorName)
                                ?: throw NotFoundException("category name (${colorCategoryRequest.colorName}) is not found ")

                        respondWithSuccessfullyResult(
                            result = updatedCategory,
                            message = "color category updated successfully ."
                        )
                    }else{
                        throw UnknownErrorException("update failed .")

                    }



                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")

            }
        }
        //delete color category //api/v1/admin-client/category/color/delete/{id}
        delete("$DELETE_COLOR_CATEGORY/{id}") {
            try {
                logger.debug { "get /$DELETE_COLOR_CATEGORY/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let {
                    colorCategoryDataSource.deleteColorCategory(it)
                    respondWithSuccessfullyResult(
                        result = true,
                        message = "color category deleted successfully ."
                    )


                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")

            }
        }


    }

}