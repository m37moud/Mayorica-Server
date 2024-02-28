package com.example.data.administrations.admin_user

import com.example.models.*
import com.example.security.hash.SaltedHash
import org.ktorm.schema.Column

interface UserDataSource {

    suspend fun getUserDetailByUsername(username: String): AdminUserDetail?
    suspend fun getUserDetailById(userId: Int): AdminUserDetail?
    suspend fun getAdminUserByUsername(username: String): AdminUser?
    suspend fun getHashed(username: String): SaltedHash

    suspend fun register(newUser: AdminUser): Int
    suspend fun create(newUser: User): Int
    suspend fun getAllUser(): List<AdminUserDetail>
    suspend fun getNumberOUsers(): Int
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

    suspend fun updateUserProfile(id: Int, userProfileInfo: UpdateUserProfileInfo): Int
    suspend fun updateUserPassword(id: Int, userPasswordInfo: UpdateUserPasswordInfo): Int


}