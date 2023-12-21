package com.example.service.auth

import com.example.models.AdminUser

interface AuthService {
    suspend fun login(username: String, password: String): Boolean

}