package com.example.route.client_admin_side

import com.example.data.contact_us.ContactUsDataSource
import com.example.models.AboutUs
import com.example.models.ContactUs
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private const val CONTACT_US = "$ADMIN_CLIENT/contact-us"
private const val CREATE_CONTACT_US = "$CONTACT_US/create"
private const val UPDATE_CONTACT_US = "$CONTACT_US/update"
private const val DELETE_CONTACT_US = "$CONTACT_US/delete"

private val logger = KotlinLogging.logger { }

fun Route.contactUsAdminRoute(
    contactUsDataSource: ContactUsDataSource
) {

    authenticate {
        //get contact us //api/v1/admin-client/contact-us
        get(CONTACT_US) {
            logger.debug { "Get Contact US $CONTACT_US" }
            try {
                contactUsDataSource.getContactUsInfo()?.let {
                    call.respond(
                        status = HttpStatusCode.OK, message = MyResponse(
                            success = true,
                            message = "Contact Us Information Found",
                            data = it
                        )
                    )


                } ?: call.respond(
                    status = HttpStatusCode.NotFound,
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
        //post contact us //api/v1/admin-client/contact-us/create
        post(CREATE_CONTACT_US) {
            logger.debug { "POST contact US $CREATE_CONTACT_US" }
            val contactUsRequest = try {
                call.receive<ContactUs>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Missing Some Fields",
                        data = null
                    )
                )
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = try {
                principal?.getClaim("userId", String::class)?.toIntOrNull()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Transaction Failed ",
                        data = null
                    )
                )
                return@post
            }

            try {
                val result =
                    contactUsDataSource
                        .addContactUsInfo(contactUsRequest.copy(userAdminID = userId!!))
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "contact Us Information inserted successfully .",
                            data = null
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "contact Us Information inserted failed .",
                            data = null
                        )
                    )
                    return@post
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Transaction Failed while adding information",
                        data = null
                    )
                )

            }
        }
        //put contact us //api/v1/admin-client/contact/update
        put("$UPDATE_CONTACT_US/{id}") {
            logger.debug { "put contact US $UPDATE_CONTACT_US" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                val contactUsRequest = try {
                    call.receive<ContactUs>()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Missing Some Fields",
                            data = null
                        )
                    )
                    return@put
                }
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
                    return@put
                }

                try {
                    val result =
                        contactUsDataSource
                            .updateContactUsInfo(
                                contactUsRequest.copy(
                                    id = id,
                                    userAdminID = userId!!
                                )
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
