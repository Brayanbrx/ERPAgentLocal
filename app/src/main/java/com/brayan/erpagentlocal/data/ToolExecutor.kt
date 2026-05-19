package com.brayan.erpagentlocal.data

import com.brayan.erpagentlocal.agent.AgentAction
import com.brayan.erpagentlocal.agent.AgentErrorMapper
import com.brayan.erpagentlocal.agent.ToolDefinition
import com.brayan.erpagentlocal.agent.ToolExecutionResult
import com.brayan.erpagentlocal.agent.ToolRegistry
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToolExecutor(
    private val apiClient: AgentApiClient = AgentApiClient()
) {

    suspend fun execute(action: AgentAction.ToolCall): String {
        val response = executeRaw(action)
        return formatJson("TOOL EJECUTADA: ${action.tool}", response)
    }

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

            val path = buildPath(
                pathTemplate = toolDefinition.path,
                arguments = arguments
            )

            val body = when (method) {
                HttpMethod.POST,
                HttpMethod.PATCH -> buildBody(
                    toolDefinition = toolDefinition,
                    arguments = arguments
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
                ToolExecutionResult.success(
                    toolName = toolDefinition.name,
                    message = message,
                    rawResponse = response,
                    statusCode = statusCode
                )
            } else {
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

    suspend fun checkHealth(): String {
        val response = apiClient.get(ApiConfig.HEALTH)
        return formatJson("HEALTH", response)
    }

    suspend fun runFullErpDemo(): String {
        val code = generateCode()

        val customerResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createCustomer",
                arguments = JSONObject()
                    .put("firstName", "Juan")
                    .put("lastName", "Perez $code")
                    .put("phone", "70000000")
                    .put("email", "juan$code@email.com")
            )
        )

        val customerId = customerResponse
            .optJSONObject("data")
            ?.optString("customerId")
            .orEmpty()

        if (customerId.isBlank()) {
            return buildString {
                appendLine("DEMO ERP DETENIDA")
                appendLine()
                appendLine("No se pudo crear el cliente.")
                appendLine(customerResponse.toString(2))
            }
        }

        val productResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createProduct",
                arguments = JSONObject()
                    .put("name", "Azucar Demo $code")
                    .put("description", "Bolsa de azucar 1kg")
                    .put("unit", "unit")
                    .put("salePrice", 20)
                    .put("purchasePrice", 15)
            )
        )

        val productId = productResponse
            .optJSONObject("data")
            ?.optString("productId")
            .orEmpty()

        if (productId.isBlank()) {
            return buildString {
                appendLine("DEMO ERP DETENIDA")
                appendLine()
                appendLine("Cliente creado: $customerId")
                appendLine("No se pudo crear el producto.")
                appendLine(productResponse.toString(2))
            }
        }

        val purchaseResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createPurchase",
                arguments = JSONObject()
                    .put("productId", productId)
                    .put("quantity", 50)
                    .put("unitCost", 15)
            )
        )

        val inventoryAfterPurchase = executeRaw(
            AgentAction.ToolCall(
                tool = "getInventory",
                arguments = JSONObject()
                    .put("productId", productId)
            )
        )

        val saleItems = JSONArray()
            .put(
                JSONObject()
                    .put("productId", productId)
                    .put("quantity", 2)
            )

        val saleResponse = executeRaw(
            AgentAction.ToolCall(
                tool = "createSale",
                arguments = JSONObject()
                    .put("customerId", customerId)
                    .put("items", saleItems)
            )
        )

        val inventoryAfterSale = executeRaw(
            AgentAction.ToolCall(
                tool = "getInventory",
                arguments = JSONObject()
                    .put("productId", productId)
            )
        )

        return buildString {
            appendLine("DEMO ERP COMPLETADA")
            appendLine()
            appendLine("1. Cliente creado:")
            appendLine("customerId: $customerId")
            appendLine()
            appendLine("2. Producto creado:")
            appendLine("productId: $productId")
            appendLine()
            appendLine("3. Compra registrada:")
            appendLine(purchaseResponse.toString(2))
            appendLine()
            appendLine("4. Inventario después de la compra:")
            appendLine(inventoryAfterPurchase.toString(2))
            appendLine()
            appendLine("5. Venta registrada:")
            appendLine(saleResponse.toString(2))
            appendLine()
            appendLine("6. Inventario después de la venta:")
            appendLine(inventoryAfterSale.toString(2))
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

    private fun generateCode(): String {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
    }

    private fun formatJson(title: String, json: JSONObject): String {
        return buildString {
            appendLine(title)
            appendLine()
            appendLine(json.toString(2))
        }
    }
}