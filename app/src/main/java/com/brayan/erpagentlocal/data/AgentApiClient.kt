package com.brayan.erpagentlocal.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class AgentApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(ApiConfig.DEFAULT_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun request(
        method: HttpMethod,
        path: String,
        body: JSONObject? = null
    ): JSONObject {
        return when (method) {
            HttpMethod.GET -> get(path)
            HttpMethod.POST -> post(path, body ?: JSONObject())
            HttpMethod.PATCH -> patch(path, body ?: JSONObject())
            HttpMethod.DELETE -> delete(path)
        }
    }

    suspend fun get(path: String): JSONObject {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .get()
                .build()

            executeRequest(request)
        }
    }

    suspend fun post(path: String, body: JSONObject): JSONObject {
        return withContext(Dispatchers.IO) {
            val requestBody = body.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .post(requestBody)
                .build()

            executeRequest(request)
        }
    }

    suspend fun patch(path: String, body: JSONObject): JSONObject {
        return withContext(Dispatchers.IO) {
            val requestBody = body.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .patch(requestBody)
                .build()

            executeRequest(request)
        }
    }

    suspend fun delete(path: String): JSONObject {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .delete()
                .build()

            executeRequest(request)
        }
    }

    private fun executeRequest(request: Request): JSONObject {
        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                val parsedBody = parseResponseBody(responseBody)
                val statusCode = response.code

                if (!response.isSuccessful) {
                    return normalizeErrorResponse(
                        statusCode = statusCode,
                        parsedBody = parsedBody,
                        rawBody = responseBody
                    )
                }

                normalizeSuccessResponse(
                    statusCode = statusCode,
                    parsedBody = parsedBody
                )
            }
        } catch (exception: UnknownHostException) {
            networkError("No se pudo resolver el host del backend. Verifica tu internet o la URL del API Gateway.", exception)
        } catch (exception: SocketTimeoutException) {
            networkError("La conexión con el backend tardó demasiado.", exception)
        } catch (exception: IOException) {
            networkError("No se pudo conectar con el backend.", exception)
        } catch (exception: Exception) {
            JSONObject()
                .put("success", false)
                .put("message", exception.message ?: "Error inesperado en la llamada HTTP.")
                .put("statusCode", 0)
                .put("data", JSONObject())
                .put("error", exception.message ?: "Unexpected error")
        }
    }

    private fun normalizeSuccessResponse(
        statusCode: Int,
        parsedBody: JSONObject
    ): JSONObject {
        if (parsedBody.length() == 0) {
            return JSONObject()
                .put("success", true)
                .put("message", "Empty response")
                .put("statusCode", statusCode)
                .put("data", JSONObject())
        }

        if (!parsedBody.has("success")) {
            parsedBody.put("success", true)
        }

        if (!parsedBody.has("message")) {
            parsedBody.put("message", "Operation completed")
        }

        if (!parsedBody.has("data")) {
            parsedBody.put("data", JSONObject())
        }

        parsedBody.put("statusCode", statusCode)

        return parsedBody
    }

    private fun normalizeErrorResponse(
        statusCode: Int,
        parsedBody: JSONObject,
        rawBody: String
    ): JSONObject {
        val message = extractErrorMessage(parsedBody, statusCode)

        return JSONObject()
            .put("success", false)
            .put("message", message)
            .put("statusCode", statusCode)
            .put("data", parsedBody.opt("data") ?: JSONObject())
            .put("error", rawBody.ifBlank { message })
    }

    private fun networkError(
        message: String,
        exception: Exception
    ): JSONObject {
        return JSONObject()
            .put("success", false)
            .put("message", message)
            .put("statusCode", 0)
            .put("data", JSONObject())
            .put("error", exception.message ?: message)
    }

    private fun parseResponseBody(responseBody: String): JSONObject {
        if (responseBody.isBlank()) {
            return JSONObject()
        }

        return try {
            JSONObject(responseBody)
        } catch (_: Exception) {
            JSONObject()
                .put("success", false)
                .put("message", "El backend devolvió una respuesta no JSON.")
                .put("rawBody", responseBody)
        }
    }

    private fun extractErrorMessage(
        body: JSONObject,
        statusCode: Int
    ): String {
        val message = body.optString("message", "")

        if (message.isNotBlank()) {
            return message
        }

        return when (statusCode) {
            400 -> "Solicitud inválida."
            401 -> "No autorizado."
            403 -> "Acceso denegado."
            404 -> "Recurso no encontrado."
            409 -> "Conflicto de negocio."
            500 -> "Error interno del servidor."
            else -> "Error HTTP $statusCode."
        }
    }
}