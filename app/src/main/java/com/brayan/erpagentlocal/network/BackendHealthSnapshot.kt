package com.brayan.erpagentlocal.network

data class BackendHealthSnapshot(
    val internetAvailable: Boolean,
    val backendConnected: Boolean,
    val latencyMs: Long? = null,
    val message: String,
    val statusCode: Int? = null,
    val baseUrl: String = "",
    val technicalError: String? = null,
    val checkedAtMs: Long = System.currentTimeMillis()
) {
    fun toStatusText(): String {
        return when {
            !internetAvailable -> "Sin internet"
            backendConnected && latencyMs != null -> "Backend online (${latencyMs} ms)"
            backendConnected -> "Backend online"
            statusCode != null && statusCode > 0 -> "Backend error HTTP $statusCode"
            else -> "Backend sin conexión"
        }
    }
}