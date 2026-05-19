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
    fun hasRequiredArguments(argumentNames: Set<String>): Boolean {
        return requiredArguments.all { requiredArgument ->
            argumentNames.contains(requiredArgument)
        }
    }

    fun allArgumentNames(): List<String> {
        return requiredArguments + optionalArguments
    }

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"name\": \"$name\",")
            appendLine("  \"operationId\": \"$operationId\",")
            appendLine("  \"method\": \"$method\",")
            appendLine("  \"path\": \"$path\",")
            appendLine("  \"description\": \"$description\",")
            appendLine("  \"requiredArguments\": [${requiredArguments.joinToString(", ") { "\"$it\"" }}],")
            appendLine("  \"optionalArguments\": [${optionalArguments.joinToString(", ") { "\"$it\"" }}]")
            appendLine("}")
        }
    }

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