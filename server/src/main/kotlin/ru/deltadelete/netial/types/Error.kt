package ru.deltadelete.netial.types

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.ktor.http.*


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Error::class, name = "error"),
    JsonSubTypes.Type(value = Error.ExceptionError::class, name = "exception"),
    JsonSubTypes.Type(value = Error.UserError::class, name = "user_error")
)
sealed class Error(
    val message: String,
    val statusCode: HttpStatusCode,
) {
    @Suppress("unused")
    class ExceptionError(
        val exception: Throwable,
        message: String,
        statusCode: HttpStatusCode,
    ) : Error(message, statusCode)

    class UserError(message: String, statusCode: HttpStatusCode) : Error(message, statusCode)
}