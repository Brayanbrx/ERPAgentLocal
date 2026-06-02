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

    fun toJsonObject(): JSONObject {
        return rawResponse ?: JSONObject()
            .put("success", success)
            .put("message", message)
            .put("data", JSONObject())
            .put("error", error ?: "")
            .put("statusCode", statusCode ?: 0)
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