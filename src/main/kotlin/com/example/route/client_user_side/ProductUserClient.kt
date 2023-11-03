package com.example.route.client_user_side

import com.example.data.gallery.products.ProductDataSource
import com.example.database.table.ProductEntity
import com.example.models.ProductPage

import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

const val ALL_PRODUCTS = "${USER_CLIENT}/products"
const val SEARCH_PRODUCTS = "${ALL_PRODUCTS}/search"
private val logger = KotlinLogging.logger {}


fun Route.productUserRoute(productDataSource: ProductDataSource) {
    // get the products --> get /api/v1/user-client/products/
    get(ALL_PRODUCTS) {
        call.request.queryParameters["page"]?.toIntOrNull()?.let {
            val page = if (it > 0) it else 0
            val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10
            val type = call.request.queryParameters["type"]?.toIntOrNull() ?: -1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: -1
            val color = call.request.queryParameters["color"]?.toIntOrNull() ?: -1
            val sortField = when (call.request.queryParameters["sort_by"] ?: "date") {
                "name" -> ProductEntity.productName
                "date" -> ProductEntity.createdAt
                else -> {
                    return@get call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = MyResponse(
                            success = false,
                            message = "invalid parameter for sort_by chose between (name & date)",
                            data = null
                        )
                    )
                }
            }
            val sortDirection = when (call.request.queryParameters["sort_direction"] ?: "asc") {
                "dec" -> -1
                "asc" -> 1
                else -> {
                    return@get call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = MyResponse(
                            success = false,
                            message = "invalid parameter for sort_direction chose between (dec & asc)",
                            data = null
                        )
                    )
                }
            }

            logger.debug { "GET ALL /$ALL_PRODUCTS?page=$page&perPage=$perPage" }
            val productList = try {
                    productDataSource.getAllProductPageable(page = page, perPage = perPage)
//                productDataSource.getAllProductPageableByCategories(
//                    page = page, perPage = perPage,
//                    categoryType = type,
//                    categorySize = size,
//                    categoryColor = color,
//                    sortField = sortField,
//                    sortDirection = sortDirection
//                )
            } catch (exc: Exception) {
                logger.error { "GET ALL /${exc.message}" }

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
            if (productList.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.OK, MyResponse(
                        success = true,
                        message = "get all type categories successfully",
                        data = ProductPage(page = page, perPage = perPage, data = productList)

                    )
                )


            } else {
                call.respond(
                    HttpStatusCode.NotFound, MyResponse(
                        success = false,
                        message = "type categories is empty",
                        data = null
                    )
                )
            }
        } ?: run {
            val type = call.request.queryParameters["type"]?.toIntOrNull() ?: -1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: -1
            val color = call.request.queryParameters["color"]?.toIntOrNull() ?: -1
            val sortField = when (call.request.queryParameters["sort_by"] ?: "name") {
                "name" -> ProductEntity.productName
                "date" -> ProductEntity.createdAt
                else -> {
                    return@get call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = MyResponse(
                            success = false,
                            message = "invalid parameter for sort_by chose between (name & date)",
                            data = null
                        )
                    )
                }
            }
            val sortDirection = when (call.request.queryParameters["sort_direction"] ?: "asc") {
                "dec" -> -1
                "asc" -> 1
                else -> {
                    return@get call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = MyResponse(
                            success = false,
                            message = "invalid parameter for sort_direction chose between (dec & asc)",
                            data = null
                        )
                    )
                }
            }
            logger.debug { "GET ALL /$ALL_PRODUCTS" }

            val productList = try {
//                    productDataSource.getAllProduct()
                productDataSource.getAllProductByCategories(
                    categoryType = type,
                    categorySize = size,
                    categoryColor = color,
                    sortField = sortField,
                    sortDirection = sortDirection
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
            if (productList.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.OK, MyResponse(
                        success = true,
                        message = "get all type categories successfully",
                        data = productList
                    )
                )


            } else {
                call.respond(
                    HttpStatusCode.NotFound, MyResponse(
                        success = false,
                        message = "type categories is empty",
                        data = null
                    )
                )
            }
        }


    }
    // get the products --> get /api/v1/user-client/products/search
    get(SEARCH_PRODUCTS) {
        logger.debug { "GET ALL /$SEARCH_PRODUCTS" }

        call.request.queryParameters["product_name"]?.let { name ->
            val productList = productDataSource.searchProductByName(productName = name)
            if (productList.isNotEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(success = true, message = "Fetch Product successfully", data = productList)
                )

            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(success = false, message = "Product Not Found", data = null)
                )
            }

        } ?: call.respond(
            status = HttpStatusCode.BadRequest,
            message = MyResponse(success = false, message = "Missing Some Fields", data = null)
        )

    }

}