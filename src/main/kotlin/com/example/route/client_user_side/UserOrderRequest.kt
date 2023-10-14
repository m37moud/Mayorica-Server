package com.example.route.client_user_side

import com.example.data.order.OrderDataSource
import com.example.data.order.OrderStatusDataSource
import com.example.models.UserOrder
import com.example.models.UserOrderStatus
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

const val USER_CLIENT = "${Constants.ENDPOINT}/user-client"
const val ORDER_REQUEST = "$USER_CLIENT/order-request"
const val CREATE_ORDER_REQUEST = "$ORDER_REQUEST/create"

private val logger = KotlinLogging.logger {}

/**
 * create new order from
 * client user app
 */
/*
{
  {
    "full_name" : "mahmoud ali",
     "id_number" : "26911170025003",
     "department" : "militry",
     "country" : "egypt",
     "governorate" : "bani swef"
}
}
 */
fun Route.userOrderRequest(
    orderDataSource: OrderDataSource,
    orderStatusDataSource: OrderStatusDataSource
) {
    // create a user order --> POST/api/v1/user-client/order-request/create
    post(CREATE_ORDER_REQUEST) {
        logger.debug { "POST /$CREATE_ORDER_REQUEST" }

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
            val checkUserOrder =
                orderDataSource.getOrderByNameAndIdNumber(
                    userOrderRequest.full_name,
                    userOrderRequest.id_number
                )
            if (checkUserOrder == null) {
                val userOrder = UserOrder(
                    fullName = userOrderRequest.full_name,
                    id_number = userOrderRequest.id_number,
                    department = userOrderRequest.department,
                    country = userOrderRequest.country,
                    governorate = userOrderRequest.governorate,
                    created_at = LocalDateTime.now().toString(),
                    updated_at = ""
                )

                val result = orderDataSource.createOrder(userOrder)
                if (result > 0) {
                    // TODO: delete record when crash from status table
                    orderDataSource
                        .getOrderByNameAndIdNumber(
                            name = userOrderRequest.full_name,
                            id_number = userOrderRequest.id_number
                        ).let {

                            UserOrderStatus(
                                requestUser_id = it!!.id, approveByAdminId = 1
                            ).let { userOrderStatus ->
                                val insertResult = orderStatusDataSource.createOrderStatus(userOrderStatus)
                                if (insertResult > 0) {
                                    call.respond(
                                        HttpStatusCode.OK,
                                        MyResponse(
                                            success = true,
                                            message = "Order Successfully",
                                            data = userOrderStatus.requestUser_id
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
                            }
                        }

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

