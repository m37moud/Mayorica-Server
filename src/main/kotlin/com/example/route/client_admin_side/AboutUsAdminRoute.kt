package com.example.route.client_admin_side

import com.example.data.about_us.AboutUsDataSource
import com.example.models.AboutUs
import com.example.models.request.AboutUsRequest
import com.example.models.request.categories.ColorCategoryRequest
import com.example.models.request.toModel
import com.example.utils.Constants
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

private const val ABOUT_US = "$ADMIN_CLIENT/about_us"
private const val CREATE_ABOUT_US = "$ABOUT_US/create"
private const val UPDATE_ABOUT_US = "$ABOUT_US/update"
private const val DELETE_ABOUT_US = "$ABOUT_US/delete"
private const val DELETE_ALL_ABOUT_US = "$ABOUT_US/delete-all"
private val logger = KotlinLogging.logger {}

fun Route.aboutUsAdminRoute(aboutUsDataSource: AboutUsDataSource) {
    authenticate {
        //get about us //api/v1/admin-client/about_us
        get(ABOUT_US) {
            logger.debug { "GET ABOUT US $ABOUT_US" }
            try {

                val result = aboutUsDataSource.getAllAboutUsInfo()
                if (result.isNotEmpty()) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = true,
                            message = "About Us Information Found",
                            data = result
                        )
                    )
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = MyResponse(
                            success = false,
                            message = "About Us Information Not Found",
                            data = null
                        )
                    )
                }
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
                        status = HttpStatusCode.NotFound,
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
            val aboutUsRequest = try {
                call.receive<AboutUs>()
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
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@post
            }

//            val result = aboutUsDataSource.createAboutUs(aboutUsRequest.toModel(userId!!))
            try {
                val result = aboutUsDataSource.createAboutUs(aboutUsRequest.copy(userAdminID = userId!!))
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "About Us Information inserted successfully .",
                            data = null
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "About Us Information inserted failed .",
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
            call.parameters["id"]?.toIntOrNull()?.let { id ->

                try {
                    val result = aboutUsDataSource.deleteAboutUs(id = id)
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

            } ?: call.respond(
                HttpStatusCode.BadRequest,
                MyResponse(
                    success = false,
                    message = "Missing parameters .",
                    data = null
                )
            )
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
