package com.example.data.administrations.admin_user

import com.example.models.AdminUser
import com.example.models.AdminUserDetail
import com.example.models.User
import org.ktorm.schema.Column

interface UserDataSource {

    suspend fun getUserByUsername(username: String): AdminUser?
    suspend fun register(newUser: AdminUser): Int
    suspend fun create(newUser: User): Int
    suspend fun getAllUser(): List<AdminUserDetail>
    suspend fun getAllUserPageable(
        query: String?,
        permission: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<AdminUserDetail>

    suspend fun isAdmin(id: Int): Boolean
    suspend fun updatePermission(id: Int, permission: String): Int
    suspend fun deleteAdminUser(id: Int): Int

}