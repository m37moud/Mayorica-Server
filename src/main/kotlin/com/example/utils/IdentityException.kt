package com.example.utils

open class IdentityException(message: String) : Throwable(message)

class UserAlreadyExistsException(message: String) : IdentityException(message)
class AlreadyExistsException(message: String) : IdentityException(message)
class ResourceNotFoundException(message: String) : IdentityException(message)

class MissingParameterException(message: String) : IdentityException(message)

class InsufficientFundsException(message: String) : IdentityException(message)

class RequestValidationException(list: List<String>) : IdentityException(list.joinToString(","))

class InvalidCredentialsException(message: String) : IdentityException(message)

class InvalidLocationException(message: String) : IdentityException(message)
class UnknownErrorException(message: String) : IdentityException(message)
class ErrorException(message: String) : IdentityException(message)