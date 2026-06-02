package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

class AgentPlanNormalizer {

    fun normalize(
        actions: List<AgentAction.ToolCall>,
        state: AgentState
    ): List<AgentAction.ToolCall> {
        val result = mutableListOf<AgentAction.ToolCall>()

        var hasCustomerProducer = !state.lastCustomerId.isNullOrBlank()
        var hasProductProducer = !state.lastProductId.isNullOrBlank()
        var lastProductName = state.lastProductName

        actions.forEach { raw ->
            val action = normalizeBasic(raw)
            val args = JSONObject(action.arguments.toString())

            when (action.tool) {
                "createCustomer" -> {
                    result.add(action.copy(arguments = args))
                    hasCustomerProducer = true
                }

                "searchCustomer" -> {
                    result.add(action.copy(arguments = args))
                    hasCustomerProducer = true
                }

                "createProduct" -> {
                    normalizeProductArgs(args)
                    result.add(action.copy(arguments = args))
                    hasProductProducer = true
                    lastProductName = args.optString("name", lastProductName.orEmpty())
                }

                "searchProduct" -> {
                    normalizeSearchArgs(args)
                    result.add(action.copy(arguments = args))
                    hasProductProducer = true
                    lastProductName = args.optString("name", lastProductName.orEmpty())
                }

                "createPurchase" -> {
                    val productName = extractProductName(args)

                    if (productName.isNotBlank() && !hasProductProducer) {
                        result.add(
                            AgentAction.ToolCall(
                                tool = "searchProduct",
                                arguments = JSONObject().put("name", cleanName(productName))
                            )
                        )
                        hasProductProducer = true
                        lastProductName = cleanName(productName)
                    }

                    args.put("productId", PlaceholderResolver.LAST_PRODUCT_ID)

                    if (!args.has("unitCost") || args.optString("unitCost").isBlank()) {
                        args.put("unitCost", PlaceholderResolver.LAST_PRODUCT_PURCHASE_PRICE)
                    }

                    args.remove("name")
                    args.remove("product")
                    args.remove("productName")

                    result.add(action.copy(arguments = args))
                }

                "createSale" -> {
                    val customerName = extractCustomerName(args)
                    val productName = extractSaleProductName(args)

                    if (customerName.isNotBlank() && !hasCustomerProducer) {
                        result.add(
                            AgentAction.ToolCall(
                                tool = "searchCustomer",
                                arguments = JSONObject().put("name", cleanName(customerName))
                            )
                        )
                        hasCustomerProducer = true
                    }

                    if (productName.isNotBlank() && !hasProductProducer) {
                        result.add(
                            AgentAction.ToolCall(
                                tool = "searchProduct",
                                arguments = JSONObject().put("name", cleanName(productName))
                            )
                        )
                        hasProductProducer = true
                        lastProductName = cleanName(productName)
                    }

                    if (hasCustomerProducer || !state.lastCustomerId.isNullOrBlank()) {
                        args.put("customerId", PlaceholderResolver.LAST_CUSTOMER_ID)
                    }

                    val normalizedItems = normalizeSaleItems(args, hasProductProducer, state)
                    args.put("items", normalizedItems)

                    args.remove("name")
                    args.remove("product")
                    args.remove("productName")
                    args.remove("quantity")
                    args.remove("customer")
                    args.remove("customerName")

                    result.add(action.copy(arguments = args))
                }

                else -> result.add(action.copy(arguments = args))
            }
        }

        return result
    }

    private fun normalizeBasic(action: AgentAction.ToolCall): AgentAction.ToolCall {
        val args = JSONObject(action.arguments.toString())

        when (action.tool) {
            "createCustomer" -> normalizeCustomerArgs(args)
            "createProduct" -> normalizeProductArgs(args)
            "searchCustomer", "searchProduct" -> normalizeSearchArgs(args)
        }

        return action.copy(arguments = args)
    }

    private fun normalizeCustomerArgs(args: JSONObject) {
        val aliasName = args.optString("name", "").trim()
        var firstName = args.optString("firstName", "").trim()
        var lastName = args.optString("lastName", "").trim()

        if (aliasName.isNotBlank()) {
            val parts = cleanName(aliasName).split(Regex("\\s+")).filter { it.isNotBlank() }
            if (parts.size >= 2) {
                args.put("firstName", parts.first())
                args.put("lastName", parts.drop(1).joinToString(" "))
            }
        } else {
            firstName = cleanName(firstName)
            lastName = cleanName(lastName)

            if (lastName.isBlank()) {
                val parts = firstName.split(Regex("\\s+")).filter { it.isNotBlank() }
                if (parts.size >= 2) {
                    args.put("firstName", parts.first())
                    args.put("lastName", parts.drop(1).joinToString(" "))
                } else {
                    args.put("firstName", firstName)
                    args.put("lastName", lastName)
                }
            } else {
                args.put("firstName", firstName)
                args.put("lastName", lastName)
            }
        }

        args.remove("name")
    }

    private fun normalizeProductArgs(args: JSONObject) {
        if (args.has("name")) {
            args.put("name", cleanName(args.optString("name")))
        }

        if (!args.has("unit")) {
            args.put("unit", "unit")
        }
    }

    private fun normalizeSearchArgs(args: JSONObject) {
        if (args.has("name")) {
            args.put("name", cleanName(args.optString("name")))
        }
    }

    private fun normalizeSaleItems(
        args: JSONObject,
        hasProductProducer: Boolean,
        state: AgentState
    ): JSONArray {
        val existingItems = args.optJSONArray("items")
        val items = JSONArray()

        if (existingItems == null || existingItems.length() == 0) {
            val quantity = args.optInt("quantity", 0)
            items.put(
                JSONObject()
                    .put(
                        "productId",
                        if (hasProductProducer || !state.lastProductId.isNullOrBlank()) {
                            PlaceholderResolver.LAST_PRODUCT_ID
                        } else {
                            extractProductName(args)
                        }
                    )
                    .put("quantity", quantity)
            )
            return items
        }

        for (i in 0 until existingItems.length()) {
            val item = existingItems.optJSONObject(i) ?: continue
            val copy = JSONObject(item.toString())

            val productId = copy.optString("productId", "")
            val productName = copy.optString("name", "")
                .ifBlank { copy.optString("product", "") }
                .ifBlank { copy.optString("productName", "") }

            if (
                productId.isBlank() ||
                !looksLikeId(productId) ||
                productName.isNotBlank() ||
                hasProductProducer ||
                !state.lastProductId.isNullOrBlank()
            ) {
                copy.put("productId", PlaceholderResolver.LAST_PRODUCT_ID)
            }

            copy.remove("name")
            copy.remove("product")
            copy.remove("productName")

            items.put(copy)
        }

        return items
    }

    private fun extractProductName(args: JSONObject): String {
        return args.optString("productName", "")
            .ifBlank { args.optString("product", "") }
            .ifBlank { args.optString("name", "") }
            .ifBlank { args.optString("productId", "") }
            .trim()
    }

    private fun extractSaleProductName(args: JSONObject): String {
        val direct = extractProductName(args)
        if (direct.isNotBlank() && !looksLikeId(direct)) return direct

        val items = args.optJSONArray("items") ?: return ""

        for (i in 0 until items.length()) {
            val item = items.optJSONObject(i) ?: continue

            val name = item.optString("name", "")
                .ifBlank { item.optString("product", "") }
                .ifBlank { item.optString("productName", "") }
                .ifBlank { item.optString("productId", "") }

            if (name.isNotBlank() && !looksLikeId(name)) {
                return name
            }
        }

        return ""
    }

    private fun extractCustomerName(args: JSONObject): String {
        return args.optString("customerName", "")
            .ifBlank { args.optString("customer", "") }
            .ifBlank { args.optString("name", "") }
            .trim()
    }

    private fun looksLikeId(value: String): Boolean {
        val clean = value.trim()
        return clean.contains("-") ||
                clean.startsWith("PROD", ignoreCase = true) ||
                clean.startsWith("CUS", ignoreCase = true) ||
                clean.startsWith("PUR", ignoreCase = true) ||
                clean.startsWith("SALE", ignoreCase = true)
    }

    private fun cleanName(value: String): String {
        return value
            .trim()
            .replace(Regex("(?i)^(el|la|los|las|al|a)\\s+"), "")
            .replace(Regex("(?i)^(cliente|producto|usuario)\\s+"), "")
            .replace(Regex("(?i)^(llamado|llamada|nombre)\\s+"), "")
            .replace(Regex("[.,;:]$"), "")
            .replace(Regex("\\s+"), " ")
            .lowercase()
            .trim()
    }
}