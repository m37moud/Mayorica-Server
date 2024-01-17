package com.example.utils

import com.example.data.localizedMessages.Language
import com.example.models.MultipartDto
import com.example.utils.Claim.PERMISSION
import com.example.utils.Claim.USER_ID
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json

suspend inline fun <reified T> PipelineContext<Unit, ApplicationCall>.respondWithResult(
    statusCode: HttpStatusCode, result: T, message: String? = null
) {
    call.respond(statusCode, ServerResponse.success(result, message))
}

suspend fun respondWithError(
    call: ApplicationCall, statusCode: HttpStatusCode, errorMessage: Map<Int, String>? = null
) {
    call.respond(statusCode, ServerResponse.error(errorMessage, statusCode.value))
}

fun PipelineContext<Unit, ApplicationCall>.extractLocalizationHeader(): String {
    val headers = call.request.headers
    return headers["Accept-Language"]?.trim() ?: Language.ENGLISH.code
}

fun PipelineContext<Unit, ApplicationCall>.extractApplicationIdHeader(): String {
    val headers = call.request.headers
    return headers["Application-Id"]?.trim() ?: ""
}

//fun WebSocketServerSession.extractLocalizationHeaderFromWebSocket(): String {
//    val headers = call.request.headers
//    return headers["Accept-Language"]?.trim() ?: Language.ENGLISH.code
//}

private fun PipelineContext<Unit, ApplicationCall>.extractPermission(): Int {
    val principal = call.principal<JWTPrincipal>()
    return principal?.getClaim(PERMISSION, Int::class) ?: -1
}

fun PipelineContext<Unit, ApplicationCall>.extractAdminId(): Int {
    val principal = call.principal<JWTPrincipal>()
    return principal?.getClaim(USER_ID, String::class)?.toIntOrNull()
        ?: throw InvalidCredentialsException("Cant Get Admin Id")
}


fun Route.authenticateWithRole(role: Int, block: Route.() -> Unit) {
    authenticate("auth-jwt") {
        intercept(ApplicationCallPipeline.Call) {
            val permission = extractPermission()
            if (!hasPermission(permission, role)) {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
        block()
    }
}

fun hasPermission(permission: Int, role: Int): Boolean {
    return (permission and role) == role
}

suspend inline fun <reified T> PipelineContext<Unit, ApplicationCall>.receiveMultipart(
    imageValidator: ImageValidator
): MultipartDto<T> {

    val multipart = call.receiveMultipart()
    var fileBytes: ByteArray? = null
    var data: T? = null
    var fileName: String? = null
    val baseUrl =
        call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port() + "${Constants.ENDPOINT}/image/"


    multipart.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                if (imageValidator.isValid(part.originalFileName)) {
                    fileBytes = part.streamProvider().readBytes()
                    fileName = generateSafeFileName(part.originalFileName as String)

                }
            }

            is PartData.FormItem -> {
                if (part.name == "data") {
                    val json = part.value.trimIndent()
                    data = Json.decodeFromString<T>(json)
                }
            }

            else -> {}
        }
        part.dispose()
    }
    return MultipartDto(data = data!!, image = fileBytes , baseUrl = baseUrl , fileName = fileName!!)
}

fun String?.toListOfIntOrNull(): List<Int>? {
    return takeIf { !it.isNullOrBlank() }?.run {
        val integerStrings = this.replace("[", "").replace("]", "").split(",")
        integerStrings.mapNotNull(String::toIntOrNull)
    }
}

fun String?.toListOfStringOrNull(): List<String>? {
    return takeIf { !it.isNullOrBlank() }?.run {
        val integerStrings = this.replace("[", "").replace("]", "").split(",")
        integerStrings.map(String::trim)
    }
}


fun Parameters.extractString(key: String): String {
    return this[key]?.trim()?.takeIf { it.isNotEmpty() } ?: ""
}

