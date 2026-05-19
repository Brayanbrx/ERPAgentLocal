package com.brayan.erpagentlocal.agent

data class AgentError(
    val type: AgentErrorType,
    val technicalMessage: String,
    val userMessage: String,
    val statusCode: Int? = null,
    val canRetry: Boolean = false,
    val shouldAskUser: Boolean = false
) {

    fun toDebugText(): String {
        return buildString {
            appendLine("AGENT ERROR")
            appendLine()
            appendLine("Type: $type")
            appendLine("Status code: ${statusCode ?: "none"}")
            appendLine("Can retry: $canRetry")
            appendLine("Should ask user: $shouldAskUser")
            appendLine()
            appendLine("User message:")
            appendLine(userMessage)
            appendLine()
            appendLine("Technical message:")
            appendLine(technicalMessage)
        }
    }
}