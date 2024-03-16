package com.example.route.client_user_side

import com.example.data.gallery.products.ProductDataSource
import com.example.database.table.ProductEntity
import com.example.mapper.toModelResponse
import com.example.mapper.toClientDto
import com.example.models.MyResponsePageable
import com.example.models.ProductResponsePage
import com.example.models.options.getCeramicProductOptions

import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import com.example.utils.NotFoundException
import com.example.utils.UnknownErrorException
import com.example.utils.respondWithSuccessfullyResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val ALL_PRODUCTS = "${USER_CLIENT}/products"
private const val ALL_PRODUCTS_PAGEABLE = "${ALL_PRODUCTS}-pageable"

const val ALL_PRODUCTS_RESPONSE = "${USER_CLIENT}/products-response"
const val SINGLE_PRODUCT = "${USER_CLIENT}/product"
const val SINGLE_PRODUCT_RESPONSE = "${USER_CLIENT}/product-response"
const val SEARCH_PRODUCTS = "${ALL_PRODUCTS}/search"
private val logger = KotlinLogging.logger {}


fun Route.productUserRoute(
//    productDataSource: ProductDataSource
) {
    val productDataSource: ProductDataSource by inject()

    // get the products --> get /api/v1/admin-client/products-pageable (token required)
    get(ALL_PRODUCTS_PAGEABLE) {
        logger.debug { "GET ALL /${ALL_PRODUCTS_PAGEABLE}" }

        try {
            val params = call.request.queryParameters
            val ceramicOption = getCeramicProductOptions(params)
            val ceramicList =
                productDataSource
                    .getAllProductPageableDto(
                        query = ceramicOption.query,
                        page = ceramicOption.page!!,
                        perPage = ceramicOption.perPage!!,
                        byTypeCategoryId = ceramicOption.byTypeCategoryId,
                        bySizeCategoryId = ceramicOption.bySizeCategoryId,
                        byColorCategoryId = ceramicOption.byColorCategoryId,
                        isHot = ceramicOption.isHot,
                        sortField = ceramicOption.sortFiled!!,
                        sortDirection = ceramicOption.sortDirection!!
                    )
            if (ceramicList.isEmpty()) throw NotFoundException("no product is found.")
            val numberOfProducts = productDataSource.getNumberOfProduct()
            respondWithSuccessfullyResult(
                statusCode = HttpStatusCode.OK,
                result = MyResponsePageable(
                    page = ceramicOption.page + 1,
                    perPage = numberOfProducts,
                    data = ceramicList.toClientDto()
                ),
                message = "get all ceramic products successfully"
            )
        } catch (e: Exception) {
            throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
        }


    }
    // get the products --> get /api/v1/user-client/products
    get(ALL_PRODUCTS) {
        logger.debug { "GET ALL /${ALL_PRODUCTS}" }

        try {
            val params = call.request.queryParameters
            val ceramicOption = getCeramicProductOptions(params)
            val ceramicList =
                productDataSource
                    .getAllProductPageable(
                        query = ceramicOption.query,
                        page = ceramicOption.page!!,
                        perPage = ceramicOption.perPage!!,
                        byTypeCategoryId = ceramicOption.byTypeCategoryId,
                        bySizeCategoryId = ceramicOption.bySizeCategoryId,
                        byColorCategoryId = ceramicOption.byColorCategoryId,
                        isHot = ceramicOption.isHot,
                        sortField = ceramicOption.sortFiled!!,
                        sortDirection = ceramicOption.sortDirection!!
                    )
            if (ceramicList.isEmpty()) throw NotFoundException("no product is found.")
            val numberOfProducts = productDataSource.getNumberOfProduct()
            respondWithSuccessfullyResult(
                statusCode = HttpStatusCode.OK,
                result = MyResponsePageable(
                    page = ceramicOption.page + 1,
                    perPage = numberOfProducts,
                    data = ceramicList.toModelResponse()
                ),
                message = "get all ceramic products successfully"
            )
        } catch (e: Exception) {
            throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
        }


    }
    // get the products --> get /api/v1/user-client/products
//    get(ALL_PRODUCTS) {
//        call.request.queryParameters["page"]?.toIntOrNull()?.let {
//            val page = if (it > 0) it else 0
//            val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10
//            val type = call.request.queryParameters["type"]?.toIntOrNull() ?: -1
//            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: -1
//            val color = call.request.queryParameters["color"]?.toIntOrNull() ?: -1
//            val sortField = when (call.request.queryParameters["sort_by"] ?: "date") {
//                "name" -> ProductEntity.productName
//                "date" -> ProductEntity.createdAt
//                else -> {
//                    return@get call.respond(
//                        status = HttpStatusCode.BadRequest,
//                        message = MyResponse(
//                            success = false,
//                            message = "invalid parameter for sort_by chose between (name & date)",
//                            data = null
//                        )
//                    )
//                }
//            }
//            val sortDirection = when (call.request.queryParameters["sort_direction"] ?: "asc") {
//                "dec" -> -1
//                "asc" -> 1
//                else -> {
//                    return@get call.respond(
//                        status = HttpStatusCode.BadRequest,
//                        message = MyResponse(
//                            success = false,
//                            message = "invalid parameter for sort_direction chose between (dec & asc)",
//                            data = null
//                        )
//                    )
//                }
//            }
//
//            logger.debug { "GET ALL /$ALL_PRODUCTS?page=$page&perPage=$perPage" }
//            val productList = try {
////                    productDataSource.getAllProductPageable(page = page, perPage = perPage)
//                productDataSource.getAllProductPageableByCategories(
//                    page = page, perPage = perPage,
//                    categoryType = type,
//                    categorySize = size,
//                    categoryColor = color,
//                    sortField = sortField,
//                    sortDirection = sortDirection
//                )
//            } catch (exc: Exception) {
//                logger.error { "GET ALL /${exc.message}" }
//
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
//            if (productList.isNotEmpty()) {
//                call.respond(
//                    HttpStatusCode.OK, MyResponse(
//                        success = true,
//                        message = "get all products successfully",
//                        data = MyResponsePageable(page = page, perPage = perPage, data = productList)
//
//                    )
//                )
//
//
//            } else {
//                call.respond(
//                    HttpStatusCode.OK, MyResponse(
//                        success = false,
//                        message = "product is empty",
//                        data = null
//                    )
//                )
//            }
//        } ?: run {
//            val type = call.request.queryParameters["type"]?.toIntOrNull() ?: -1
//            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: -1
//            val color = call.request.queryParameters["color"]?.toIntOrNull() ?: -1
//            val sortField = when (call.request.queryParameters["sort_by"] ?: "name") {
//                "name" -> ProductEntity.productName
//                "date" -> ProductEntity.createdAt
//                else -> {
//                    return@get call.respond(
//                        status = HttpStatusCode.BadRequest,
//                        message = MyResponse(
//                            success = false,
//                            message = "invalid parameter for sort_by chose between (name & date)",
//                            data = null
//                        )
//                    )
//                }
//            }
//            val sortDirection = when (call.request.queryParameters["sort_direction"] ?: "asc") {
//                "dec" -> -1
//                "asc" -> 1
//                else -> {
//                    return@get call.respond(
//                        status = HttpStatusCode.BadRequest,
//                        message = MyResponse(
//                            success = false,
//                            message = "invalid parameter for sort_direction chose between (dec & asc)",
//                            data = null
//                        )
//                    )
//                }
//            }
//            logger.debug { "GET ALL /$ALL_PRODUCTS" }
//
//            val productList = try {
////                    productDataSource.getAllProduct()
//                productDataSource.getAllProductByCategories(
//                    categoryType = type,
//                    categorySize = size,
//                    categoryColor = color,
//                    sortField = sortField,
//                    sortDirection = sortDirection
//                )
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
//            if (productList.isNotEmpty()) {
//                call.respond(
//                    HttpStatusCode.OK, MyResponse(
//                        success = true,
//                        message = "get all type categories successfully",
//                        data = productList
//                    )
//                )
//
//
//            } else {
//                call.respond(
//                    HttpStatusCode.OK, MyResponse(
//                        success = false,
//                        message = "type categories is empty",
//                        data = null
//                    )
//                )
//            }
//        }
//
//
//    }
    // get the products --> get /api/v1/user-client/products-response
    get(ALL_PRODUCTS_RESPONSE) {
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

            logger.debug { "GET ALL /$ALL_PRODUCTS_RESPONSE?page=$page&perPage=$perPage" }
            val productList = try {
//                    productDataSource.getAllProductPageable(page = page, perPage = perPage)
                productDataSource.getAllProductResponsePageableByCategories(
                    page = page, perPage = perPage,
                    categoryType = type,
                    categorySize = size,
                    categoryColor = color,
                    sortField = sortField,
                    sortDirection = sortDirection
                )
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
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "get all products successfully",
                        data = ProductResponsePage(page = page, perPage = perPage, data = productList)

                    )
                )


            } else {
                call.respond(
                    HttpStatusCode.OK, MyResponse(
                        success = false,
                        message = "product is empty",
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
                productDataSource.getAllProductResponseByCategories(
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
                    HttpStatusCode.OK, MyResponse(
                        success = false,
                        message = "type categories is empty",
                        data = null
                    )
                )
            }
        }


    }
    // get the product --> get /api/v1/user-client/product/{id} (token required)
    get("$SINGLE_PRODUCT_RESPONSE/{id}") {
        logger.debug { "get /$SINGLE_PRODUCT_RESPONSE" }
        call.parameters["id"]?.toIntOrNull()?.let { id ->

            try {
                productDataSource.getProductResponseById(id)?.let { product ->
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = true,
                            message = "get product successfully",
                            data = product
                        )
                    )
                } ?: call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = false,
                        message = "Product Not Found",
                        data = null
                    )
                )
            } catch (e: Exception) {
                logger.error { "Exception /${e.stackTrace}" }
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )

            }

        } ?: call.respond(
            status = HttpStatusCode.BadRequest,
            message = MyResponse(
                success = false,
                message = "Missing Parameters",
                data = null
            )
        )
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