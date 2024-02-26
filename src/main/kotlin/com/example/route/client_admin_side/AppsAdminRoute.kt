package com.example.route.client_admin_side

import com.example.data.administrations.apps.admin.AppsAdminDataSource
import com.example.data.administrations.apps.user.AppsUserDataSource
import com.example.models.AppsModel
import com.example.models.MyResponsePageable
import com.example.models.options.getAppsOptions
import com.example.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject


private val logger = KotlinLogging.logger {}

/**
 * USER APP
 */
private const val ADMIN_APP = "${Constants.ADMIN_CLIENT}/admin-app"
private const val ALL_ADMIN_APP_PAGEABLE = "${ADMIN_APP}-pageable"
private const val CREATE_ADMIN_APP = "${ADMIN_APP}/create"
private const val UPDATE_ADMIN_APP = "${ADMIN_APP}/update"
private const val DELETE_ADMIN_APP = "${ADMIN_APP}/delete"

/**
 * USER APP
 */
private const val USER_APP = "${Constants.ADMIN_CLIENT}/user-app"
private const val ALL_USER_APP_PAGEABLE = "${USER_APP}-pageable"

private const val CREATE_USER_APP = "${USER_APP}/create"
private const val UPDATE_USER_APP = "${USER_APP}/update"
private const val DELETE_USER_APP = "${USER_APP}/delete"


fun Route.appsAdminRoute() {
    val appsAdminDataSource: AppsAdminDataSource by inject()
    val appsUserDataSource: AppsUserDataSource by inject()

    authenticate {

        /**
         * USER APP
         */


        //get all -> api/v1/admin-client/admin-app/{id}
        get("$ADMIN_APP/{id}") {
            logger.debug { "get youtube Links $ADMIN_APP/{id}" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
                    val link = appsAdminDataSource.getAppInfo(appId = id)
                    if (link != null) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = true,
                                message = "get app successfully",
                                data = link
                            )
                        )

                    } else {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            message = MyResponse(
                                success = false,
                                message = "app is not found",
                                data = null
                            )
                        )


                    }
                } catch (e: Exception) {
                    logger.error { "get youtube Link error ${e.stackTrace}" }
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = false,
                            message = e.message ?: "failed to get ",
                            data = null
                        )
                    )


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
        //post  -> api/v1/admin-client/admin-app/create
        post(CREATE_ADMIN_APP) {
            logger.debug { "create new app $CREATE_ADMIN_APP" }

            val appRequest = try {
                call.receive<AppsModel>()

            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = MyResponse(
                        success = false, message = e.message ?: "Missing Some Fields",
                        data = null
                    )
                )
                return@post
            }



            try {//check if this app is inserted before

                appsAdminDataSource
                    .getAppInfo(packageName = appRequest.packageName)?.let {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = false,
                                message = "this app inserted before",
                                data = null
                            )
                        )
                        return@post


                    } ?: run { // app not found try to insert new one
                    val principal = call.principal<JWTPrincipal>()
                    val adminUserId = try {
                        principal?.getClaim("userId", String::class)?.toIntOrNull()
                    } catch (e: Exception) {
                        call.respond(
                            status = HttpStatusCode.Conflict,
                            message = MyResponse(
                                success = false,
                                message = e.message ?: "Missing Some Fields",
                                data = null
                            )
                        )
                        return@post
                    }

                    val result = appsAdminDataSource
                        .appCreate(appRequest.copy(userAdminId = adminUserId ?: -1))
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "App Information inserted successfully .",
                                data = appRequest
                            )
                        )
                        return@post
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "App Information inserted failed .",
                                data = null
                            )
                        )
                        return@post
                    }
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "error while insert ",
                        data = null
                    )
                )
                return@post
            }

        }
        //put  -> api/v1/admin-client/admin-app/update
        put("$UPDATE_ADMIN_APP/{id}") {
            logger.debug { "update apps $UPDATE_ADMIN_APP" }

            call.parameters["id"]?.toIntOrNull()?.let {
                val linkRequest = try {
                    call.receive<AppsModel>()
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
                    val result = appsAdminDataSource.appUpdate(linkRequest.copy(userAdminId = userId!!))
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = " Youtube Link Information update successfully .",
                                data = linkRequest
                            )
                        )
                        return@put
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "Youtube Link Information update failed .",
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
        //delete  -> api/v1/admin-client/admin-app/delete
        delete("$DELETE_ADMIN_APP/{id}") {
            logger.debug { "delete app $DELETE_ADMIN_APP" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
                    val result = appsAdminDataSource.appDelete(appId = id)
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "Youtube Link Information delete successfully .",
                                data = null
                            )
                        )
                        return@delete
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "Youtube Link Information delete failed .",
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


        /**
         * USER APP
         */
        get(ALL_USER_APP_PAGEABLE) {
            logger.debug { "GET ALL /$ALL_USER_APP_PAGEABLE" }

            try {
                val params = call.request.queryParameters
                val appsOption = getAppsOptions(params)
                val appsList =
                    appsUserDataSource
                        .getAllAppsPageable(
                            query = appsOption.query,
                            page = appsOption.page!!,
                            perPage = appsOption.perPage!!,
                            sortField = appsOption.sortFiled!!,
                            sortDirection = appsOption.sortDirection!!
                        )
                if (appsList.isEmpty()) throw NotFoundException("no apps is found.")
                val numberOfNews = appsUserDataSource.getNumberOfApps()

                respondWithSuccessfullyResult(
                    result = MyResponsePageable(
                        page = appsOption.page + 1,
                        perPage = numberOfNews,
                        data = appsList
                    ),
                    message = "get all apps successfully"
                )
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }


        }

        //get all -> api/v1/admin-client/user-app/{id}
        get("$USER_APP/{id}") {
            logger.debug { "get user App Client $USER_APP/{id}" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
                    val link = appsUserDataSource.getAppInfo(appId = id)
                    if (link != null) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = true,
                                message = "get app successfully",
                                data = link
                            )
                        )

                    } else {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            message = MyResponse(
                                success = false,
                                message = "app is not found",
                                data = null
                            )
                        )


                    }
                } catch (e: Exception) {
                    logger.error { "get youtube Link error ${e.stackTrace}" }
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = false,
                            message = e.message ?: "failed to get ",
                            data = null
                        )
                    )


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
        //post  -> api/v1/admin-client/user-app/create
        post(CREATE_USER_APP) {
            logger.debug { "create new user app $CREATE_USER_APP" }

            val appRequest = try {
                call.receive<AppsModel>()

            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = MyResponse(
                        success = false, message = e.message ?: "Missing Some Fields",
                        data = null
                    )
                )
                return@post
            }
            logger.debug { "appRequest $appRequest" }



            try {//check if this app is inserted before

                appsUserDataSource
                    .getAppInfo(packageName = appRequest.packageName)?.let {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = false,
                                message = "this app inserted before",
                                data = null
                            )
                        )
                        return@post


                    } ?: run { // app not found try to insert new one
                    val principal = call.principal<JWTPrincipal>()
                    val adminUserId = try {
                        principal?.getClaim("userId", String::class)?.toIntOrNull()
                    } catch (e: Exception) {
                        call.respond(
                            status = HttpStatusCode.Conflict,
                            message = MyResponse(
                                success = false,
                                message = e.message ?: "Missing Some Fields",
                                data = null
                            )
                        )
                        return@post
                    }

                    val result = appsUserDataSource
                        .appCreate(appRequest.copy(userAdminId = adminUserId ?: -1))
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "App Information inserted successfully .",
                                data = appRequest
                            )
                        )
                        return@post
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "App Information inserted failed .",
                                data = null
                            )
                        )
                        return@post
                    }
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "error while insert ",
                        data = null
                    )
                )
                return@post
            }

        }
        //put  -> api/v1/admin-client/user-app/update
        put("$UPDATE_USER_APP/{id}") {
            logger.debug { "update USER apps $UPDATE_USER_APP" }

            call.parameters["id"]?.toIntOrNull()?.let {
                val linkRequest = try {
                    call.receive<AppsModel>()
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
                    val result = appsUserDataSource.appUpdate(linkRequest.copy(userAdminId = userId!!))
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = " Youtube Link Information update successfully .",
                                data = linkRequest
                            )
                        )
                        return@put
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "Youtube Link Information update failed .",
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
        //delete  -> api/v1/admin-client/app/delete
        delete("$DELETE_USER_APP/{id}") {
            logger.debug { "delete app $DELETE_USER_APP" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    val result = appsUserDataSource.appDelete(appId = id)
                    if (result > 0) {
                        respondWithSuccessfullyResult(
                            result = true,
                            message = "App deleted successfully ."
                        )
                    } else {
                        throw UnknownErrorException("failed to delete App .")
                    }
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "$DELETE_USER_APP error ${exc.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")

            }


        }
    }


}