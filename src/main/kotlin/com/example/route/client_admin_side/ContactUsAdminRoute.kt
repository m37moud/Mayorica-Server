package com.example.route.client_admin_side

import com.example.data.contact_us.ContactUsDataSource
import com.example.data.gallery.products.hot_release.HotReleaseDataSource
import com.example.mapper.toModel
import com.example.models.AboutUs
import com.example.models.ContactUs
import com.example.models.dto.ContactUsCreateDto
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import com.example.utils.UnknownErrorException
import com.example.utils.extractAdminId
import com.example.utils.respondWithSuccessfullyResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val CONTACT_US = "$ADMIN_CLIENT/contact-us"
private const val CREATE_CONTACT_US = "$CONTACT_US/create"
private const val UPDATE_CONTACT_US = "$CONTACT_US/update"
private const val DELETE_CONTACT_US = "$CONTACT_US/delete"

private val logger = KotlinLogging.logger { }

fun Route.contactUsAdminRoute() {
    val contactUsDataSource: ContactUsDataSource by inject()

    authenticate {
        //get contact us //api/v1/admin-client/contact-us
        get(CONTACT_US) {
            logger.debug { "Get Contact US $CONTACT_US" }
            try {
                contactUsDataSource.getContactUsInfo()?.let {
                    respondWithSuccessfullyResult(
                        result = it,
                        message = "Contact Us Information Found"
                    )


                } ?: respondWithSuccessfullyResult(
                    result = ContactUs(),
                    message = "Contact Us Information Found"
                )


            } catch (e: Exception) {
                logger.error { "$CONTACT_US error ${e.stackTrace}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }

        }
        //post contact us //api/v1/admin-client/contact-us/create
        post(CREATE_CONTACT_US) {
            logger.debug { "POST contact US $CREATE_CONTACT_US" }


            try {
                val contactUsRequest = call.receive<ContactUsCreateDto>()
                val userId = extractAdminId()
                val result =
                    contactUsDataSource
                        .addContactUsInfo(contactUsRequest.toModel(adminId = userId))
                respondWithSuccessfullyResult(
                    result = result,
                    message = "Contact Us Information inserted successfully ."
                )
            } catch (e: Exception) {
                logger.error { "$CREATE_CONTACT_US error ${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }
        }
        //put contact us //api/v1/admin-client/contact/update
        put("$UPDATE_CONTACT_US/{id}") {
            logger.debug { "put contact US $UPDATE_CONTACT_US" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                val contactUsRequest = call.receive<ContactUsCreateDto>()
                val userId = extractAdminId()

                try {
                    val result =
                        contactUsDataSource
                            .updateContactUsInfo(
                                1,
                                contactUsRequest.toModel(userId)
                            )
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "contact Us Information update successfully .",
                                data = null
                            )
                        )
                        return@put
                    } else {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = false,
                                message = "contact Us Information update failed .",
                                data = null
                            )
                        )
                        return@put
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = e.message ?: "error while update ",
                            data = null
                        )
                    )
                    return@put
                }

            } ?: call.respond(
                HttpStatusCode.BadRequest,
                MyResponse(
                    success = false,
                    message = "Missing parameters .",
                    data = null
                )
            )

        }
        //delete contact us //api/v1/admin-client/contact_us/delete
        delete(DELETE_CONTACT_US) {
            logger.debug { "delete contact US $DELETE_CONTACT_US" }

            try {
                val result = contactUsDataSource.deleteContactUsInfo()
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "About Us Information delete successfully .",
                            data = null
                        )
                    )
                    return@delete
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "About Us Information delete failed .",
                            data = null
                        )
                    )
                    return@delete
                }
            } catch (e: Exception) {

                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "error while delete ",
                        data = null
                    )
                )
                return@delete
            }


        }

    }

}
