package com.example.plugins

import com.example.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import mu.KotlinLogging

val logger = KotlinLogging.logger {  }
fun Application.configureStatusPages() {
    install(StatusPages) {
        handleStatusPageExceptions()
//        exception<Throwable> { call, cause ->
//            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
//        }
    }
}

private fun StatusPagesConfig.handleStatusPageExceptions() {
    respondWithErrorCodes<MissingParameterException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<RequestValidationException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<UserAlreadyExistsException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<ResourceNotFoundException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<InsufficientFundsException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<InvalidCredentialsException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<InvalidLocationException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<AlreadyExistsException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<UnknownErrorException>(HttpStatusCode.BadRequest)
    respondWithErrorCodes<ErrorException>(HttpStatusCode.BadRequest)
    respondWithErrorCodes<UploadImageException>(HttpStatusCode.BadRequest)
    respondWithErrorCodes<DeleteImageException>(HttpStatusCode.BadRequest)
    respondWithErrorCodes<NotFoundException>(HttpStatusCode.NotFound)
}

private inline fun <reified T : Throwable> StatusPagesConfig.respondWithErrorCodes(
    statusCode: HttpStatusCode
) {
    exception<T> { call, t ->
        println("Status Code = ${t.message}")
        logger.error { "An unknown error occurred $t" }

        val reasons = t.message?.split(",") ?: emptyList()
        println("Status reasons = $reasons")

        call.respond(
            status = statusCode,
            MyResponse(
                success = false,
                message = t.message ?: "Some thing goes wrong .",
                data = null
            )
        )

    }

}