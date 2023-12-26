package com.example.service.auth

import com.example.models.AdminUser
import com.example.models.AdminUserDetail
import com.example.models.UserInfo
import com.example.models.request.auth.AdminRegister

interface AuthService {
    suspend fun login(username: String, password: String): Boolean

    suspend fun createUser(password: String?, user: UserInfo): AdminUserDetail

}