package com.example.route.client_admin_side

import com.example.data.about_us.AboutUsDataSource
import com.example.models.AboutUs
import com.example.models.request.AboutUsRequest
import com.example.models.request.toEntity
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val ABOUT_US = "$ADMIN_CLIENT/about-us"
private const val CREATE_ABOUT_US = "$ABOUT_US/create"
private const val UPDATE_ABOUT_US = "$ABOUT_US/update"
private const val DELETE_ABOUT_US = "$ABOUT_US/delete"
private const val DELETE_ALL_ABOUT_US = "$ABOUT_US/delete-all"
private val logger = KotlinLogging.logger {}

fun Route.aboutUsAdminRoute() {
    val aboutUsDataSource: AboutUsDataSource by inject()

    authenticate {
        //get about us //api/v1/admin-client/about_us
        get(ABOUT_US) {
            logger.debug { "GET ABOUT US $ABOUT_US" }
            try {

                val result = aboutUsDataSource.getAllAboutUsInfo()
                if (result.isNotEmpty()) {
                    respondWithSuccessfullyResult(
                        message = "About Us Information Found",
                        result = result
                    )
                } else {
                    throw NotFoundException("About Us Information Not Found")

                }
            } catch (e: Exception) {
                logger.error { "$ABOUT_US error ${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }
        }
        //get about us //api/v1/admin-client/about_us/{id}
        get("$ABOUT_US/{id}") {
            logger.debug { "GET ABOUT US $ABOUT_US" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->

                try {

                    aboutUsDataSource
                        .getAboutUsInfoById(id = id)?.let {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = MyResponse(
                                    success = true,
                                    message = "About Us Information Found",
                                    data = it
                                )
                            )


                        } ?: call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = false,
                            message = "About Us Information Not Found",
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
            } ?: call.respond(
                HttpStatusCode.BadRequest,
                MyResponse(
                    success = false,
                    message = "Missing parameters .",
                    data = null
                )
            )
        }
        //post about us //api/v1/admin-client/about_us/create
        post(CREATE_ABOUT_US) {
            logger.debug { "POST ABOUT US $CREATE_ABOUT_US" }
            val aboutUsRequest = call.receive<AboutUsRequest>()
            val userId = extractAdminId()

            try {
                val createdAboutUs = aboutUsDataSource.addAboutUs(aboutUsRequest.toEntity(userId))
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = createdAboutUs,
                    message = "Color Category inserted successfully ."
                )
            } catch (e: Exception) {
                logger.error { "$CREATE_ABOUT_US error ${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")


            }
        }
        //put about us //api/v1/admin-client/about_us/update
        put("$UPDATE_ABOUT_US/{id}") {
            logger.debug { "put ABOUT US $UPDATE_ABOUT_US" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                val aboutUsRequest = try {
                    call.receive<AboutUs>()
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
                        aboutUsDataSource
                            .updateAboutUs(
                                aboutUsRequest.copy(
                                    id = id,
                                    userAdminID = userId!!
                                )
                            )
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "About Us Information update successfully .",
                                data = null
                            )
                        )
                        return@put
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "About Us Information update failed .",
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
        //delete about us //api/v1/admin-client/about_us/delete
        delete("$DELETE_ABOUT_US/{id}") {
            logger.debug { "delete ABOUT US $DELETE_ABOUT_US" }

            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    val result = aboutUsDataSource.deleteAboutUs(id = id)
                    if (result > 0) {
                        respondWithSuccessfullyResult(
                            result = true,
                            message = "About Us Information delete successfully ."
                        )
                    } else {
                        throw UnknownErrorException("About Us Information delete failed .")
                    }
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "$DELETE_ABOUT_US error ${exc.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")
            }


        }
        //delete about us //api/v1/admin-client/about_us/delete-all
        delete(DELETE_ALL_ABOUT_US) {
            logger.debug { "delete all ABOUT US $DELETE_ALL_ABOUT_US" }

            try {
                val result = aboutUsDataSource.deleteAllAboutUs()
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "All About Us Information delete successfully .",
                            data = null
                        )
                    )
                    return@delete
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "All About Us Information delete failed .",
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
