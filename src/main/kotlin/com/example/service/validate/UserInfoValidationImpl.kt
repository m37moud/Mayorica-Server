package com.example.service.validate

import com.example.models.UserInfo
import com.example.utils.*
import org.koin.core.annotation.Singleton

@Singleton
class UserInfoValidationImpl :UserInfoValidation {
    override fun validateUserInformation(user: UserInfo, password: String?) {
        val reasons = mutableListOf<String>()

        if (!validateUsernameIsNotEmpty(user.username)) {
            reasons.add(USERNAME_CANNOT_BE_BLANK)
        }

        if (!validateUsername(user.username)) {
            reasons.add(INVALID_USERNAME)
        }

        if (!validateFullNameIsNotEmpty(user.fullName)) {
            reasons.add(INVALID_FULLNAME)
        }

        if (password == null) {
            reasons.add(INVALID_REQUEST_PARAMETER)
        }

//        if (user.phone.isBlank()) {
//            reasons.add(INVALID_REQUEST_PARAMETER)
//        } else if (!isValidPhone(user.phone)) {
//            reasons.add(INVALID_PHONE)
//        }

        password?.let {
            if (!validatePasswordIsNotEmpty(password)) {
                reasons.add(PASSWORD_CANNOT_BE_BLANK)
            }
            if (!validatePasswordLength(password)) {
                reasons.add(PASSWORD_CANNOT_BE_LESS_THAN_8_CHARACTERS)
            }
        }

//        if (!validateEmail(user.email)) {
//            reasons.add(INVALID_EMAIL)
//        }

        if (reasons.isNotEmpty()) {
            throw RequestValidationException(reasons)
        }
    }

    override fun validateUpdateUserInformation(fullName: String?, phone: String?) {
        val reasons = mutableListOf<String>()

        fullName?.let {
            if (!validateFullNameIsNotEmpty(it)) {
                reasons.add(INVALID_FULLNAME)
            }
        }

        phone?.let {
            if (!isValidPhone(phone)) {
                reasons.add(INVALID_EMAIL)
            }
        }

        if (reasons.isNotEmpty()) {
            throw RequestValidationException(reasons)
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        val phoneRegexMap = mapOf(
            "EGP" to "^\\+20\\d{10}$".toRegex(),
            "IQD" to "^\\+964\\d{10}$".toRegex(),
            "SYP" to "^\\+963\\d{9}$".toRegex(),
            "ILS" to "^\\+972([59])\\d{8}$".toRegex(),
            "US" to "^\\+1\\d{10}$".toRegex()
        )
        return phoneRegexMap.values.any { it.matches(phone) }
    }

    override fun validateUsernameIsNotEmpty(username: String): Boolean = username.isNotBlank()

    override fun validateUsername(username: String): Boolean {
        val validUserNameRegex = "[a-zA-Z0-9_]+".toRegex()
        return username.matches(validUserNameRegex)
    }

    override fun validateFullNameIsNotEmpty(fullName: String): Boolean = fullName.isNotBlank()

    override fun validatePasswordIsNotEmpty(password: String): Boolean = password.isNotBlank()

    override fun validatePasswordLength(password: String): Boolean = password.length > 8

    override fun validateEmail(email: String): Boolean {
        val validEmailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
        return email.matches(validEmailRegex)
    }
}