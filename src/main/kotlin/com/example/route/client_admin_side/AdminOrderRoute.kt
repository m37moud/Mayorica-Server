package com.example.route.client_admin_side

import com.example.data.order.OrderDataSource
import com.example.utils.Constants
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

const val ORDER_RESPONSE = "${ADMIN_CLIENT}/orders"
private val logger = KotlinLogging.logger {}

fun Route.getOrders(orderDataSource: OrderDataSource) {
    authenticate {
        // Get the orders info --> GET /api/admin-client/orders (with token)
        route(ORDER_RESPONSE) {
            get {
                logger.debug { "get /$ORDER_RESPONSE" }
                try {
                    val orders = orderDataSource.getAllOrder()
                    if (orders.isEmpty()) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "no orders is found .",
                                data = orders
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get orders successful .",
                                data = orders
                            )
                        )

                    }
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


            }
            get("{id}") {
                logger.debug { "get /$ORDER_RESPONSE/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                try {
                    id?.let {
                        orderDataSource.getOrderById(id)?.run {
                            call.respond(
                                HttpStatusCode.OK, MyResponse(
                                    success = true,
                                    message = "get orders successful .",
                                    data = this
                                )
                            )
                        } ?: call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = false,
                                message = "no orders is found .",
                                data = null
                            )
                        )
                    } ?: call.respond(
                        HttpStatusCode.BadRequest,
                        MyResponse(
                            success = false,
                            message = "Missing parameter .",
                            data = null
                        )
                    )

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


            }

        }
    }
}

