package com.example.data.admin_user

import com.example.models.AdminUser

interface UserDataSource {

    suspend fun getUserByUsername(username: String): AdminUser?
    suspend fun register(newUser: AdminUser): Int
    suspend fun getAllUser(): List<AdminUser>

}