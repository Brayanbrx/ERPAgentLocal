package com.brayan.erpagentlocal.data

import com.brayan.erpagentlocal.metrics.PerformanceEvent
import com.brayan.erpagentlocal.metrics.PerformanceTracker
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

            executeRequest(request = request, method = "GET", path = path)
        }
    }

    suspend fun post(path: String, body: JSONObject): JSONObject {
        return withContext(Dispatchers.IO) {
            val requestBody = body.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .post(requestBody)
                .build()

            executeRequest(request = request, method = "POST", path = path)
        }
    }

    suspend fun patch(path: String, body: JSONObject): JSONObject {
        return withContext(Dispatchers.IO) {
            val requestBody = body.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .patch(requestBody)
                .build()

            executeRequest(request = request, method = "PATCH", path = path)
        }
    }

    suspend fun delete(path: String): JSONObject {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(ApiConfig.buildUrl(path))
                .delete()
                .build()

            executeRequest(request = request, method = "DELETE", path = path)
        }
    }

    private fun executeRequest(
        request: Request,
        method: String,
        path: String
    ): JSONObject {
        val startedAtMs = System.currentTimeMillis()
        val url = request.url.toString()

        return try {
            client.newCall(request).execute().use { response ->
                val latencyMs = System.currentTimeMillis() - startedAtMs
                PerformanceTracker.record(PerformanceEvent.LAMBDA_LATENCY_MS, latencyMs)

                val responseBody = response.body?.string().orEmpty()
                val parsedBody = parseResponseBody(responseBody)
                val statusCode = response.code

                if (!response.isSuccessful) {
                    return normalizeErrorResponse(
                        statusCode = statusCode,
                        parsedBody = parsedBody,
                        rawBody = responseBody,
                        method = method,
                        path = path,
                        url = url,
                        latencyMs = latencyMs
                    )
                }

                normalizeSuccessResponse(
                    statusCode = statusCode,
                    parsedBody = parsedBody,
                    method = method,
                    path = path,
                    url = url,
                    latencyMs = latencyMs
                )
            }
        } catch (exception: UnknownHostException) {
            recordFailedLatency(startedAtMs)
            networkError(
                message = "No se pudo resolver el host del backend. Verifica internet o la URL del API Gateway.",
                exception = exception,
                method = method,
                path = path,
                url = url
            )
        } catch (exception: SocketTimeoutException) {
            recordFailedLatency(startedAtMs)
            networkError(
                message = "La conexión con el backend tardó demasiado. El API puede estar apagado o lento.",
                exception = exception,
                method = method,
                path = path,
                url = url
            )
        } catch (exception: IOException) {
            recordFailedLatency(startedAtMs)
            networkError(
                message = "No se pudo conectar con el backend. Revisa la URL, internet o si la API está desplegada.",
                exception = exception,
                method = method,
                path = path,
                url = url
            )
        } catch (exception: Exception) {
            recordFailedLatency(startedAtMs)
            JSONObject()
                .put("success", false)
                .put("message", exception.message ?: "Error inesperado en la llamada HTTP.")
                .put("statusCode", 0)
                .put("data", JSONObject())
                .put("error", exception.message ?: "Unexpected error")
                .put("exceptionType", exception::class.java.simpleName)
                .put("method", method)
                .put("path", path)
                .put("url", url)
                .put("baseUrl", ApiConfig.BASE_URL)
        }
    }

    private fun recordFailedLatency(startedAtMs: Long) {
        PerformanceTracker.record(
            PerformanceEvent.LAMBDA_LATENCY_MS,
            System.currentTimeMillis() - startedAtMs
        )
    }

    private fun normalizeSuccessResponse(
        statusCode: Int,
        parsedBody: JSONObject,
        method: String,
        path: String,
        url: String,
        latencyMs: Long
    ): JSONObject {
        val result = if (parsedBody.length() == 0) {
            JSONObject()
                .put("success", true)
                .put("message", "Empty response")
                .put("data", JSONObject())
        } else {
            JSONObject(parsedBody.toString())
        }

        if (!result.has("success")) result.put("success", true)
        if (!result.has("message")) result.put("message", "Operation completed")
        if (!result.has("data")) result.put("data", JSONObject())

        return result
            .put("statusCode", statusCode)
            .put("method", method)
            .put("path", path)
            .put("url", url)
            .put("baseUrl", ApiConfig.BASE_URL)
            .put("latencyMs", latencyMs)
    }

    private fun normalizeErrorResponse(
        statusCode: Int,
        parsedBody: JSONObject,
        rawBody: String,
        method: String,
        path: String,
        url: String,
        latencyMs: Long
    ): JSONObject {
        val message = extractErrorMessage(parsedBody, statusCode)

        return JSONObject()
            .put("success", false)
            .put("message", message)
            .put("statusCode", statusCode)
            .put("data", parsedBody.opt("data") ?: JSONObject())
            .put("error", rawBody.ifBlank { message })
            .put("method", method)
            .put("path", path)
            .put("url", url)
            .put("baseUrl", ApiConfig.BASE_URL)
            .put("latencyMs", latencyMs)
    }

    private fun networkError(
        message: String,
        exception: Exception,
        method: String,
        path: String,
        url: String
    ): JSONObject {
        return JSONObject()
            .put("success", false)
            .put("message", message)
            .put("statusCode", 0)
            .put("data", JSONObject())
            .put("error", exception.message ?: message)
            .put("exceptionType", exception::class.java.simpleName)
            .put("method", method)
            .put("path", path)
            .put("url", url)
            .put("baseUrl", ApiConfig.BASE_URL)
    }

    private fun parseResponseBody(responseBody: String): JSONObject {
        if (responseBody.isBlank()) return JSONObject()

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
        if (message.isNotBlank()) return message

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