package com.example.route.client_admin_side

import com.example.data.gallery.products.ProductDataSource
import com.example.models.Product
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
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*


/**
 * we need
 * create
 * update
 * delete
 * MySQL80
 */

const val ALL_PRODUCTS = "$ADMIN_CLIENT/products"
const val SINGLE_PRODUCT = "$ADMIN_CLIENT/product"
const val CREATE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/create"
const val UPDATE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/update"
const val DELETE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/delete"

private val logger = KotlinLogging.logger {}

fun Route.productAdminRoute(
    productDataSource: ProductDataSource,
    storageService: StorageService
) {
    authenticate {
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
                            imageUrl = "$baseUrl$fileName"
                            fileBytes = part.streamProvider().readBytes()
                            url = "$baseUrl$fileName"


                        }

                        else -> {}

                    }
                    part.dispose()
                }

                val isProduct = productDataSource.getProductByName(productName!!)
                if (isProduct == null) {
                    imageUrl = try {
                        storageService.saveFile(
                            fileName = fileName!!,
                            fileUrl = url!!,
                            fileBytes = fileBytes!!
                        ).also {
                            if (!imageUrl.isNullOrEmpty()) {
                                Product(
                                    typeCategoryId = typeCategoryId!!,
                                    sizeCategoryId = sizeCategoryId!!,
                                    colorCategoryId = colorCategoryId!!,
                                    userAdminID = userAdminId!!,
                                    productName = productName!!,
                                    image = imageUrl!!,
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
                                                message = "this product inserted before",
                                                data = null
                                            )
                                        )
                                        return@post
                                    }
                                }
                            } else {
                                storageService.deleteFile(fileName = fileName!!)
                                call.respond(
                                    status = HttpStatusCode.OK,
                                    message = MyResponse(
                                        success = false,
                                        message = "some Error happened while uploading .",
                                        data = null
                                    )
                                )
                                return@post
                            }

                        }
                    } catch (ex: Exception) {
                        // something went wrong with the image part, delete the file
                        storageService.deleteFile(fileName = fileName!!)
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
                storageService.deleteFile(fileName = fileName!!)
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

    }

}


