package com.brayan.erpagentlocal.agent

import org.json.JSONArray
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

    fun fromInvalidJson(
        rawOutput: String,
        error: String
    ): AgentError {
        return AgentError(
            type = AgentErrorType.INVALID_JSON,
            technicalMessage = "$error\n\nRaw output:\n$rawOutput",
            userMessage = "No pude interpretar la acción del modelo. Intenta escribir la instrucción de forma más específica.",
            statusCode = null,
            canRetry = true,
            shouldAskUser = true
        )
    }

    fun fromModelNotReady(): AgentError {
        return AgentError(
            type = AgentErrorType.MODEL_NOT_READY,
            technicalMessage = "Local model is not initialized.",
            userMessage = "El modelo local todavía no está inicializado. Selecciona el archivo .litertlm y presiona “Inicializar”.",
            canRetry = true,
            shouldAskUser = false
        )
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

    fun isEmptySearchResult(
        toolName: String,
        response: JSONObject
    ): Boolean {
        if (toolName != "searchProduct" && toolName != "searchCustomer") {
            return false
        }

        if (!response.optBoolean("success", false)) {
            return false
        }

        val data = response.opt("data")

        return when (data) {
            is JSONArray -> data.length() == 0
            null -> true
            JSONObject.NULL -> true
            else -> false
        }
    }

    fun emptySearchMessage(
        toolName: String,
        arguments: JSONObject
    ): String {
        val name = arguments.optString("name", "solicitado")

        return when (toolName) {
            "searchProduct" -> "No encontré el producto \"$name\". ¿Deseas crearlo primero?"
            "searchCustomer" -> "No encontré el cliente \"$name\". ¿Deseas crearlo primero?"
            else -> "No encontré el recurso solicitado."
        }
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