package com.example.route.client_user_side

import com.example.data.contact_us.ContactUsDataSource
import com.example.mapper.toUserResponse
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject


private const val CONTACT_US = "${USER_CLIENT}/contact-us"


private val logger = KotlinLogging.logger { }

fun Route.contactUsUserRoute() {
    val contactUsDataSource: ContactUsDataSource by inject()
    //get about us //api/v1/user-client/contact-us
    get(CONTACT_US) {
        logger.debug { "Get Contact US $CONTACT_US" }
        try {
            contactUsDataSource.getContactUsInfo()?.let {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = true,
                        message = "Contact Us Information Found",
                        data = it.toUserResponse()
                    )
                )


            } ?: call.respond(
                status = HttpStatusCode.OK,
                message = MyResponse(
                    success = false,
                    message = "Contact Us Information Not Found",
                    data = null
                )
            )


        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = e.message ?: "Transaction Failed ",
                    data = null
                )
            )
            return@get
        }

    }

}