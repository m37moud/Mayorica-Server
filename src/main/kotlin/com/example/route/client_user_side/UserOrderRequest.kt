package com.example.route.client_user_side

import com.example.data.order.OrderDataSource
import com.example.models.UserOrder
import com.example.models.request.order.UserOrderRequest
import com.example.route.client_admin_side.LOGIN_REQUEST
import com.example.utils.Constants
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.time.LocalDateTime


const val USER_REQUEST = "${Constants.ENDPOINT}/user-request"
const val CREATE_USER_REQUEST = "$USER_REQUEST/create"

private val logger = KotlinLogging.logger {}

/**
 * create new order from
 * client user app
 */
fun Route.userOrderRequest(orderDataSource: OrderDataSource) {
    // create a user order --> POST /api/v1/user/create
    post(CREATE_USER_REQUEST) {
        logger.debug { "POST /$LOGIN_REQUEST" }

        val userOrderRequest = try {
            call.receive<UserOrderRequest>()
        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = false,
                    message = "Missing Some Fields",
                    data = null
                )
            )
            return@post
        }
        // check if operation connected db successfully

        try {
            val userOrder =
                orderDataSource.getOrderByNameAndIdNumber(
                    userOrderRequest.full_name,
                    userOrderRequest.id_number
                )
            if (userOrder == null) {
                val userOrder = UserOrder(
                    fullName = userOrderRequest.full_name,
                    id_number = userOrderRequest.id_number,
                    department = userOrderRequest.department,
                    country = userOrderRequest.country,
                    governorate = userOrderRequest.governorate,
                    created_at = LocalDateTime.now().toString(),
                    update_at = ""
                )
                val result = orderDataSource.createOrder(userOrder)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Registration Successfully",
                            data = null
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Failed to create new order.",
                            data = null
                        )
                    )
                    return@post
                }

            } else {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "User order already made before.",
                        data = null
                    )
                )
                return@post
            }


        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = false,
                    message = exc.message ?: "create order Failed.",
                    data = null
                )
            )
            return@post
        }

    }

}

