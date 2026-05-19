package com.brayan.erpagentlocal.ai

data class ModelStatus(
    val initialized: Boolean,
    val modelPath: String?,
    val modelName: String?,
    val backend: String,
    val lastError: String? = null
) {

    fun toDebugText(): String {
        return buildString {
            appendLine("MODEL STATUS")
            appendLine()
            appendLine("Initialized: $initialized")
            appendLine("Backend: $backend")
            appendLine()

            appendLine("Model name:")
            appendLine(modelName ?: "none")
            appendLine()

            appendLine("Model path:")
            appendLine(modelPath ?: "none")
            appendLine()

            if (!lastError.isNullOrBlank()) {
                appendLine("Last error:")
                appendLine(lastError)
            }
        }
    }

    fun toUserText(): String {
        return if (initialized) {
            buildString {
                appendLine("Modelo cargado correctamente.")
                appendLine()
                appendLine("Archivo:")
                appendLine(modelName ?: "Modelo desconocido")
            }
        } else {
            buildString {
                appendLine("Modelo no inicializado.")

                if (!lastError.isNullOrBlank()) {
                    appendLine()
                    appendLine("Último error:")
                    appendLine(lastError)
                }
            }
        }
    }
}