package com.brayan.erpagentlocal.ai

data class ModelStatus(
    val initialized: Boolean,
    val modelPath: String?,
    val modelName: String?,
    val modelFileSize: Long = 0L,
    val backendMode: ModelBackendMode = ModelBackendMode.AUTO,
    val activeBackend: String = "none",
    val modelLoadMs: Long? = null,
    val warmupMs: Long? = null,
    val lastGenerationMs: Long? = null,
    val lastError: String? = null
) {

    fun toDebugText(): String {
        return buildString {
            appendLine("MODEL STATUS")
            appendLine()
            appendLine("Initialized: $initialized")
            appendLine("Backend mode: $backendMode")
            appendLine("Active backend: $activeBackend")
            appendLine("Model load ms: ${modelLoadMs ?: "none"}")
            appendLine("Warmup ms: ${warmupMs ?: "none"}")
            appendLine("Last generation ms: ${lastGenerationMs ?: "none"}")
            appendLine()

            appendLine("Model name:")
            appendLine(modelName ?: "none")
            appendLine()

            appendLine("Model path:")
            appendLine(modelPath ?: "none")
            appendLine()

            appendLine("Model file size:")
            appendLine(if (modelFileSize > 0L) "$modelFileSize bytes" else "none")
            appendLine()

            if (!lastError.isNullOrBlank()) {
                appendLine("Last error:")
                appendLine(lastError)
            }
        }
    }
}
