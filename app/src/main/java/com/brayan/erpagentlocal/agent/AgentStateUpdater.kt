package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

class AgentStateUpdater {

    fun updateFromToolResponse(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject,
        currentState: AgentState
    ): AgentState {
        if (!response.optBoolean("success", false)) {
            return currentState
        }

        val data = response.opt("data") ?: return currentState

        return when (toolName) {
            "createCustomer" -> updateFromCustomerObject(
                customer = data as? JSONObject,
                currentState = currentState,
                fallbackName = buildCustomerNameFromArguments(arguments)
            )

            "searchCustomer" -> updateFromCustomerSearch(
                data = data,
                currentState = currentState
            )

            "createProduct" -> updateFromProductObject(
                product = data as? JSONObject,
                currentState = currentState,
                fallbackName = arguments.optString("name", null)
            )

            "searchProduct" -> updateFromProductSearch(
                data = data,
                currentState = currentState
            )

            "updateProduct" -> updateFromProductObject(
                product = data as? JSONObject,
                currentState = currentState,
                fallbackName = arguments.optString("name", null)
            )

            "getInventory" -> updateFromInventoryObject(
                inventory = data as? JSONObject,
                currentState = currentState
            )

            "createPurchase" -> updateFromPurchaseResponse(
                data = data as? JSONObject,
                currentState = currentState
            )

            "createSale" -> updateFromSaleResponse(
                data = data as? JSONObject,
                currentState = currentState
            )

            else -> currentState
        }
    }

    fun addExecutedTool(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject,
        currentState: AgentState
    ): AgentState {
        val executedTool = ExecutedTool(
            toolName = toolName,
            argumentsJson = arguments.toString(2),
            success = response.optBoolean("success", false),
            resultSummary = response.optString("message", "Tool executed"),
            resultJson = response.toString(2)
        )

        return currentState
            .addExecutedTool(executedTool)
            .keepLastExecutedTools(10)
    }

    fun updateAndStoreExecution(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject,
        currentState: AgentState
    ): AgentState {
        val updatedState = updateFromToolResponse(
            toolName = toolName,
            arguments = arguments,
            response = response,
            currentState = currentState
        )

        return addExecutedTool(
            toolName = toolName,
            arguments = arguments,
            response = response,
            currentState = updatedState
        )
    }

    private fun updateFromCustomerSearch(
        data: Any,
        currentState: AgentState
    ): AgentState {
        val customer = when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }

        return updateFromCustomerObject(
            customer = customer,
            currentState = currentState,
            fallbackName = null
        )
    }

    private fun updateFromCustomerObject(
        customer: JSONObject?,
        currentState: AgentState,
        fallbackName: String?
    ): AgentState {
        if (customer == null) {
            return currentState
        }

        val customerId = customer.optString("customerId", null)
        val firstName = customer.optString("firstName", "")
        val lastName = customer.optString("lastName", "")

        val customerName = buildString {
            append(firstName)
            if (lastName.isNotBlank()) {
                append(" ")
                append(lastName)
            }
        }.trim().ifBlank {
            fallbackName.orEmpty()
        }

        return currentState.withCustomer(
            customerId = customerId.ifBlank { null },
            customerName = customerName.ifBlank { null }
        )
    }

    private fun updateFromProductSearch(
        data: Any,
        currentState: AgentState
    ): AgentState {
        val product = when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }

        return updateFromProductObject(
            product = product,
            currentState = currentState,
            fallbackName = null
        )
    }

    private fun updateFromProductObject(
        product: JSONObject?,
        currentState: AgentState,
        fallbackName: String?
    ): AgentState {
        if (product == null) {
            return currentState
        }

        val productId = product.optString("productId", null)
        val productName = product.optString("name", fallbackName.orEmpty())

        return currentState.withProduct(
            productId = productId.ifBlank { null },
            productName = productName.ifBlank { null }
        )
    }

    private fun updateFromInventoryObject(
        inventory: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (inventory == null) {
            return currentState
        }

        val productId = inventory.optString("productId", null)
        val stock = if (inventory.has("stock")) {
            inventory.optInt("stock")
        } else {
            null
        }

        return currentState
            .withProduct(
                productId = productId.ifBlank { null },
                productName = null
            )
            .withInventoryStock(stock)
    }

    private fun updateFromPurchaseResponse(
        data: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (data == null) {
            return currentState
        }

        val purchase = data.optJSONObject("purchase")
        val inventory = data.optJSONObject("inventory")

        val purchaseId = purchase?.optString("purchaseId", null)
        val productId = purchase?.optString("productId", null)
        val productName = purchase?.optString("productName", null)

        var newState = currentState
            .withPurchase(purchaseId?.ifBlank { null })
            .withProduct(
                productId = productId?.ifBlank { null },
                productName = productName?.ifBlank { null }
            )

        if (inventory != null) {
            newState = updateFromInventoryObject(
                inventory = inventory,
                currentState = newState
            )
        }

        return newState
    }

    private fun updateFromSaleResponse(
        data: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (data == null) {
            return currentState
        }

        val sale = data.optJSONObject("sale")
        val inventoryArray = data.optJSONArray("inventory")

        val saleId = sale?.optString("saleId", null)
        val customerId = sale?.optString("customerId", null)
        val customerName = sale?.optString("customerName", null)

        var newState = currentState
            .withSale(saleId?.ifBlank { null })
            .withCustomer(
                customerId = customerId?.ifBlank { null },
                customerName = customerName?.ifBlank { null }
            )

        val firstItem = sale
            ?.optJSONArray("items")
            ?.optJSONObject(0)

        if (firstItem != null) {
            newState = newState.withProduct(
                productId = firstItem.optString("productId", null)?.ifBlank { null },
                productName = firstItem.optString("productName", null)?.ifBlank { null }
            )
        }

        val firstInventory = inventoryArray?.optJSONObject(0)

        if (firstInventory != null) {
            newState = updateFromInventoryObject(
                inventory = firstInventory,
                currentState = newState
            )
        }

        return newState
    }

    private fun buildCustomerNameFromArguments(arguments: JSONObject): String? {
        val firstName = arguments.optString("firstName", "")
        val lastName = arguments.optString("lastName", "")

        return "$firstName $lastName".trim().ifBlank {
            null
        }
    }
}