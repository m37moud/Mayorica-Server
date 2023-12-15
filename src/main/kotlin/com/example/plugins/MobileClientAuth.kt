package com.example.plugins

import com.example.data.administrations.apps.admin.AppsAdminDataSource
import com.example.models.response.toResponse
import dev.forst.ktor.apikey.apiKey
import io.ktor.server.auth.*

fun AuthenticationConfig.configureMobileAuthority(app: AppsAdminDataSource) {
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