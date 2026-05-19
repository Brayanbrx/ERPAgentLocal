package com.brayan.erpagentlocal.agent

import org.json.JSONObject

data class ToolExecutionResult(
    val toolName: String,
    val success: Boolean,
    val message: String,
    val statusCode: Int? = null,
    val rawResponse: JSONObject? = null,
    val error: String? = null,
    val executedAt: Long = System.currentTimeMillis()
) {

    fun toExecutedTool(argumentsJson: String): ExecutedTool {
        return ExecutedTool(
            toolName = toolName,
            argumentsJson = argumentsJson,
            success = success,
            resultSummary = message,
            resultJson = rawResponse?.toString(2),
            createdAt = executedAt
        )
    }

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"toolName\": \"$toolName\",")
            appendLine("  \"success\": $success,")

            if (statusCode != null) {
                appendLine("  \"statusCode\": $statusCode,")
            }

            appendLine("  \"message\": \"${escape(message)}\"")

            if (rawResponse != null) {
                appendLine("  ,\"rawResponse\": ${rawResponse.toString(2)}")
            }

            if (!error.isNullOrBlank()) {
                appendLine("  ,\"error\": \"${escape(error)}\"")
            }

            appendLine("}")
        }
    }

    fun toDebugText(): String {
        return buildString {
            appendLine("Tool execution result")
            appendLine("Tool: $toolName")
            appendLine("Success: $success")

            if (statusCode != null) {
                appendLine("Status code: $statusCode")
            }

            appendLine("Message: $message")

            if (!error.isNullOrBlank()) {
                appendLine("Error: $error")
            }

            if (rawResponse != null) {
                appendLine("Raw response:")
                appendLine(rawResponse.toString(2))
            }
        }
    }

    fun toJsonObject(): JSONObject {
        return rawResponse ?: JSONObject()
            .put("success", success)
            .put("message", message)
            .put("data", JSONObject())
            .put("error", error ?: "")
            .put("statusCode", statusCode ?: 0)
    }

    private fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }

    companion object {

        fun success(
            toolName: String,
            message: String,
            rawResponse: JSONObject,
            statusCode: Int? = null
        ): ToolExecutionResult {
            return ToolExecutionResult(
                toolName = toolName,
                success = true,
                message = message,
                statusCode = statusCode,
                rawResponse = rawResponse
            )
        }

        fun failure(
            toolName: String,
            message: String,
            error: String? = null,
            rawResponse: JSONObject? = null,
            statusCode: Int? = null
        ): ToolExecutionResult {
            return ToolExecutionResult(
                toolName = toolName,
                success = false,
                message = message,
                statusCode = statusCode,
                rawResponse = rawResponse,
                error = error
            )
        }
    }
}