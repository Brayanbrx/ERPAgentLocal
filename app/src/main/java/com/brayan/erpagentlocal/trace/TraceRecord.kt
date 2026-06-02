package com.brayan.erpagentlocal.trace

data class TraceRecord(
    val originalText: String,
    val transcribedText: String,
    val normalizedText: String,
    val promptSummary: String,
    val understoodIntent: String,
    val requestedTool: String,
    val argumentsText: String,
    val validationResult: String,
    val endpointCalled: String,
    val lambdaResponse: String,
    val updatedState: String,
    val totalMs: Long?,
    val modelName: String,
    val activeBackend: String,
    val toolsExecuted: String,
    val result: String,
    val error: String? = null,
    val createdAtMs: Long = System.currentTimeMillis()
)
