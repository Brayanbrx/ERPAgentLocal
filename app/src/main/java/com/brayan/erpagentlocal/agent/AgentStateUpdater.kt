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
        if (!response.optBoolean("success", false)) return currentState

        when (toolName) {
            "deleteCustomer" -> return currentState.copy(
                lastCustomerId = null,
                lastCustomerName = null
            )

            "deleteProduct" -> return currentState.copy(
                lastProductId = null,
                lastProductName = null,
                lastProductPurchasePrice = null,
                lastProductSalePrice = null
            )
        }

        val data = response.opt("data") ?: return currentState

        return when (toolName) {
            "createCustomer" -> updateFromCustomerObject(
                customer = data as? JSONObject,
                currentState = currentState,
                fallbackName = buildCustomerNameFromArguments(arguments)
            )

            "searchCustomer" -> updateFromCustomerSearch(data, currentState)

            "createProduct" -> updateFromProductObject(
                product = data as? JSONObject,
                currentState = currentState,
                fallbackName = arguments.optString("name", "")
            )

            "searchProduct" -> updateFromProductSearch(data, currentState)

            "updateProduct" -> updateFromProductObject(
                product = data as? JSONObject,
                currentState = currentState,
                fallbackName = arguments.optString("name", "")
            )

            "listProducts" -> currentState

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
            resultJson = extractRelevantIds(response)
        )

        return currentState
            .addExecutedTool(executedTool)
            .keepLastExecutedTools(10)
    }

    private fun updateFromCustomerSearch(data: Any, currentState: AgentState): AgentState {
        val customer = when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }

        return updateFromCustomerObject(customer, currentState, null)
    }

    private fun updateFromCustomerObject(
        customer: JSONObject?,
        currentState: AgentState,
        fallbackName: String?
    ): AgentState {
        if (customer == null) return currentState

        val customerId = customer.optString("customerId", "")
        val firstName = customer.optString("firstName", "")
        val lastName = customer.optString("lastName", "")

        val customerName = "$firstName $lastName"
            .replace(Regex("\\s+"), " ")
            .trim()
            .ifBlank { fallbackName.orEmpty() }

        return currentState.withCustomer(
            customerId = trustedIdOrNull(customerId),
            customerName = customerName.ifBlank { null }
        )
    }

    private fun updateFromProductSearch(data: Any, currentState: AgentState): AgentState {
        val product = when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }

        return updateFromProductObject(product, currentState, null)
    }

    private fun updateFromProductObject(
        product: JSONObject?,
        currentState: AgentState,
        fallbackName: String?
    ): AgentState {
        if (product == null) return currentState

        val productId = product.optString("productId", "")
        val productName = product.optString("name", fallbackName.orEmpty())

        val purchasePrice = if (product.has("purchasePrice")) {
            product.optDouble("purchasePrice")
        } else {
            null
        }

        val salePrice = if (product.has("salePrice")) {
            product.optDouble("salePrice")
        } else {
            null
        }

        return currentState.withProduct(
            productId = trustedIdOrNull(productId),
            productName = productName.ifBlank { null },
            purchasePrice = purchasePrice,
            salePrice = salePrice
        )
    }

    private fun updateFromInventoryObject(
        inventory: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (inventory == null) return currentState

        val productId = inventory.optString("productId", "")
        val stock = if (inventory.has("stock")) inventory.optInt("stock") else null

        return currentState
            .withProduct(
                productId = trustedIdOrNull(productId),
                productName = null
            )
            .withInventoryStock(stock)
    }

    private fun updateFromPurchaseResponse(
        data: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (data == null) return currentState

        val purchase = data.optJSONObject("purchase")
        val inventory = data.optJSONObject("inventory")

        val purchaseId = purchase?.optString("purchaseId", "")
        val productId = purchase?.optString("productId", "")

        var newState = currentState
            .withPurchase(purchaseId?.ifBlank { null })
            .withProduct(
                productId = trustedIdOrNull(productId),
                productName = null
            )

        if (inventory != null) {
            newState = updateFromInventoryObject(inventory, newState)
        }

        return newState
    }

    private fun updateFromSaleResponse(
        data: JSONObject?,
        currentState: AgentState
    ): AgentState {
        if (data == null) return currentState

        val sale = data.optJSONObject("sale")
        val inventoryArray = data.optJSONArray("inventory")

        val saleId = sale?.optString("saleId", "")
        val customerId = sale?.optString("customerId", "")
        val customerName = sale?.optString("customerName", "")

        var newState = currentState
            .withSale(saleId?.ifBlank { null })
            .withCustomer(
                customerId = trustedIdOrNull(customerId),
                customerName = customerName?.ifBlank { null }
            )

        val firstItem = sale
            ?.optJSONArray("items")
            ?.optJSONObject(0)

        if (firstItem != null) {
            newState = newState.withProduct(
                productId = trustedIdOrNull(firstItem.optString("productId", "")),
                productName = firstItem.optString("productName", "").ifBlank { null }
            )
        }

        val firstInventory = inventoryArray?.optJSONObject(0)

        if (firstInventory != null) {
            newState = updateFromInventoryObject(firstInventory, newState)
        }

        return newState
    }

    private fun buildCustomerNameFromArguments(arguments: JSONObject): String? {
        val firstName = arguments.optString("firstName", "")
        val lastName = arguments.optString("lastName", "")
        return "$firstName $lastName".trim().ifBlank { null }
    }

    private fun extractRelevantIds(response: JSONObject): String {
        val ids = mutableListOf<String>()

        fun scanObject(obj: JSONObject?) {
            obj ?: return
            listOf(
                "customerId",
                "productId",
                "purchaseId",
                "saleId",
                "stock",
                "purchasePrice",
                "salePrice"
            ).forEach { key ->
                val value = obj.opt(key)
                if (value != null && value.toString().isNotBlank()) {
                    ids.add("$key=$value")
                }
            }
        }

        val data = response.opt("data")

        when (data) {
            is JSONObject -> {
                scanObject(data)
                scanObject(data.optJSONObject("purchase"))
                scanObject(data.optJSONObject("sale"))
                scanObject(data.optJSONObject("inventory"))
            }

            is JSONArray -> {
                for (i in 0 until data.length()) {
                    scanObject(data.optJSONObject(i))
                }
            }
        }

        return ids.joinToString(", ").ifBlank {
            response.optString("message", "ok")
        }
    }

    private fun trustedIdOrNull(value: String?): String? {
        val cleanValue = value?.trim().orEmpty()
        if (cleanValue.isBlank()) return null

        val lower = cleanValue.lowercase()
        val rejectedFragments = listOf(
            "pega_aqui",
            "placeholder",
            "unknown",
            "desconocido",
            "inventado",
            "\$last"
        )

        if (rejectedFragments.any { lower.contains(it) }) return null

        return cleanValue
    }
}
