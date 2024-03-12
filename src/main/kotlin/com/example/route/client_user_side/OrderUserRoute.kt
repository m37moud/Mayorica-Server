package com.example.route.client_user_side

import com.example.data.order.OrderDataSource
import com.example.data.order.OrderStatusDataSource
import com.example.mapper.toEntity
import com.example.models.request.order.UserOrderRequest
import com.example.models.response.OrderResponse
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import com.example.utils.generateOrderNumber
import com.example.utils.respondWithSuccessfullyResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val ORDER_REQUEST = "$USER_CLIENT/order"
private const val CREATE_ORDER_REQUEST = "$ORDER_REQUEST/create"

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
fun Route.userOrderRequest() {
    val orderDataSource: OrderDataSource by inject()

    // create a user order --> POST/api/v1/user-client/order-request/create
    post(CREATE_ORDER_REQUEST) {
        logger.debug { "POST /$CREATE_ORDER_REQUEST" }



        try {
            val userOrderRequest = call.receive<UserOrderRequest>()
            // check if operation connected db successfully
            //tried this on a webhook, where call = ApplicationCall inside the PipelineContext block

            val clientIP = call.request.origin.remoteAddress.toString()

            logger.debug { "$CREATE_ORDER_REQUEST clientIP = $clientIP" }
            //change from getOrderByNameAndIdNumber
            val checkUserOrder =
                orderDataSource.getOrderByIdNumber(
//                    userOrderRequest.fullName,
                    userOrderRequest.idNumber
                )
            if (checkUserOrder == null) {
                val generatedOrderNum = generateOrderNumber()


                val result = orderDataSource
                    .createOrderWithOrderStatus(
                        userOrderRequest
                            .toEntity(generatedOrderNum)
                    )
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Order Successfully please save this number",
                            data = generatedOrderNum
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
                        success = true,
                        message = "User order already made before.",
                        data = checkUserOrder.orderNumber
                    )
                )
                return@post
            }


        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
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

fun Route.getUserOrderClient() {
    val orderDataSource: OrderDataSource by inject()
    val orderStatusDataSource: OrderStatusDataSource by inject()

    //api/v1/user-client/order/{order_num}
    get("$ORDER_REQUEST/{order_num}") {
        logger.debug { "GET /$ORDER_REQUEST" }
        call.parameters["order_num"]?.let { number ->
            try {
                orderDataSource
                    .getOrderByOrderNumDto(orderNumber = number)?.let {
                        respondWithSuccessfullyResult(
                            result = it,
                            message = "got order Info successfully ."
                        )
                    } ?: call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "no order is found .",
                        data = null
                    )
                )
//

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Conflict during get note",
                        data = null
                    )
                )
            }


        } ?: call.respond(
            HttpStatusCode.BadRequest,
            MyResponse(
                success = false,
                message = "Missing parameter .",
                data = null
            )
        )
    }

}




