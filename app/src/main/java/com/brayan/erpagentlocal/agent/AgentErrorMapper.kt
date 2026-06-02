package com.brayan.erpagentlocal.agent

import org.json.JSONObject

object AgentErrorMapper {

    fun fromHttpResponse(
        response: JSONObject,
        toolName: String? = null
    ): AgentError {
        val statusCode = response.optInt("statusCode", 0).takeIf { it > 0 }
        val message = response.optString("message", "Error desconocido.")
        val error = response.optString("error", "")
        val technicalMessage = if (error.isNotBlank()) error else message
        val normalized = "$message $error".lowercase()

        if (normalized.contains("not enough stock") || statusCode == 409) {
            val availableStock = extractAvailableStock(normalized)

            val userMessage = if (availableStock != null) {
                "No se pudo registrar la venta porque solo hay $availableStock unidades disponibles."
            } else {
                "No se pudo registrar la venta porque no hay stock suficiente."
            }

            return AgentError(
                type = AgentErrorType.CONFLICT,
                technicalMessage = technicalMessage,
                userMessage = userMessage,
                statusCode = statusCode,
                canRetry = false,
                shouldAskUser = false
            )
        }

        return when (statusCode) {
            400 -> AgentError(
                type = AgentErrorType.BAD_REQUEST,
                technicalMessage = technicalMessage,
                userMessage = "No pude completar la operación porque hay datos inválidos o incompletos. Revisa la información e inténtalo de nuevo.",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = true
            )

            404 -> AgentError(
                type = AgentErrorType.NOT_FOUND,
                technicalMessage = technicalMessage,
                userMessage = buildNotFoundUserMessage(message, toolName),
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = true
            )

            409 -> AgentError(
                type = AgentErrorType.CONFLICT,
                technicalMessage = technicalMessage,
                userMessage = "No pude completar la operación por una regla de negocio: $message",
                statusCode = statusCode,
                canRetry = false,
                shouldAskUser = false
            )

            500 -> AgentError(
                type = AgentErrorType.SERVER_ERROR,
                technicalMessage = technicalMessage,
                userMessage = "El backend tuvo un error interno. Intenta nuevamente más tarde.",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = false
            )

            0 -> AgentError(
                type = AgentErrorType.NETWORK_ERROR,
                technicalMessage = technicalMessage,
                userMessage = "No pude conectarme con el backend. Verifica tu conexión a internet o que el API Gateway esté activo.",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = false
            )

            else -> AgentError(
                type = AgentErrorType.UNKNOWN,
                technicalMessage = technicalMessage,
                userMessage = "No pude completar la operación: $message",
                statusCode = statusCode,
                canRetry = true,
                shouldAskUser = false
            )
        }
    }

    fun fromException(
        exception: Exception
    ): AgentError {
        val message = exception.message ?: "Error inesperado."

        return AgentError(
            type = AgentErrorType.UNKNOWN,
            technicalMessage = message,
            userMessage = "Ocurrió un error inesperado: $message",
            canRetry = true,
            shouldAskUser = false
        )
    }

    private fun buildNotFoundUserMessage(
        message: String,
        toolName: String?
    ): String {
        val normalized = message.lowercase()

        return when {
            normalized.contains("product") || toolName == "searchProduct" || toolName == "getInventory" ->
                "No encontré ese producto. ¿Quieres crearlo primero?"

            normalized.contains("customer") || toolName == "searchCustomer" ->
                "No encontré ese cliente. ¿Quieres crearlo primero?"

            else ->
                "No encontré el recurso solicitado. Revisa el dato o créalo primero."
        }
    }

    private fun extractAvailableStock(
        message: String
    ): Int? {
        val regex = Regex("available:\\s*(\\d+)", RegexOption.IGNORE_CASE)
        val match = regex.find(message)

        return match
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
    }
}