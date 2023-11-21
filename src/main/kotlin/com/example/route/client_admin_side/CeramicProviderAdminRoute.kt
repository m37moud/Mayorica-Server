package com.example.route.client_admin_side

import com.example.data.ceramic_provider.CeramicProviderDataSource
import com.example.mapper.toModelCreate
import com.example.models.CeramicProvider
import com.example.models.request.ceramic_provider.CeramicProviderRequest
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import com.example.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import mu.KotlinLogging
import io.ktor.server.routing.*
import java.time.LocalDateTime

// get all providers
const val PROVIDERS = "$ADMIN_CLIENT/providers"

// get single provider
const val PROVIDER = "$ADMIN_CLIENT/provider"
const val CREATE_PROVIDER = "$PROVIDER/create"
const val UPDATE_PROVIDER = "$PROVIDER/update"
const val DELETE_PROVIDER = "$PROVIDER/delete"
private val logger = KotlinLogging.logger {}


fun Route.providerAdminClient(
    ceramicProvider: CeramicProviderDataSource
) {
    authenticate {
        //get all providers //api/v1/admin-client/providers
        get(PROVIDERS) {
            logger.debug { "get /$PROVIDERS" }
            try {
                val providersList = ceramicProvider.getAllCeramicProvider()
                if (providersList.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "get providers successfully .",
                            data = providersList
                        )
                    )
                    return@get

                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "no providers found .",
                            data = null
                        )
                    )
                    return@get

                }

            } catch (exc: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = exc.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }

        }
        //get single providers //api/v1/admin-client/provider/6
        get("$PROVIDER/{id}") {
            logger.debug { "get /$PROVIDER/{id}" }
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let {
                ceramicProvider.getCeramicProviderByID(it)?.let { provider ->
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "provider is found .",
                            data = provider
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "no provider found .",
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


        }
        //put update providers //api/v1/admin-client/provider/update/6
        put("$UPDATE_PROVIDER/{id}") {
            logger.debug { "put /$UPDATE_PROVIDER/{id}" }
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let { providerId ->
                val updateProvider = try {
                    call.receive<CeramicProvider>()
                } catch (exc: Exception) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = exc.message ?: "update Failed .",
                            data = null
                        )
                    )
                    return@put
                }
                ceramicProvider.getCeramicProviderByID(id = providerId)?.let { temp ->
                    val updateResult = ceramicProvider.updateCeramicProvider(
                        providerId = providerId,
                        updateProvider
                    )
                    if (updateResult > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "provider update successfully .",
                                data = null
                            )
                        )
                        return@put

                    } else {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = false,
                                message = "no provider found .",
                                data = null
                            )
                        )
                        return@put

                    }
                } ?: call.respond(
                    HttpStatusCode.NotFound,
                    MyResponse(
                        success = false,
                        message = "no provider found .",
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


        }
        //post all providers //api/v1/admin-client/provider/create
        post(CREATE_PROVIDER) {
            logger.debug { "POST /$CREATE_PROVIDER" }

            val providerRequest = try {
                call.receive<CeramicProviderRequest>()
            } catch (exc: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest, MyResponse(
                        success = false,
                        message = exc.message ?: "Missing Some Fields",
                        data = null
                    )
                )
                return@post
            }

            try {
                val provider = ceramicProvider.getCeramicProviderByName(providerRequest.name)
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
                if (provider == null) {
                    val result = ceramicProvider.addCeramicProvider(providerRequest.toModelCreate(userId!!))
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "provider inserted successfully .",
                                data = providerRequest.name
                            )
                        )
                        return@post
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "provider inserted failed .",
                                data = null
                            )
                        )
                        return@post
                    }
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "provider inserted before .",
                            data = null
                        )
                    )
                    return@post

                }
            } catch (exc: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = exc.message ?: "Creation Failed .",
                        data = null
                    )
                )
                return@post
            }

        }
        //put update providers //api/v1/admin-client/provider/delete/6
        delete("$DELETE_PROVIDER/{id}") {
            logger.debug { "delete /$DELETE_PROVIDER/{id}" }
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let {
                val result = ceramicProvider.deleteCeramicProvider(id)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        MyResponse(
                            success = true,
                            message = "delete provider success .",
                            data = null
                        )
                    )
                    return@delete
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        MyResponse(
                            success = false,
                            message = "fail to delete provider .",
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
    }


}