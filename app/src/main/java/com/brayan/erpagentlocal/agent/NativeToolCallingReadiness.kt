package com.brayan.erpagentlocal.agent

data class NativeToolCallingReadiness(
    val canTryNativeTools: Boolean,
    val recommendedMode: AgentToolCallingMode,
    val modelName: String,
    val reasons: List<String>,
    val warnings: List<String>
) {

    fun toDebugText(): String {
        return buildString {
            appendLine("NATIVE TOOL CALLING READINESS")
            appendLine()
            appendLine("Model: ${modelName.ifBlank { "unknown" }}")
            appendLine("Can try native tools: $canTryNativeTools")
            appendLine("Recommended mode: ${recommendedMode.title}")
            appendLine()
            appendLine("Reasons:")
            if (reasons.isEmpty()) {
                appendLine("- No reasons available.")
            } else {
                reasons.forEach { reason ->
                    appendLine("- $reason")
                }
            }
            appendLine()
            appendLine("Warnings:")
            if (warnings.isEmpty()) {
                appendLine("- No warnings.")
            } else {
                warnings.forEach { warning ->
                    appendLine("- $warning")
                }
            }
        }
    }
}