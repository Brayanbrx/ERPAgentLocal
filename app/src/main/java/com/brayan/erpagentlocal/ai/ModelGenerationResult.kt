package com.brayan.erpagentlocal.ai

data class ModelGenerationResult(
    val success: Boolean,
    val text: String,
    val error: String? = null,
    val promptLength: Int = 0,
    val outputLength: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun getOrThrow(): String {
        if (!success) {
            throw IllegalStateException(error ?: "Error generando respuesta del modelo.")
        }

        return text
    }

    fun toDebugText(): String {
        val builder = StringBuilder()

        builder.appendLine("MODEL GENERATION RESULT")
        builder.appendLine()
        builder.appendLine("Success: $success")
        builder.appendLine("Prompt length: $promptLength")
        builder.appendLine("Output length: $outputLength")
        builder.appendLine("Created at: $createdAt")
        builder.appendLine()

        if (!error.isNullOrBlank()) {
            builder.appendLine("Error:")
            builder.appendLine(error)
            builder.appendLine()
        }

        builder.appendLine("Output:")
        builder.appendLine(text)

        return builder.toString()
    }

    companion object {

        fun success(
            text: String,
            promptLength: Int
        ): ModelGenerationResult {
            return ModelGenerationResult(
                success = true,
                text = text,
                promptLength = promptLength,
                outputLength = text.length
            )
        }

        fun failure(
            error: String,
            promptLength: Int = 0
        ): ModelGenerationResult {
            return ModelGenerationResult(
                success = false,
                text = "",
                error = error,
                promptLength = promptLength,
                outputLength = 0
            )
        }
    }
}