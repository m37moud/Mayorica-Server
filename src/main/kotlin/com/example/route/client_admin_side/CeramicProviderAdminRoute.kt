package com.example.route.client_admin_side

import com.example.data.ceramic_provider.CeramicProviderDataSource
import com.example.database.table.CeramicProviderEntity
import com.example.mapper.toModel
import com.example.mapper.toEntity
import com.example.models.MyResponsePageable
import com.example.models.dto.ProviderCreateDto
import com.example.models.request.ceramic_provider.CeramicProviderRequest
import com.example.utils.Claim.USER_ID
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import mu.KotlinLogging
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

// get all providers
private const val PROVIDERS = "$ADMIN_CLIENT/providers"
private const val PROVIDERS_PAGEABLE = "$PROVIDERS-pageable"

// get single provider
private const val PROVIDER = "$ADMIN_CLIENT/provider"
private const val PROVIDER_LOCATIONS = "$PROVIDER/locations"
private const val CREATE_PROVIDER = "$PROVIDER/create"
private const val UPDATE_PROVIDER = "$PROVIDER/update"
private const val DELETE_PROVIDER = "$PROVIDER/delete"
private val logger = KotlinLogging.logger {}


fun Route.providerAdminClient() {
    val ceramicProvider: CeramicProviderDataSource by inject()

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

        //get all providers locations fo filtering //api/v1/admin-client/provider/locations
        get(PROVIDER_LOCATIONS) {
            logger.debug { "get all locations PROVIDER_LOCATIONS" }
            try {
                val result = ceramicProvider.getAllLocations()
                if (result.isNotEmpty()) {
                    return@get call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = true,
                            message = "get all locations successfully",
                            data = result
                        )
                    )

                } else {
                    return@get call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = false,
                            message = "no locations is found",
                            data = null
                        )
                    )


                }
            } catch (e: Exception) {
                return@get call.respond(
                    status = HttpStatusCode.Conflict,
                    message = MyResponse(
                        success = false,
                        message = e.message ?: "Some Thing Goes Wrong",
                        data = null
                    )
                )

            }
        }
        //get all pageable provides //api/v1/admin-client/provides-pageable
        get(PROVIDERS_PAGEABLE) {
            logger.debug { "get pageable providers $PROVIDERS_PAGEABLE" }
            val params = call.request.queryParameters
            params["page"]?.toIntOrNull()?.let { pageNum ->
                val page = if (pageNum > 0) pageNum - 1 else 0
                val perPage = params["perPage"]?.toIntOrNull() ?: 10
                val sortField = when (params["sort_by"] ?: "date") {
                    "name" -> CeramicProviderEntity.name
                    "date" -> CeramicProviderEntity.createdAt
                    else -> {
                        return@get call.respond(
                            status = HttpStatusCode.Conflict,
                            message = MyResponse(
                                success = false,
                                message = "invalid parameter for sort_by chose between (name & date)",
                                data = null
                            )
                        )
                    }

                }
                val sortDirections = when (params["sort_direction"] ?: "dec") {
                    "dec" -> -1
                    "asc" -> 1
                    else -> {
                        return@get call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = MyResponse(
                                success = false,
                                message = "invalid parameter for sort_direction chose between (dec & asc)",
                                data = null
                            )
                        )
                    }

                }
                val query = params["query"]?.trim() ?: ""
//                val country = params["byCountry"]?.trim() ?: ""
//                val governorate = params["byGovernorate"]?.trim() ?: ""
                try {
                    logger.debug { "GET ALL Pageable Providers /$PROVIDERS_PAGEABLE?page=$page&perPage=$perPage" }

                    val providers = ceramicProvider.getAllCeramicProviderPageable(
                        page = page,
                        perPage = perPage,
                        searchQuery = query ?: "",
//                        byCountry = country,
//                        byGovernorate = governorate,
                        sortField = sortField,
                        sortDirection = sortDirections

                    )
                    if (providers.isNotEmpty()) {
                        val numberOfProviders = ceramicProvider.getNumberOfProviders()
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get all providers successfully",
                                data = MyResponsePageable(
                                    page = page + 1,
                                    perPage = numberOfProviders,
                                    data = providers
                                )
                            )
                        )
                        return@get

                    } else {
                        return@get call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "No Providers is found",
                                data = null
                            )
                        )
                    }

                } catch (e: Exception) {
                    return@get call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = e.message ?: "Failed To Get Providers",
                            data = null
                        )
                    )
                }


            }

        }
        //get single providers //api/v1/admin-client/provider/6
        get("$PROVIDER/{id}") {
            logger.debug { "get /$PROVIDER/{id}" }
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let {
                ceramicProvider.getCeramicProviderByIdDto(it)?.let { provider ->
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
                    call.receive<ProviderCreateDto>()
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
                val principal = call.principal<JWTPrincipal>()
                val adminUserId = try {
                    principal?.getClaim(USER_ID, String::class)?.toIntOrNull()
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
                    val updateResult = ceramicProvider.updateCeramicProvider(
                        providerId = providerId,
                        adminUserId = adminUserId ?: -1,
                        updateProvider.toModel()
                    )
                    if (updateResult > 0) {
                        val provider = ceramicProvider.getCeramicProviderByIdDto(id = providerId)

                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "provider update successfully .",
                                data = provider
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
                } catch (e: Exception) {
                    return@put call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = e.message ?: "Update Failed",
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
                    principal?.getClaim(USER_ID, String::class)?.toIntOrNull()
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
                    val result = ceramicProvider.addCeramicProvider(providerRequest.toEntity(userId!!))
                    if (result > 0) {
                        val createdProvider = ceramicProvider.getCeramicProviderByNameDto(providerRequest.name)
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "provider inserted successfully .",
                                data = createdProvider
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
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "delete provider success .",
                            data = null
                        )
                    )
                    return@delete
                } else {
                    call.respond(
                        HttpStatusCode.OK,
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