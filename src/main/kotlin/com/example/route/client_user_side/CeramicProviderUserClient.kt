package com.example.route.client_user_side

import com.example.data.ceramic_provider.CeramicProviderDataSource
import com.example.mapper.toUserResponse
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


private const val PROVIDERS = "${USER_CLIENT}/providers"
private const val PROVIDERS_NEARLY_LOCATION = "${PROVIDERS}/nearlyLocation"
private const val PROVIDERS_GOVERNORATE = "${PROVIDERS}/governorate"
private const val PROVIDERS_SEARCH = "${PROVIDERS}/search"


fun Route.getNearlyProvider(
//    ceramicProvider: CeramicProviderDataSource
) {
    val ceramicProvider: CeramicProviderDataSource by inject()

    get(PROVIDERS) {
        try {
            val providers = ceramicProvider.getAllCeramicProvider()
            if (providers.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Get All Ceramic Providers Successfully",
                        data = providers.toUserResponse()
                    )
                )
                return@get

            } else {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "No Ceramic Providers Is Found",
                        data = null
                    )
                )
                return@get

            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = e.message ?: "Failed",
                    data = null
                )
            )
            return@get
        }
    }

    get(PROVIDERS_GOVERNORATE) {
        call.request.queryParameters["governorate"]?.let { governorate ->

            try {
                val providers = ceramicProvider.getCeramicProviderByGovernorate(governorate = governorate)
                if (providers.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Get All Ceramic Providers Successfully",
                            data = providers
                        )
                    )
                    return@get

                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "No Ceramic Providers Is Found",
                            data = null
                        )
                    )
                    return@get

                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@get
            }

        } ?: call.respond(
            HttpStatusCode.BadGateway,
            MyResponse(
                success = false,
                message = "Missing Some Failed",
                data = null
            )
        )

    }
    get(PROVIDERS_SEARCH) {
        call.request.queryParameters["search"]?.let { search ->

            try {
                val providers = ceramicProvider.getCeramicProviderBySearching(searchValue = search)
                if (providers.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Get All Ceramic Providers Successfully",
                            data = providers
                        )
                    )
                    return@get

                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "No Ceramic Providers Is Found",
                            data = null
                        )
                    )
                    return@get

                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@get
            }

        }?: call.respond(
            HttpStatusCode.BadGateway,
            MyResponse(
                success = false,
                message = "Missing Some Failed",
                data = null
            )
        )
    }

    get(PROVIDERS_NEARLY_LOCATION) {
        call.request.queryParameters["latitude"]?.toDoubleOrNull()?.let { latitude ->

            val longitude = call.request.queryParameters["longitude"]?.toDoubleOrNull() ?: 0.0
            try {

                val providers = ceramicProvider.getNearlyProvider(latitude, longitude)

                if (providers.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Get All Ceramic Providers Successfully",
                            data = providers.toUserResponse()
                        )
                    )
                    return@get

                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "No Ceramic Providers Is Found",
                            data = null
                        )
                    )
                    return@get

                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@get
            }

        } ?: call.respond(
            HttpStatusCode.BadGateway,
            MyResponse(
                success = false,
                message = "Missing Some Failed",
                data = null
            )
        )


    }


}