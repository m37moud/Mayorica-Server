package com.example.route.client_admin_side

import com.example.data.gallery.products.ProductDataSource
import com.example.data.gallery.products.hot_release.HotReleaseDataSource
import com.example.database.table.HotReleaseProductEntity
import com.example.database.table.ProductEntity
import com.example.models.HotReleaseProduct
import com.example.utils.Constants
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private const val HOT_RELEASE_PRODUCTS = "${ADMIN_CLIENT}/hot_release_products"
private const val HOT_RELEASE_PRODUCT = "${ADMIN_CLIENT}/hot_release"
private const val ADD_HOT_RELEASE_PRODUCT = "${HOT_RELEASE_PRODUCT}/add"
private const val DELETE_HOT_RELEASE_PRODUCT = "${HOT_RELEASE_PRODUCT}/delete"

private val logger = KotlinLogging.logger {}

fun Route.hotReleaseAdminRoute(
    hotReleaseDataSource: HotReleaseDataSource,
    productDataSource: ProductDataSource
) {
    authenticate {
        //get request -> api/v1/admin-client/hot_release_products
        get(HOT_RELEASE_PRODUCTS) {
            logger.debug { "get $HOT_RELEASE_PRODUCTS" }
            try {
                val limit  = call.request.queryParameters["limit"]?.toIntOrNull() ?: 5

                val productList = hotReleaseDataSource.getAllHotReleaseProduct(limit)
                if (productList.isNotEmpty()) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = true,
                            message = "product fetched successfully from hot release",
                            data = productList
                        )
                    )
                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = false,
                            message = "no product found in hot release",
                            data = null
                        )
                    )
                    return@get
                }

            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "failed"}" }
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = MyResponse(
                        success = false,
                        message = e.message ?: "Missing parameters",
                        data = null
                    )
                )
                return@get
            }


        }
        //post request -> api/v1/admin-client/hot_release_product/create
        post("$ADD_HOT_RELEASE_PRODUCT/{id}") {
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                logger.debug { "post $ADD_HOT_RELEASE_PRODUCT/$id" }
                val principal = call.principal<JWTPrincipal>()
                val userAdminId = try {
                    principal?.getClaim("userId", String::class)?.toIntOrNull()
                } catch (e: Exception) {
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
                    productDataSource.getProductById(productId = id)?.let {
                        // if product in hot release table dont insert and return post
                        hotReleaseDataSource.getHotReleaseProduct(id)?.let {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = MyResponse(
                                    success = false,
                                    message = "Hot Release Product added before",
                                    data = null
                                )
                            )
                            return@post
                        }
                        val hotProduct = HotReleaseProduct(productId = id, userAdminId =userAdminId!!)
                        val result = hotReleaseDataSource.addHotReleaseProduct(hotProduct)
                        if (result > 0) {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = MyResponse(
                                    success = true,
                                    message = "Hot Release Product Insert successfully",
                                    data = null
                                )
                            )

                        } else {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = MyResponse(
                                    success = false,
                                    message = "Hot Release Product Insert Failed",
                                    data = null
                                )
                            )

                        }
                    } ?: call.respond(
                        status = HttpStatusCode.NotFound,
                        message = MyResponse(
                            success = false,
                            message = "this product is not found please add it first then add to hot release",
                            data = null
                        )
                    )


                } catch (e: Exception) {
                    logger.error { "${e.stackTrace ?: "failed"}" }
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = false,
                            message = e.message ?: "failed",
                            data = null
                        )
                    )
                    return@post

                }

            } ?: call.respond(
                status = HttpStatusCode.Conflict, message = MyResponse(
                    success = false, message = "Missing parameters",
                    data = null
                )
            )
        }
        //delete request -> api/v1/admin-client/hot_release_product/delete
        delete(DELETE_HOT_RELEASE_PRODUCT) {
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                logger.debug { "delete $DELETE_HOT_RELEASE_PRODUCT/$id" }
                try {
                    val result = hotReleaseDataSource.deleteHotReleaseProduct(productId = id)
                    if (result > 0) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = true,
                                message = "Hot Release Product deleted successfully",
                                data = null
                            )
                        )

                    } else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = false,
                                message = "Hot Release Product delete Failed",
                                data = null
                            )
                        )

                    }
                } catch (e: Exception) {

                    logger.error { "delete ${e.stackTrace ?: "failed"}" }
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = false,
                            message = e.message ?: "failed ",
                            data = null
                        )
                    )
                    return@delete
                }

            } ?: call.respond(
                status = HttpStatusCode.Conflict,
                message = MyResponse(
                    success = false,
                    message = "Missing parameters",
                    data = null
                )
            )
        }
    }
}