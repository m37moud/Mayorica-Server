package com.example.service.auth

import com.example.data.administrations.admin_user.UserDataSource
import com.example.models.AdminUserDetail
import com.example.models.UserInfo
import com.example.models.mapper.toModel
import com.example.security.hash.HashingService
import com.example.security.hash.SaltedHash
import com.example.service.validate.UserInfoValidation
import com.example.utils.*
import org.koin.core.annotation.Singleton

@Singleton
class AuthServiceImpl(
    private val userDataSource: UserDataSource,
    private val hashingService: HashingService,
    private val userInfoValidationUseCase: UserInfoValidation
) : AuthService {
    override suspend fun login(username: String, password: String): Boolean {
        try {
            userDataSource.getAdminUserByUsername(username)?.let { adminUser ->
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
                     throw InvalidCredentialsException(INVALID_CREDENTIALS)
                }

            } ?:  throw InvalidCredentialsException(INVALID_CREDENTIALS)


        } catch (e: Exception) {

            return false

        }
    }

    override suspend fun createUser(password: String?, user: UserInfo): AdminUserDetail {
        userInfoValidationUseCase.validateUserInformation(password = password, user = user)
        if (password == null) {
            throw RequestValidationException(listOf(INVALID_REQUEST_PARAMETER))
        }
         userDataSource.getUserDetailByUsername(user.username)?.let {
            throw UserAlreadyExistsException(USER_ALREADY_EXISTS)
        }
        val saltedHash = hashingService.createHashingPassword(password)

        val newUser = userDataSource.register(newUser = user.toModel(saltedHash))

        val userDetails = userDataSource.getAdminUserByUsername(user.username)?.toModel()
        return userDetails!!
    }


}