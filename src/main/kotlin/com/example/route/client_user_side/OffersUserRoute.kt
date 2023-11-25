package com.example.route.client_user_side

import com.example.data.offers.OffersDataSource
import com.example.utils.Constants
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging


private const val ALL_OFFERS = "${USER_CLIENT}/offers"
private const val SINGLE_OFFERS = "${USER_CLIENT}/offer"
private const val SINGLE_LAST_OFFERS = "${SINGLE_OFFERS}/last"


private val logger = KotlinLogging.logger { }


fun Route.offersUserRoute(
    offersDataSource: OffersDataSource
) {
    //get all offers //api/v1/user-client/offers
    get(ALL_OFFERS) {
        try {

            logger.debug { "GET ALL /${ALL_OFFERS}" }

            val result = offersDataSource.getAllAvailableOffers()
            if (result.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "get all Offers successfully",
                        data = result
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
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
                            data = offer
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
                        data = it
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
