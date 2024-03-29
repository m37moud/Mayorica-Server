package com.example.route.client_user_side

import com.example.data.offers.OffersDataSource
import com.example.mapper.toUserResponse
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject


private const val ALL_AVAILABLE_OFFERS = "${USER_CLIENT}/offers"
private const val SINGLE_OFFERS = "${USER_CLIENT}/offer"
private const val SINGLE_LAST_OFFERS = "${SINGLE_OFFERS}/last"
private const val SINGLE_RANDOM_HOT_OFFERS = "${SINGLE_OFFERS}/random"


private val logger = KotlinLogging.logger { }


fun Route.offersUserRoute() {
    val offersDataSource: OffersDataSource by inject()

    //get all offers //api/v1/user-client/offers
    get(ALL_AVAILABLE_OFFERS) {
        try {

            logger.debug { "GET ALL /${ALL_AVAILABLE_OFFERS}" }

            val result = offersDataSource.getAllAvailableOffers()
            if (result.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "get all Offers successfully",
                        data = result.toUserResponse()
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "No Available Offers",
                        data = null
                    )
                )
            }

        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Transaction Failed ",
                    data = null
                )
            )
            return@get
        }

    }
    //get offer //api/v1/user-client/offer/{id}
    get("${SINGLE_OFFERS}/{id}") {
        try {
            logger.debug { "get /${SINGLE_OFFERS}/{id}" }
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let {
                offersDataSource.getOffersById(it)?.let { offer ->
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "offer is found .",
                            data = offer.toUserResponse()
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "no offer found .",
                        data = null
                    )
                )

            } ?: call.respond(
                HttpStatusCode.BadRequest,
                MyResponse(
                    success = false,
                    message = "Missing parameters .",
                    data = null
                )
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
    }

    //get offer //api/v1/user-client/offer/last
    get(SINGLE_LAST_OFFERS) {
        try {

            logger.debug { "GET ALL /${SINGLE_LAST_OFFERS}" }

            offersDataSource.getLastAvailableOffer()?.let {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "get Last Offer successfully",
                        data = it.toUserResponse()
                    )
                )

            } ?: call.respond(
                HttpStatusCode.NotFound,
                MyResponse(
                    success = false,
                    message = "No Available Offers",
                    data = null
                )
            )


        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Transaction Failed ",
                    data = null
                )
            )
            return@get
        }

    }

    //get offer //api/v1/user-client/offer/random
    get(SINGLE_RANDOM_HOT_OFFERS) {
        try {

            logger.debug { "GET  /${SINGLE_RANDOM_HOT_OFFERS}" }

            offersDataSource.getRandomHotOffers()?.let {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "get Random Offer successfully",
                        data = it.toUserResponse()
                    )
                )

            } ?: call.respond(
                HttpStatusCode.NotFound,
                MyResponse(
                    success = false,
                    message = "No Available Offers",
                    data = null
                )
            )


        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Transaction Failed ",
                    data = null
                )
            )
            return@get
        }

    }

}
