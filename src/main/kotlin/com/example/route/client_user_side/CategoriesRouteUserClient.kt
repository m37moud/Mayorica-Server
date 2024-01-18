package com.example.route.client_user_side

import com.example.data.gallery.categories.type.TypeCategoryDataSource
import com.example.data.gallery.categories.color.ColorCategoryDataSource
import com.example.data.gallery.categories.size.SizeCategoryDataSource
import com.example.models.ColorCategoryPage
import com.example.models.SizeCategoryPage
import com.example.models.TypeCategoryPage
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val CATEGORIES = "${USER_CLIENT}/categories"
private const val TYPE_CATEGORIES = "${CATEGORIES}/type"
private const val SIZE_CATEGORIES = "${CATEGORIES}/size"
private const val COLOR_CATEGORIES = "${CATEGORIES}/color"
private const val CATEGORY = "${USER_CLIENT}/category"
private const val SIZE_CATEGORY = "${CATEGORY}/size"


private val logger = KotlinLogging.logger {}

fun Route.categoriesUserRoute(
//    categoryDataSource: CategoryDataSource,
) {
    val typeCategoryDataSource: TypeCategoryDataSource by inject()
    val sizeCategoryDataSource: SizeCategoryDataSource by inject()
    val colorCategoryDataSource: ColorCategoryDataSource by inject()
    /**
     * get all categories
     */
    //get all type category //api/v1/admin-client/categories/type
    get(TYPE_CATEGORIES) {
        try {
            // QueryParams: type categories?page=1&perPage=10
            call.request.queryParameters["page"]?.toIntOrNull()?.let {
                val page = if (it > 0) it else 0
                val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                logger.debug { "GET ALL /$TYPE_CATEGORIES?page=$page&perPage=$perPage" }

                val typeCategoriesList = typeCategoryDataSource.getAllTypeCategoryPageable(page, perPage)
                if (typeCategoriesList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = true,
                            message = "get all type categories successfully",
                            data = TypeCategoryPage(page, perPage, typeCategoriesList)
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "type categories is empty",
                            data = null
                        )
                    )
                }

            } ?: run {
                logger.debug { "GET ALL /$TYPE_CATEGORIES" }

                val typeCategoriesList = typeCategoryDataSource.getAllTypeCategory()
                if (typeCategoriesList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = true,
                            message = "get all type categories successfully",
                            data = typeCategoriesList
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "type categories is empty",
                            data = null
                        )
                    )
                }
            }
        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Failed ",
                    data = null
                )
            )
            return@get
        }
    }
    //get all size category //api/v1/admin-client/categories/size
    get(SIZE_CATEGORIES) {
        try {
            // QueryParams: type categories?page=1&perPage=10
            call.request.queryParameters["page"]?.toIntOrNull()?.let {
                val page = if (it > 0) it else 0
                val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                logger.debug { "GET ALL /$SIZE_CATEGORIES?page=$page&perPage=$perPage" }

                val sizeCategoriesList = sizeCategoryDataSource.getAllSizeCategoryPageable(page, perPage)
                if (sizeCategoriesList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "get all size categories successfully",
                            data = SizeCategoryPage(page, perPage, sizeCategoriesList)
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "no size categories is found",
                            data = null
                        )
                    )
                }

            } ?: run {
                logger.debug { "GET ALL /$SIZE_CATEGORIES" }

                val typeCategoriesList = sizeCategoryDataSource.getAllSizeCategory()
                if (typeCategoriesList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = true,
                            message = "get all size categories successfully",
                            data = typeCategoriesList
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "size categories is not found",
                            data = null
                        )
                    )
                }
            }
        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Failed ",
                    data = null
                )
            )
            return@get
        }
    }
    //get all type category //api/v1/admin-client/categories/color
    get(COLOR_CATEGORIES) {
        try {
            // QueryParams: type categories?page=1&perPage=10
            call.request.queryParameters["page"]?.toIntOrNull()?.let {
                val page = if (it > 0) it else 0
                val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                logger.debug { "GET ALL /$TYPE_CATEGORIES?page=$page&perPage=$perPage" }

                val colorCategoriesList = colorCategoryDataSource.getAllColorCategoryPageable(page, perPage)
                if (colorCategoriesList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = true,
                            message = "get all size categories successfully",
                            data = ColorCategoryPage(page, perPage, colorCategoriesList)
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "no color categories is found",
                            data = null
                        )
                    )
                }

            } ?: run {
                logger.debug { "GET ALL /$TYPE_CATEGORIES" }

                val typeCategoriesList = typeCategoryDataSource.getAllTypeCategory()
                if (typeCategoriesList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = true,
                            message = "get all color categories successfully",
                            data = typeCategoriesList
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "no color categories is found",
                            data = null
                        )
                    )
                }
            }
        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Failed ",
                    data = null
                )
            )
            return@get
        }
    }

    //get size category //api/v1/admin-client/category/size/{id}
    get(SIZE_CATEGORY) {
        try {
            val id = call.request.queryParameters["typeCategoryId"]!!.toIntOrNull()
            id?.let {
                logger.debug { "get /$SIZE_CATEGORY?typeCategoryId=$it" }

                val sizeCategoriesList = sizeCategoryDataSource.getAllSizeCategoryByTypeId(it)
                if (sizeCategoriesList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "get size category successfully .",
                            data = sizeCategoriesList
                        )
                    )
                } else
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "no size category found .",
                            data = null
                        )
                    )

            } ?: call.respond(
                HttpStatusCode.BadRequest,
                MyResponse(
                    success = false,
                    message = "Missing parameters .",
                    data = null
                )
            )

        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Failed ",
                    data = null
                )
            )
            return@get
        }
    }

}