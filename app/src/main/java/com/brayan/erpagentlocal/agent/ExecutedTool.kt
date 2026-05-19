package com.brayan.erpagentlocal.agent

data class ExecutedTool(
    val toolName: String,
    val argumentsJson: String,
    val success: Boolean,
    val resultSummary: String,
    val resultJson: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"toolName\": \"$toolName\",")
            appendLine("  \"success\": $success,")
            appendLine("  \"arguments\": ${argumentsJson.ifBlank { "{}" }},")
            appendLine("  \"resultSummary\": \"${escape(resultSummary)}\"")
            if (!resultJson.isNullOrBlank()) {
                appendLine("  ,\"result\": $resultJson")
            }
            appendLine("}")
        }
    }

    fun toDebugText(): String {
        return buildString {
            appendLine("Tool: $toolName")
            appendLine("Success: $success")
            appendLine("Arguments:")
            appendLine(argumentsJson)
            appendLine("Result summary:")
            appendLine(resultSummary)

            if (!resultJson.isNullOrBlank()) {
                appendLine("Result JSON:")
                appendLine(resultJson)
            }
        }
    }

    private fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
}