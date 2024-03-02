package com.example.plugins

import com.example.data.administrations.apps.admin.AppsAdminDataSource
import com.example.models.response.toResponse
import dev.forst.ktor.apikey.apiKey
import io.ktor.server.auth.*
import mu.KotlinLogging


fun AuthenticationConfig.configureMobileAuthority(app: AppsAdminDataSource) {
    basic("mobile") {

        validate { credentials ->
            val mobileApp = app.getAppDetailByKeyAndPackageName(
                packageName = credentials.name,
                apiKey = credentials.password
            )

            if (mobileApp != null) {
                AppPrincipal(mobileApp.toResponse())
            } else {
                null
            }
        }
    }

//    apiKey("mobile") {
//
//        validate { keyFromHeader ->
//            println("configureMobileAuthority keyFromHeader = $keyFromHeader")
//            // Decode the encoded string to a byte array
//            val bytes = Base64.decode(keyFromHeader, Base64.DEFAULT)
//            // Convert the byte array to a string
//            val credentials = String(bytes, Charsets.UTF_8)
//            // Split the string by the colon
//            val parts = credentials.split(":")
//            val packageName = keyFromHeader.substringBeforeLast("-")
//            val apiKey = keyFromHeader.substringAfterLast("-")
//            val mobileApp = app.getUserWithApp(keyFromHeader)
//            if (mobileApp != null) {
//                AppPrincipal(mobileApp.toResponse())
//            } else
//                null
//
//        }
////        challenge {
////
////            throw AuthenticationException()
////        }
//
//    }


}