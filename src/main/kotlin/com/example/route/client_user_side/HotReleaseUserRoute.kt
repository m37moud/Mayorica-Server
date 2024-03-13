package com.example.route.client_user_side

import com.example.data.gallery.products.hot_release.HotReleaseDataSource
import com.example.mapper.toModelResponse
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject


private const val HOT_RELEASE_PRODUCTS = "${USER_CLIENT}/hot_release_products"

private val logger = KotlinLogging.logger {}

fun Route.hotReleaseUserRoute(){
    val hotReleaseDataSource: HotReleaseDataSource by inject()

    //get request -> api/v1/user-client/hot_release_products
    get(HOT_RELEASE_PRODUCTS) {
        logger.debug { "get $HOT_RELEASE_PRODUCTS" }
        try {
            val limit  = call.request.queryParameters["limit"]?.toIntOrNull() ?: 5

            val productList = hotReleaseDataSource.getAllHotReleaseProduct(limit)
            if (productList.isNotEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = true,
                        message = "product fetched successfully from hot release",
                        data = productList.toModelResponse()
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

}