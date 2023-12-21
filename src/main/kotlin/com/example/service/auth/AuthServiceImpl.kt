package com.example.service.auth

import com.example.data.administrations.admin_user.UserDataSource
import com.example.models.AdminUser
import com.example.security.hash.HashingService
import com.example.security.hash.SaltedHash
import com.example.security.token.TokenService
import com.example.utils.INVALID_CREDENTIALS
import com.example.utils.InvalidCredentialsException
import com.example.utils.USER_NOT_FOUND
import org.koin.core.annotation.Singleton

@Singleton
class AuthServiceImpl(
    private val userDataSource: UserDataSource,
    private val hashingService: HashingService,
) : AuthService {
    override suspend fun login(username: String, password: String): Boolean {
        try {
            userDataSource.getUserByUsername(username)?.let { adminUser ->
                val isValidPassword = hashingService.verifyHashingPassword(
                    value = password,
                    saltedHash = SaltedHash(
                        hash = adminUser.password,
                        salt = adminUser.salt
                    )
                )
                if (isValidPassword) {
                    return true

                } else {
                    throw throw InvalidCredentialsException(INVALID_CREDENTIALS)
                }

            } ?: throw throw InvalidCredentialsException(INVALID_CREDENTIALS)


        } catch (e: Exception) {

            return false

        }
    }



}