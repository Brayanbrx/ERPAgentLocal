package com.brayan.erpagentlocal.data

import com.brayan.erpagentlocal.agent.AgentAction
import com.brayan.erpagentlocal.agent.AgentErrorMapper
import com.brayan.erpagentlocal.agent.ToolDefinition
import com.brayan.erpagentlocal.agent.ToolExecutionResult
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.metrics.PerformanceEvent
import com.brayan.erpagentlocal.metrics.PerformanceTracker
import org.json.JSONObject
import java.net.URLEncoder
import java.text.Normalizer

class ToolExecutor(
    private val apiClient: AgentApiClient = AgentApiClient()
) {

    suspend fun executeRaw(action: AgentAction.ToolCall): JSONObject {
        return executeToolCall(action).toJsonObject()
    }

    suspend fun executeToolCall(action: AgentAction.ToolCall): ToolExecutionResult {
        val toolDefinition = ToolRegistry.find(action.tool)
            ?: return ToolExecutionResult.failure(
                toolName = action.tool,
                message = "Tool no registrada: ${action.tool}",
                error = "Tool not found in ToolRegistry"
            )

        return executeByDefinition(
            toolDefinition = toolDefinition,
            arguments = action.arguments
        )
    }

    suspend fun executeByDefinition(
        toolDefinition: ToolDefinition,
        arguments: JSONObject
    ): ToolExecutionResult {
        return try {
            val method = HttpMethod.fromValue(toolDefinition.method)
            val normalizedArguments = normalizeArgumentsForTool(toolDefinition.name, arguments)

            val path = buildPath(
                pathTemplate = toolDefinition.path,
                arguments = normalizedArguments
            )

            val body = when (method) {
                HttpMethod.POST,
                HttpMethod.PATCH -> buildBody(
                    toolDefinition = toolDefinition,
                    arguments = normalizedArguments
                )

                HttpMethod.GET,
                HttpMethod.DELETE -> null
            }

            val response = apiClient.request(
                method = method,
                path = path,
                body = body
            )

            val success = response.optBoolean("success", false)

            val message = response.optString(
                "message",
                if (success) {
                    "Tool executed successfully"
                } else {
                    "Tool execution failed"
                }
            )

            val statusCode = response
                .optInt("statusCode", 0)
                .takeIf { it > 0 }

            if (success) {
                PerformanceTracker.increment(PerformanceEvent.TOOL_SUCCESS_COUNT)
                ToolExecutionResult.success(
                    toolName = toolDefinition.name,
                    message = message,
                    rawResponse = response,
                    statusCode = statusCode
                )
            } else {
                PerformanceTracker.increment(PerformanceEvent.TOOL_ERROR_COUNT)
                val mappedError = AgentErrorMapper.fromHttpResponse(
                    response = response,
                    toolName = toolDefinition.name
                )

                response.put("userMessage", mappedError.userMessage)

                ToolExecutionResult.failure(
                    toolName = toolDefinition.name,
                    message = mappedError.userMessage,
                    rawResponse = response,
                    statusCode = statusCode,
                    error = mappedError.technicalMessage
                )
            }
        } catch (exception: Exception) {
            PerformanceTracker.increment(PerformanceEvent.TOOL_ERROR_COUNT)
            val mappedError = AgentErrorMapper.fromException(exception)

            val errorResponse = JSONObject()
                .put("success", false)
                .put("message", mappedError.userMessage)
                .put("statusCode", 0)
                .put("data", JSONObject())
                .put("error", mappedError.technicalMessage)

            ToolExecutionResult.failure(
                toolName = toolDefinition.name,
                message = mappedError.userMessage,
                rawResponse = errorResponse,
                statusCode = 0,
                error = mappedError.technicalMessage
            )
        }
    }

    private fun buildPath(
        pathTemplate: String,
        arguments: JSONObject
    ): String {
        var path = pathTemplate

        val placeholderRegex = Regex("\\{([^}]+)\\}")
        val matches = placeholderRegex.findAll(pathTemplate).toList()

        matches.forEach { match ->
            val fieldName = match.groupValues[1]

            val rawValue = arguments.opt(fieldName)
                ?: throw IllegalArgumentException("Falta argumento para path/query: $fieldName")

            val encodedValue = encode(rawValue.toString())
            path = path.replace("{$fieldName}", encodedValue)
        }

        return path
    }

    private fun buildBody(
        toolDefinition: ToolDefinition,
        arguments: JSONObject
    ): JSONObject {
        val body = JSONObject()
        val pathArgumentNames = extractPlaceholders(toolDefinition.path)

        val keys = arguments.keys()

        while (keys.hasNext()) {
            val key = keys.next()

            if (!pathArgumentNames.contains(key)) {
                body.put(key, arguments.get(key))
            }
        }

        return body
    }

    private fun extractPlaceholders(pathTemplate: String): Set<String> {
        val placeholderRegex = Regex("\\{([^}]+)\\}")

        return placeholderRegex
            .findAll(pathTemplate)
            .map { match -> match.groupValues[1] }
            .toSet()
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
    }

    private fun normalizeArgumentsForTool(toolName: String, arguments: JSONObject): JSONObject {
        return when (toolName) {
            "createProduct", "updateProduct" -> {
                val copy = JSONObject(arguments.toString())
                copy.optString("name").takeIf { it.isNotBlank() }
                    ?.let { copy.put("name", normalizeNameText(it)) }
                copy
            }

            "createCustomer" -> normalizeCreateCustomerArguments(arguments)

            "searchProduct", "searchCustomer" -> {
                val copy = JSONObject(arguments.toString())
                copy.optString("name").takeIf { it.isNotBlank() }
                    ?.let { copy.put("name", normalizeNameText(stripLeadingCustomerNoise(it))) }
                copy
            }

            else -> arguments
        }
    }

    private fun normalizeCreateCustomerArguments(arguments: JSONObject): JSONObject {
        val copy = JSONObject(arguments.toString())

        val aliasName = copy.optString("name", "").trim()
        var firstName = copy.optString("firstName", "").trim()
        var lastName = copy.optString("lastName", "").trim()

        val firstNameLooksLikeArticle = isLeadingArticle(firstName)
        val shouldSplitFullName =
            aliasName.isNotBlank() ||
                    lastName.isBlank() ||
                    firstNameLooksLikeArticle

        if (shouldSplitFullName) {
            val fullNameSource = when {
                aliasName.isNotBlank() -> aliasName
                firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
                else -> firstName.ifBlank { lastName }
            }

            val cleanedFullName = normalizeNameText(stripLeadingCustomerNoise(fullNameSource))
            val parts = cleanedFullName
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }

            if (parts.size >= 2) {
                copy.put("firstName", parts.first())
                copy.put("lastName", parts.drop(1).joinToString(" "))
                copy.remove("name")
                return copy
            }

            if (parts.size == 1) {
                copy.put("firstName", parts.first())
                copy.put("lastName", "")
                copy.remove("name")
                return copy
            }
        }

        firstName = normalizeNameText(stripLeadingCustomerNoise(firstName))
        lastName = normalizeNameText(stripLeadingCustomerNoise(lastName))

        copy.put("firstName", firstName)
        copy.put("lastName", lastName)
        copy.remove("name")

        return copy
    }

    private fun normalizeNameText(value: String): String {
        val noAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}"), "")
        return noAccents.trim().lowercase().replace(Regex("\\s+"), " ")
    }

    private fun stripLeadingCustomerNoise(value: String): String {
        return value
            .trim()
            .replace(Regex("(?i)^(cliente|usuario|customer)\\s+"), "")
            .replace(Regex("(?i)^(llamado|llamada|nombre)\\s+"), "")
            .replace(Regex("(?i)^(el|la|los|las|al|a)\\s+"), "")
            .replace(Regex("[.,;:]$"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun isLeadingArticle(value: String): Boolean {
        val normalized = normalizeNameText(value)
        return normalized in setOf("el", "la", "los", "las", "al", "a") ||
                normalized.startsWith("el ") ||
                normalized.startsWith("la ") ||
                normalized.startsWith("los ") ||
                normalized.startsWith("las ") ||
                normalized.startsWith("al ") ||
                normalized.startsWith("a ")
    }
}