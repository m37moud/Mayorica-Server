package com.example.data.administrations.admin_user

import com.example.models.AdminUser

interface UserDataSource {

    suspend fun getUserByUsername(username: String): AdminUser?
    suspend fun register(newUser: AdminUser): Int
    suspend fun getAllUser(): List<AdminUser>

    suspend fun isAdmin(id:Int): Boolean

}