package com.brayan.erpagentlocal.agent

import com.brayan.erpagentlocal.data.ToolExecutor
import org.json.JSONObject

class AgentExecutor(
    private val toolExecutor: ToolExecutor = ToolExecutor(),
    private val stateUpdater: AgentStateUpdater = AgentStateUpdater(),
    private val placeholderResolver: PlaceholderResolver = PlaceholderResolver()
) {

    suspend fun execute(
        actions: List<AgentAction.ToolCall>,
        initialState: AgentState
    ): AgentExecutionReport {
        var state = initialState
        val results = mutableListOf<AgentExecutedAction>()

        for (action in actions) {
            val resolvedArgs = placeholderResolver.resolveArguments(action.arguments, state)

            val readyCheck = validateReady(action.tool, resolvedArgs)
            if (readyCheck != null) {
                return AgentExecutionReport(
                    completed = false,
                    state = state,
                    actions = results,
                    pausedMessage = readyCheck
                )
            }

            val resolvedAction = action.copy(arguments = resolvedArgs)

            val response = toolExecutor.executeRaw(resolvedAction)

            state = stateUpdater.updateAndStoreExecution(
                toolName = resolvedAction.tool,
                arguments = resolvedAction.arguments,
                response = response,
                currentState = state
            )

            results.add(
                AgentExecutedAction(
                    tool = resolvedAction.tool,
                    arguments = resolvedAction.arguments,
                    response = response,
                    success = response.optBoolean("success", false)
                )
            )

            if (!response.optBoolean("success", false)) {
                return AgentExecutionReport(
                    completed = false,
                    state = state,
                    actions = results,
                    pausedMessage = response.optString("message", "No se pudo completar la acción.")
                )
            }
        }

        return AgentExecutionReport(
            completed = true,
            state = state,
            actions = results,
            pausedMessage = null
        )
    }

    private fun validateReady(tool: String, args: JSONObject): String? {
        fun unresolved(value: String): Boolean {
            return value.startsWith("$")
        }

        return when (tool) {
            "createPurchase" -> {
                val productId = args.optString("productId", "")
                val quantity = args.optInt("quantity", 0)
                val unitCost = args.opt("unitCost")

                when {
                    productId.isBlank() || unresolved(productId) ->
                        "Falta resolver el producto antes de registrar la compra."

                    quantity <= 0 ->
                        "Falta la cantidad de la compra."

                    unitCost == null || unitCost.toString().startsWith("$") ->
                        "Falta el costo unitario de la compra."

                    else -> null
                }
            }

            "createSale" -> {
                val customerId = args.optString("customerId", "")
                val items = args.optJSONArray("items")

                if (customerId.isBlank() || unresolved(customerId)) {
                    return "Falta resolver el cliente antes de registrar la venta."
                }

                if (items == null || items.length() == 0) {
                    return "Faltan productos para registrar la venta."
                }

                for (i in 0 until items.length()) {
                    val item = items.optJSONObject(i) ?: continue
                    val productId = item.optString("productId", "")
                    val quantity = item.optInt("quantity", 0)

                    if (productId.isBlank() || unresolved(productId)) {
                        return "Falta resolver el producto antes de registrar la venta."
                    }

                    if (quantity <= 0) {
                        return "Falta la cantidad del producto en la venta."
                    }
                }

                null
            }

            else -> null
        }
    }
}

data class AgentExecutionReport(
    val completed: Boolean,
    val state: AgentState,
    val actions: List<AgentExecutedAction>,
    val pausedMessage: String?
)

data class AgentExecutedAction(
    val tool: String,
    val arguments: JSONObject,
    val response: JSONObject,
    val success: Boolean
)