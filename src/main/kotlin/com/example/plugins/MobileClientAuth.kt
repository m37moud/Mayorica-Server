package com.example.plugins

import com.example.data.administrations.apps.AppsDataSource
import com.example.models.response.toResponse
import dev.forst.ktor.apikey.apiKey
import io.ktor.server.auth.*

fun AuthenticationConfig.configureMobileAuthority(app: AppsDataSource) {
    apiKey("mobile") {
        validate { keyFromHeader ->

            val mobileApp = app.getUserWithApp(keyFromHeader)
            if (mobileApp != null) {
                AppPrincipal(mobileApp.toResponse())
            } else
                null

        }
//        challenge {
//
//            throw AuthenticationException()
//        }

    }


}