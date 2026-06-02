package com.brayan.erpagentlocal.agent

data class AgentError(
    val type: AgentErrorType,
    val technicalMessage: String,
    val userMessage: String,
    val statusCode: Int? = null,
    val canRetry: Boolean = false,
    val shouldAskUser: Boolean = false
)