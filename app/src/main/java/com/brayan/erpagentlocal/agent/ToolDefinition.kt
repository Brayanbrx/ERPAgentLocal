package com.brayan.erpagentlocal.agent

data class ToolDefinition(
    val name: String,
    val operationId: String,
    val method: String,
    val path: String,
    val description: String,
    val requiredArguments: List<String>,
    val optionalArguments: List<String> = emptyList()
) {
    fun toDebugText(): String {
        return buildString {
            appendLine("- $name")
            appendLine("  operationId: $operationId")
            appendLine("  method: $method")
            appendLine("  path: $path")
            appendLine("  description: $description")
            appendLine("  required: ${requiredArguments.joinToString(", ").ifBlank { "none" }}")
            appendLine("  optional: ${optionalArguments.joinToString(", ").ifBlank { "none" }}")
        }
    }
}