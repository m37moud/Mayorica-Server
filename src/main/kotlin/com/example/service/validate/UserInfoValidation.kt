package com.example.service.validate

import com.example.models.UserInfo
import com.example.models.request.auth.AdminRegister

interface UserInfoValidation {
    fun validateUserInformation(user: UserInfo, password: String?)

    fun validateUpdateUserInformation(fullName: String?, phone: String?)

    fun validateUsernameIsNotEmpty(username: String): Boolean

    fun validateFullNameIsNotEmpty(fullName: String): Boolean

    fun validatePasswordIsNotEmpty(password: String): Boolean

    fun validateUsername(username: String): Boolean

    fun validatePasswordLength(password: String): Boolean

    fun validateEmail(email: String): Boolean
}