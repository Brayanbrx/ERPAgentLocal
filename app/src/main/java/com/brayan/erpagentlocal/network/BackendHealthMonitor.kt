package com.brayan.erpagentlocal.network

import android.content.Context
import com.brayan.erpagentlocal.data.AgentApiClient
import com.brayan.erpagentlocal.data.ApiConfig
import com.brayan.erpagentlocal.metrics.PerformanceEvent
import com.brayan.erpagentlocal.metrics.PerformanceTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackendHealthMonitor(
    context: Context,
    private val apiClient: AgentApiClient = AgentApiClient()
) {

    private val networkStatusMonitor = NetworkStatusMonitor(context)

    suspend fun checkHealth(): BackendHealthSnapshot {
        return withContext(Dispatchers.IO) {
            if (!networkStatusMonitor.hasInternet()) {
                return@withContext BackendHealthSnapshot(
                    internetAvailable = false,
                    backendConnected = false,
                    message = "El celular no tiene conexión a internet.",
                    baseUrl = ApiConfig.BASE_URL
                )
            }

            val startMs = System.currentTimeMillis()

            try {
                val response = apiClient.get(ApiConfig.HEALTH)
                val latencyMs = System.currentTimeMillis() - startMs
                PerformanceTracker.record(PerformanceEvent.BACKEND_HEALTH_MS, latencyMs)

                val connected = response.optBoolean("success", false)
                val statusCode = response.optInt("statusCode", 0).takeIf { it > 0 }
                val message = response.optString(
                    "message",
                    if (connected) "Backend conectado." else "Backend sin respuesta."
                )
                val technicalError = response.optString("error", "").ifBlank { null }
                val baseUrl = response.optString("baseUrl", ApiConfig.BASE_URL)

                BackendHealthSnapshot(
                    internetAvailable = true,
                    backendConnected = connected,
                    latencyMs = latencyMs,
                    message = message,
                    statusCode = statusCode,
                    baseUrl = baseUrl,
                    technicalError = technicalError
                )
            } catch (exception: Exception) {
                val latencyMs = System.currentTimeMillis() - startMs
                PerformanceTracker.record(PerformanceEvent.BACKEND_HEALTH_MS, latencyMs)

                BackendHealthSnapshot(
                    internetAvailable = true,
                    backendConnected = false,
                    latencyMs = latencyMs,
                    message = "No se pudo verificar el backend.",
                    statusCode = null,
                    baseUrl = ApiConfig.BASE_URL,
                    technicalError = exception.message ?: exception::class.java.simpleName
                )
            }
        }
    }
}