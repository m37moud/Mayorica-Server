package com.example.route.client_user_side

import com.example.data.ceramic_provider.CeramicProviderDataSource
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getNearlyProvider(ceramicProvider: CeramicProviderDataSource) {
    post(NEARLY_LOCATION) {
        val latitude: Double
        val longitude: Double
        try {
            latitude = call.request.queryParameters["latitude"]!!.toDouble()
            longitude = call.request.queryParameters["longitude"]!!.toDouble()
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadGateway,
                MyResponse(
                    success = false,
                    message = "Missing Some Failed",
                    data = null
                )
            )
            return@post
        }

        try {

            val providers = ceramicProvider.getNearlyProvider(latitude, longitude)
            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = true,
                    message = "success",
                    data = providers
                )
            )
            return@post

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = e.message ?: "Failed",
                    data = null
                )
            )
            return@post
        }


    }

}