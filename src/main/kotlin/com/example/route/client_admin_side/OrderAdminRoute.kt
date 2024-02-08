package com.example.route.client_admin_side

import com.example.data.administrations.admin_user.UserDataSource
import com.example.data.order.OrderDataSource
import com.example.data.order.OrderStatusDataSource
import com.example.models.MyResponsePageable
import com.example.models.options.getCustomerOrderOptions
import com.example.models.request.order.UserOrderStatusRequest
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

const val ORDER_RESPONSE = "${ADMIN_CLIENT}/orders"
const val ORDER_RESPONSE_PAGEABLE = "$ORDER_RESPONSE-pageable"
const val ORDER_STATUE_RESPONSE = "/statue"
private val logger = KotlinLogging.logger {}

fun Route.ordersAdminRoute() {
    val orderDataSource: OrderDataSource by inject()
    val orderStatusDataSource: OrderStatusDataSource by inject()
    val userDataSource: UserDataSource by inject()



    authenticate {
        // Get the orders info --> GET /api/v1/admin-client/orders (with token)
        route(ORDER_RESPONSE) {
            get {
                logger.debug { "get /$ORDER_RESPONSE" }

                logger.debug { "GET ALL pageable /$ORDER_RESPONSE_PAGEABLE" }

                val userId = extractAdminId()
                val isAdmin = userDataSource.isAdmin(userId)
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


            get("$ORDER_STATUE_RESPONSE/{id}") {
                logger.debug { "get /$ORDER_STATUE_RESPONSE/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                try {
                    id?.let {
                        orderStatusDataSource.getOrderStatusByRequestUserIdDto(it)?.let { order ->
                            respondWithSuccessfullyResult(
                                result = order,
                                message = "get order successful ."
                            )
                        } ?: throw NotFoundException("order not found .")
                    } ?: throw MissingParameterException("Missing parameters .")
                } catch (e: Exception) {
                    throw UnknownErrorException(e.message ?: "An Known Error Occurred  ")
                }


            }

            put("/statue/{id}") {

                logger.debug { "put /$ORDER_RESPONSE/{id}" }
                val userAdminId = extractAdminId()
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
        get(ORDER_RESPONSE_PAGEABLE) {
            logger.debug { "GET ALL /$ORDER_RESPONSE_PAGEABLE" }
            try {
                val params = call.request.queryParameters
                val orderOptions = getCustomerOrderOptions(parameters = params)
                val customerOrders = orderStatusDataSource
                    .getAllCustomerOrderPageable(
                        query = orderOptions.query,
                        page = orderOptions.page!!,
                        perPage = orderOptions.perPage!!,
                        byApproveStatue = orderOptions.byApproveStatue,
                        sortField = orderOptions.sortFiled!!,
                        sortDirection = orderOptions.sortDirection!!
                    )
                if (customerOrders.isEmpty()) throw NotFoundException("no order is found.")
                val numberOfOrders = orderStatusDataSource.getNumberOfOrders()
                respondWithSuccessfullyResult(
                    result = MyResponsePageable(
                        page = orderOptions.page + 1,
                        perPage = numberOfOrders,
                        data = customerOrders
                    )
                )

            } catch (e: Exception) {
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }


        }

    }
}

