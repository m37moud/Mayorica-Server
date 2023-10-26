package com.example.route.client_admin_side

import com.example.data.admin_user.UserDataSource
import com.example.data.order.OrderDataSource
import com.example.data.order.OrderStatusDataSource
import com.example.models.request.order.UserOrderStatusRequest
import com.example.utils.Constants
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import com.example.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.time.LocalDateTime

const val ORDER_RESPONSE = "${ADMIN_CLIENT}/orders"
const val ORDER_STATUE_RESPONSE = "${ADMIN_CLIENT}/statue"
private val logger = KotlinLogging.logger {}

fun Route.orders(
    orderDataSource: OrderDataSource,
    orderStatusDataSource: OrderStatusDataSource,
    userDataSource: UserDataSource
) {
    authenticate {
        // Get the orders info --> GET /api/v1/admin-client/orders (with token)
        route(ORDER_RESPONSE) {
            get{
                logger.debug { "get /$ORDER_RESPONSE" }
                val principal = call.principal<JWTPrincipal>()
                val userId = try {
                    principal?.getClaim("userId", String::class)?.toIntOrNull()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = e.message ?: "Failed ",
                            data = null
                        )
                    )
                    return@get
                }
                val isAdmin = userDataSource.isAdmin(userId!!)
                if (isAdmin) {
                    try {

                        val orders = orderDataSource.getAllOrder()
                        if (orders.isEmpty()) {
                            call.respond(
                                HttpStatusCode.OK, MyResponse(
                                    success = false,
                                    message = "no orders is found .",
                                    data = null
                                )
                            )
                            return@get
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
                } else {
                    call.respond(
                        HttpStatusCode.Forbidden, MyResponse(
                            success = false,
                            message = "not Authorize to show .",
                            data = null
                        )
                    )
                }


            }
            get("/statue/{id}") {
                logger.debug { "get /$ORDER_STATUE_RESPONSE/{id}" }
                val principal = call.principal<JWTPrincipal>()
                val userId = try {
                    principal?.getClaim("userId", String::class)?.toIntOrNull()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = e.message ?: "Failed ",
                            data = null
                        )
                    )
                    return@get
                }
                val isAdmin = userDataSource.isAdmin(userId!!)
                if (isAdmin) {
                    val id = call.parameters["id"]?.toIntOrNull()
                    try {

                        id?.let {
                            orderStatusDataSource.getOrderStatusByRequestUserId(it)?.run {
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
                } else {
                    call.respond(
                        HttpStatusCode.Forbidden, MyResponse(
                            success = false,
                            message = "not Authorize to show .",
                            data = null
                        )
                    )
                    return@get
                }


            }

            put("/statue/{id}") {

                logger.debug { "put /$ORDER_RESPONSE/{id}" }
                val principal = call.principal<JWTPrincipal>()
                val userAdminId = try {
                    principal?.getClaim("userId", String::class)?.toIntOrNull()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = e.message ?: "Failed ",
                            data = null
                        )
                    )
                    return@put
                }
                val isAdmin = userDataSource.isAdmin(userAdminId!!)
                if (isAdmin) {
                    val id = call.parameters["id"]?.toIntOrNull()
                    try {

                        id?.let {
                            val orderStatusRequest = call.receive<UserOrderStatusRequest>()

                            orderStatusDataSource.getOrderStatusByRequestUserId(it)?.run {
                                val tempOrderStatue = this.copy(
                                    approveState = orderStatusRequest.approveState,
                                    approveUpdateDate = LocalDateTime.now().toDatabaseString(),
                                    approveByAdminId = userAdminId,
                                    totalAmount = orderStatusRequest.totalAmount,
                                    takenAmount = orderStatusRequest.takenAmount,
                                    availableAmount = orderStatusRequest.availableAmount,
                                    note = orderStatusRequest.note
                                )
                                val updateResult =
                                    orderStatusDataSource.updateOrderStatus(requestUserId = id, tempOrderStatue)
                                if (updateResult > 0) {
                                    call.respond(
                                        HttpStatusCode.OK, MyResponse(
                                            success = true,
                                            message = "order statue update successful .",
                                            data = null
                                        )
                                    )
                                    return@put
                                } else {
                                    call.respond(
                                        HttpStatusCode.OK,
                                        MyResponse(
                                            success = false,
                                            message = "update order is failed .",
                                            data = null
                                        )
                                    )
                                    return@put

                                }

                            } ?: call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = false,
                                    message = "no order is found .",
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

                } else {
                    call.respond(
                        HttpStatusCode.Forbidden, MyResponse(
                            success = false,
                            message = "not Authorize to show .",
                            data = null
                        )
                    )
                    return@put
                }

            }

        }
    }
}

