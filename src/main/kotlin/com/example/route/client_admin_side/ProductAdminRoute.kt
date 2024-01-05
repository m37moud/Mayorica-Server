package com.example.route.client_admin_side

import com.example.data.gallery.products.ProductDataSource
import com.example.database.table.ProductEntity
import com.example.models.Product
import com.example.models.MyResponsePageable

import com.example.service.storage.StorageService
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.Constants.ENDPOINT
import com.example.utils.MyResponse
import com.example.utils.generateSafeFileName
import com.example.utils.isImageContentType
import com.example.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.time.LocalDateTime


/**
 * we need
 * create
 * update
 * delete
 * MySQL80
 */

private const val ALL_PRODUCTS = "$ADMIN_CLIENT/products"
private const val SEARCH_PRODUCTS = "$ALL_PRODUCTS/search"
private const val SINGLE_PRODUCT = "$ADMIN_CLIENT/product"
private const val CREATE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/create"
private const val UPDATE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/update"
private const val DELETE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/delete"

private val logger = KotlinLogging.logger {}

fun Route.productAdminRoute(
//    productDataSource: ProductDataSource,
//    storageService: StorageService
) {
    val productDataSource: ProductDataSource by inject()
    val storageService: StorageService by inject()

    authenticate {

        // get the products --> get /api/v1/admin-client/products/ (token required)
        get(ALL_PRODUCTS) {
            call.request.queryParameters["page"]?.toIntOrNull()?.let {
                val page = if (it > 0) it-1 else 0
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
                val sortDirection = when (call.request.queryParameters["sort_direction"] ?: "dec") {
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
//                    productDataSource.getAllProductPageable(page = page, perPage = perPage)
                    productDataSource.getAllProductPageableByCategories(
                        page = page, perPage = perPage,
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
                            message = "get all products successfully",
                            data = MyResponsePageable(page = page, perPage = perPage, data = productList)
                        )
                    )


                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "no product is found.",
                            data = null
                        )
                    )
                }
            } ?: run {
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
                val sortDirection = when (call.request.queryParameters["sort_direction"] ?: "dec") {
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
                logger.debug { "GET ALL /$TYPE_CATEGORIES" }

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
                        HttpStatusCode.OK, MyResponse(
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

            call.request.queryParameters["product_name"]?.let { name ->
                logger.debug { "GET ALL /$SEARCH_PRODUCTS?product_name=$name" }

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
        // post the product --> POST /api/v1/admin-client/product/create (token required)
        post(CREATE_SINGLE_PRODUCT) {
            logger.debug { "post /$CREATE_SINGLE_PRODUCT" }

            val multiPart = call.receiveMultipart()
            var typeCategoryId: Int? = null
            var sizeCategoryId: Int? = null
            var colorCategoryId: Int? = null
            var productName: String? = null
            var deleted = false
            var fileName: String? = null
            var fileBytes: ByteArray? = null
            var url: String? = null
            var imageUrl: String? = null
            val principal = call.principal<JWTPrincipal>()

            val userAdminId = try {
                principal?.getClaim("userId", String::class)?.toIntOrNull()
            } catch (e: Exception) {
//                logger.runCatching { "post /$e" }
                call.respond(
                    status = HttpStatusCode.Conflict, message = MyResponse(
                        success = false,
                        message = e.message ?: "Error with authentication",
                        data = null
                    )
                )
                return@post
            }
            try {
                val baseUrl =
                    call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port() + "$ENDPOINT/image/"
                multiPart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            // to read parameters that we sent with the image
                            when (part.name) {
                                "typeCategoryId" -> {
                                    typeCategoryId = part.value.toIntOrNull()
                                }

                                "sizeCategoryId" -> {
                                    sizeCategoryId = part.value.toIntOrNull()
                                }

                                "colorCategoryId" -> {
                                    colorCategoryId = part.value.toIntOrNull()
                                }

                                "productName" -> {
                                    productName = part.value
                                }

                                "deleted" -> {
                                    deleted = part.value.toBoolean()
                                }

                            }

                        }

                        is PartData.FileItem -> {
                            if (!isImageContentType(part.contentType.toString())) {
                                call.respond(
                                    message = MyResponse(
                                        success = false,
                                        message = "Invalid file format",
                                        data = null
                                    ), status = HttpStatusCode.BadRequest
                                )
                                part.dispose()
                                return@forEachPart

                            }
                            fileName = generateSafeFileName(part.originalFileName as String)
//                            imageUrl = "$baseUrl$fileName"
                            fileBytes = part.streamProvider().readBytes()
                            url = "${baseUrl}products/${fileName}"


                        }

                        else -> {}

                    }
                    part.dispose()
                }

                val isProduct = productDataSource.getProductByName(productName!!)
                if (isProduct == null) {
                    try {
                        imageUrl = storageService.saveProductImage(
                            fileName = fileName!!,
                            fileUrl = url!!,
                            fileBytes = fileBytes!!
                        )
                        if (!imageUrl.isNullOrEmpty()) {
                            Product(
                                typeCategoryId = typeCategoryId!!,
                                sizeCategoryId = sizeCategoryId!!,
                                colorCategoryId = colorCategoryId!!,
                                userAdminID = userAdminId!!,
                                productName = productName!!,
                                image = imageUrl,
                                createdAt = LocalDateTime.now().toDatabaseString(),
                                updatedAt = "",
                                deleted = deleted
                            ).apply {

                                val isInserted = productDataSource.createProduct(this)
                                if (isInserted > 0) {
                                    call.respond(
                                        status = HttpStatusCode.Created,
                                        message = MyResponse(
                                            success = true,
                                            message = "product inserted successfully",
                                            data = this
                                        )
                                    )
                                    return@post
                                } else {
                                    call.respond(
                                        status = HttpStatusCode.OK,
                                        message = MyResponse(
                                            success = false,
                                            message = "product insert failed",
                                            data = null
                                        )
                                    )
                                    return@post
                                }
                            }
                        } else {
                            storageService.deleteProductImage(fileName = fileName!!)
                            call.respond(
                                status = HttpStatusCode.Conflict,
                                message = MyResponse(
                                    success = false,
                                    message = "some Error happened while uploading .",
                                    data = null
                                )
                            )
                            return@post
                        }

                    } catch (ex: Exception) {
                        // something went wrong with the image part, delete the file
                        storageService.deleteProductImage(fileName = fileName!!)
                        ex.printStackTrace()
                        call.respond(
                            status = HttpStatusCode.InternalServerError, message = MyResponse(
                                success = false,
                                message = ex.message ?: "Error happened while uploading .",
                                data = null
                            )
                        )
                        return@post
                    }

                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = false,
                            message = "this product is found",
                            data = null
                        )
                    )
                    return@post
                }


            } catch (ex: Exception) {
                storageService.deleteProductImage(fileName = fileName!!)
                ex.printStackTrace()
                call.respond(
                    status = HttpStatusCode.InternalServerError, message = MyResponse(
                        success = false,
                        message = ex.message ?: "Error happened",
                        data = null
                    )
                )
                return@post
            }


        }
        // get the product --> get /api/v1/admin-client/product/{id} (token required)
        get("$SINGLE_PRODUCT/{id}") {
            logger.debug { "get /$SINGLE_PRODUCT" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->

                try {
                    productDataSource.getProductById(id)?.let { product ->
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
        // delete the product --> delete /api/v1/admin-client/product/delete/{id} (token required)
        delete("$DELETE_SINGLE_PRODUCT/{id}") {
            logger.debug { "delete /$CREATE_SINGLE_PRODUCT" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                productDataSource.getProductById(productId = id)?.let {
                    val isDeletedImage = try {
                        storageService.deleteProductImage(fileName = it.image.substringAfterLast("/"))
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            MyResponse(
                                success = false,
                                message = e.message ?: "Failed to delete image",
                                data = null
                            )
                        )
                        return@delete
                    }
                    if (isDeletedImage) {
                        val deleteResult =
                            productDataSource.deleteProduct(productId = id)
                        if (deleteResult > 0) {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = true,
                                    message = "product deleted successfully .",
                                    data = null
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = false,
                                    message = " product deleted failed .",
                                    data = null
                                )
                            )
                        }

                    } else {
                        call.respond(
                            HttpStatusCode.Conflict,
                            MyResponse(
                                success = false,
                                message = "Failed to delete image",
                                data = null
                            )
                        )

                    }


                } ?: call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = " news deleted failed .",
                        data = null
                    )
                )

            } ?: call.respond(
                status = HttpStatusCode.BadRequest,
                message = MyResponse(
                    success = false,
                    message = "Missing parameters",
                    data = null
                )
            )

        }


    }

}


