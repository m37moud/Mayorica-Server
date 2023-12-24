package com.example.utils

object Constants {
    const val ENDPOINT = "/api/v1"
    const val ADMIN_CLIENT = "${ENDPOINT}/admin-client"
    const val USER_CLIENT = "${ENDPOINT}/user-client"

    const val SERVER_LOCKED_FILE_NAME = ".serverLock"
}