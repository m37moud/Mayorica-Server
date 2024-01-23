package com.example.route.client_admin_side

import com.example.data.gallery.categories.color.ColorCategoryDataSource
import com.example.mapper.toEntity
import com.example.models.*
import com.example.models.request.categories.ColorCategoryRequest
import com.example.service.storage.StorageService
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

private const val CATEGORIES = "$ADMIN_CLIENT/categories"
private const val COLOR_CATEGORIES = "$CATEGORIES/color"
private const val CATEGORY = "$ADMIN_CLIENT/category"
private const val COLOR_CATEGORY = "$CATEGORY/color"
private const val CREATE_COLOR_CATEGORY = "$COLOR_CATEGORY/create"
private const val UPDATE_COLOR_CATEGORY = "$COLOR_CATEGORY/update"
private const val DELETE_COLOR_CATEGORY = "$COLOR_CATEGORY/delete"

private val logger = KotlinLogging.logger {}

fun Route.categoriesAdminRoute() {
    val colorCategoryDataSource: ColorCategoryDataSource by inject()
    val storageService: StorageService by inject()
    val imageValidator: ImageValidator by inject()

//    authenticate {
//        /**
//         * create new category
//         */
//        //post color category //api/v1/admin-client/category/color/create
//        post(CREATE_COLOR_CATEGORY) {
//            logger.debug { "POST /$CREATE_COLOR_CATEGORY" }
//
//            val colorCategoryRequest = try {
//                call.receive<ColorCategoryRequest>()
//            } catch (e: Exception) {
//                call.respond(
//                    HttpStatusCode.Conflict,
//                    MyResponse(
//                        success = false,
//                        message = "Missing Some Fields",
//                        data = null
//                    )
//                )
//                return@post
//            }
//            val principal = call.principal<JWTPrincipal>()
//            val userId = try {
//                principal?.getClaim("userId", String::class)?.toIntOrNull()
//            } catch (e: Exception) {
//                call.respond(
//                    HttpStatusCode.Conflict,
//                    MyResponse(
//                        success = false,
//                        message = e.message ?: "Failed ",
//                        data = null
//                    )
//                )
//                return@post
//            }
//
//            try {
//                val typeCategory = colorCategoryDataSource.getColorCategoryByName(colorCategoryRequest.colorName)
//                if (typeCategory == null) {
//
//                    val result =
//                        colorCategoryDataSource.createColorCategory(colorCategoryRequest.toEntity(userId!!))
//
//                    if (result > 0) {
//                        call.respond(
//                            HttpStatusCode.OK, MyResponse(
//                                success = true,
//                                message = "Color Category inserted successfully .",
//                                data = colorCategoryRequest.colorName
//                            )
//                        )
//                        return@post
//                    } else {
//                        call.respond(
//                            HttpStatusCode.OK, MyResponse(
//                                success = false,
//                                message = "Color Category inserted failed .",
//                                data = null
//                            )
//                        )
//                        return@post
//                    }
//                } else {
//                    call.respond(
//                        HttpStatusCode.OK, MyResponse(
//                            success = false,
//                            message = "Color Category inserted before .",
//                            data = null
//                        )
//                    )
//                    return@post
//                }
//            } catch (exc: Exception) {
//                call.respond(
//                    HttpStatusCode.Conflict,
//                    MyResponse(
//                        success = false,
//                        message = exc.message ?: "Creation Failed .",
//                        data = null
//                    )
//                )
//                return@post
//            }
//
//
//        }
//
//        /**
//         * get all type categories
//         */
//        //get all type category //api/v1/admin-client/categories/color
//        get(COLOR_CATEGORIES) {
//            try {
//                // QueryParams: type categories?page=1&perPage=10
//                call.request.queryParameters["page"]?.toIntOrNull()?.let {
//                    val page = if (it > 0) it else 0
//                    val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10
//
//                    logger.debug { "GET ALL /$COLOR_CATEGORIES?page=$page&perPage=$perPage" }
//
//                    val colorCategoriesList = colorCategoryDataSource.getAllColorCategoryPageable(page, perPage)
//                    if (colorCategoriesList.isNotEmpty()) {
//                        call.respond(
//                            HttpStatusCode.OK, MyResponse(
//                                success = true,
//                                message = "get all size categories successfully",
//                                data = ColorCategoryPage(page, perPage, colorCategoriesList)
//                            )
//                        )
//                    } else {
//                        call.respond(
//                            HttpStatusCode.NotFound, MyResponse(
//                                success = false,
//                                message = "no color categories is found",
//                                data = null
//                            )
//                        )
//                    }
//
//                } ?: run {
//                    logger.debug { "GET ALL /$COLOR_CATEGORIES" }
//
//                    val typeCategoriesList = colorCategoryDataSource.getAllColorCategory()
//                    if (typeCategoriesList.isNotEmpty()) {
//                        call.respond(
//                            HttpStatusCode.OK, MyResponse(
//                                success = true,
//                                message = "get all color categories successfully",
//                                data = typeCategoriesList
//                            )
//                        )
//                    } else {
//                        call.respond(
//                            HttpStatusCode.NotFound, MyResponse(
//                                success = false,
//                                message = "no color categories is found",
//                                data = null
//                            )
//                        )
//                    }
//                }
//            } catch (exc: Exception) {
//                call.respond(
//                    HttpStatusCode.Conflict,
//                    MyResponse(
//                        success = false,
//                        message = exc.message ?: "Failed ",
//                        data = null
//                    )
//                )
//                return@get
//            }
//        }
//
//        /**
//         * get category by id
//         */
//        //get color category //api/v1/admin-client/category/color/{id}
//        get("$COLOR_CATEGORY/{id}") {
//            try {
//                logger.debug { "get /$COLOR_CATEGORY/{id}" }
//                val id = call.parameters["id"]?.toIntOrNull()
//                id?.let {
//                    colorCategoryDataSource.getColorCategoryById(it)?.let { colorCategory ->
//                        call.respond(
//                            HttpStatusCode.OK,
//                            MyResponse(
//                                success = true,
//                                message = "get color category successfully .",
//                                data = colorCategory
//                            )
//                        )
//                    } ?: call.respond(
//                        HttpStatusCode.NotFound,
//                        MyResponse(
//                            success = false,
//                            message = "no color category found .",
//                            data = null
//                        )
//                    )
//
//                } ?: call.respond(
//                    HttpStatusCode.BadRequest,
//                    MyResponse(
//                        success = false,
//                        message = "Missing parameters .",
//                        data = null
//                    )
//                )
//
//            } catch (exc: Exception) {
//                call.respond(
//                    HttpStatusCode.Conflict,
//                    MyResponse(
//                        success = false,
//                        message = exc.message ?: "Failed ",
//                        data = null
//                    )
//                )
//                return@get
//            }
//        }
//
//        /**
//         * delete category by id
//         */
//        //delete color category //api/v1/admin-client/category/color/delete/{id}
//        delete("$DELETE_COLOR_CATEGORY/{id}") {
//            try {
//                logger.debug { "get /$DELETE_COLOR_CATEGORY/{id}" }
//                val id = call.parameters["id"]?.toIntOrNull()
//                id?.let {
//                    val deleteResult = colorCategoryDataSource.deleteColorCategory(it)
//                    if (deleteResult > 0) {
//                        call.respond(
//                            HttpStatusCode.OK,
//                            MyResponse(
//                                success = true,
//                                message = "color category deleted successfully .",
//                                data = null
//                            )
//                        )
//                    } else {
//                        call.respond(
//                            HttpStatusCode.NotFound,
//                            MyResponse(
//                                success = false,
//                                message = "color category deleted failed .",
//                                data = null
//                            )
//                        )
//                    }
//                } ?: call.respond(
//                    HttpStatusCode.BadRequest,
//                    MyResponse(
//                        success = false,
//                        message = "Missing parameters .",
//                        data = null
//                    )
//                )
//
//            } catch (exc: Exception) {
//                call.respond(
//                    HttpStatusCode.Conflict,
//                    MyResponse(
//                        success = false,
//                        message = exc.message ?: "Failed ",
//                        data = null
//                    )
//                )
//                return@delete
//            }
//        }
//
//
//        /**
//         * update
//         */
//        //put color category //api/v1/admin-client/category/color/update/{id}
//        put("$UPDATE_COLOR_CATEGORY/{id}") {
//            try {
//                logger.debug { "get /$UPDATE_COLOR_CATEGORY/{id}" }
//                val id = call.parameters["id"]?.toIntOrNull()
//                id?.let {
//                    val colorCategory = try {
//                        call.receive<ColorCategory>()
//                    } catch (exc: Exception) {
//                        call.respond(
//                            HttpStatusCode.Conflict,
//                            MyResponse(
//                                success = false,
//                                message = exc.message ?: "update Failed .",
//                                data = null
//                            )
//                        )
//                        return@put
//                    }
//                    colorCategoryDataSource.getColorCategoryById(it)?.let { temp ->
//                        val newColorCategory = colorCategory.copy(
//                            createdAt = temp.createdAt,
//                            updatedAt = LocalDateTime.now().toDatabaseString()
//                        )
//                        val updateResult = colorCategoryDataSource.updateColorCategory(newColorCategory)
//                        if (updateResult > 0) {
//                            call.respond(
//                                HttpStatusCode.OK,
//                                MyResponse(
//                                    success = true,
//                                    message = "color category updated successfully .",
//                                    data = null
//                                )
//                            )
//                        } else {
//                            call.respond(
//                                HttpStatusCode.OK,
//                                MyResponse(
//                                    success = false,
//                                    message = " color category updated failed .",
//                                    data = null
//                                )
//                            )
//                        }
//                    } ?: call.respond(
//                        HttpStatusCode.NotFound,
//                        MyResponse(
//                            success = false,
//                            message = "no color category found .",
//                            data = null
//                        )
//                    )
//
//                } ?: call.respond(
//                    HttpStatusCode.BadRequest,
//                    MyResponse(
//                        success = false,
//                        message = "Missing parameters .",
//                        data = null
//                    )
//                )
//
//            } catch (exc: Exception) {
//                call.respond(
//                    HttpStatusCode.Conflict,
//                    MyResponse(
//                        success = false,
//                        message = exc.message ?: "Failed ",
//                        data = null
//                    )
//                )
//                return@put
//            }
//        }
//
//
//    }
}