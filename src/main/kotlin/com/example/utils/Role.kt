package com.example.utils

object Role {
    const val END_USER = 1
    const val DASHBOARD_ADMIN = 2
    const val RESTAURANT_OWNER = 4
    const val TAXI_DRIVER = 8
    const val SUPPORT = 16
    const val DELIVERY = 32
}

object Claim {
    const val USER_ID = "userId"
    const val PERMISSION = "userRole"
    const val USERNAME = "username"
    const val FULL_NAME = "fullName"
    const val CREATED_AT = "createdAt"
    const val TOKEN_TYPE = "tokenType"
}