package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

class PlaceholderResolver {

    fun resolveArguments(
        arguments: JSONObject,
        state: AgentState
    ): JSONObject {
        return resolveValue(arguments, state, mutableListOf()) as JSONObject
    }

    private fun resolveValue(value: Any?, state: AgentState, unresolved: MutableList<String>): Any {
        return when (value) {
            null -> JSONObject.NULL
            JSONObject.NULL -> JSONObject.NULL

            is String -> resolveString(value, state, unresolved)

            is JSONObject -> {
                val result = JSONObject()
                value.keys().forEach { key ->
                    result.put(key, resolveValue(value.opt(key), state, unresolved))
                }
                result
            }

            is JSONArray -> {
                val result = JSONArray()
                for (i in 0 until value.length()) {
                    result.put(resolveValue(value.opt(i), state, unresolved))
                }
                result
            }

            else -> value
        }
    }

    private fun resolveString(value: String, state: AgentState, unresolved: MutableList<String>): Any {
        return when (value) {
            LAST_CUSTOMER_ID -> state.lastCustomerId ?: value.also { unresolved.add(LAST_CUSTOMER_ID) }
            LAST_PRODUCT_ID -> state.lastProductId ?: value.also { unresolved.add(LAST_PRODUCT_ID) }
            LAST_PRODUCT_PURCHASE_PRICE -> state.lastProductPurchasePrice ?: value.also { unresolved.add(LAST_PRODUCT_PURCHASE_PRICE) }
            LAST_PRODUCT_SALE_PRICE -> state.lastProductSalePrice ?: value.also { unresolved.add(LAST_PRODUCT_SALE_PRICE) }
            LAST_SALE_ID -> state.lastSaleId ?: value.also { unresolved.add(LAST_SALE_ID) }
            LAST_PURCHASE_ID -> state.lastPurchaseId ?: value.also { unresolved.add(LAST_PURCHASE_ID) }
            LAST_INVENTORY_STOCK -> state.lastInventoryStock ?: value.also { unresolved.add(LAST_INVENTORY_STOCK) }
            else -> value
        }
    }

    companion object {
        const val LAST_CUSTOMER_ID = "\$lastCustomerId"
        const val LAST_PRODUCT_ID = "\$lastProductId"
        const val LAST_PRODUCT_PURCHASE_PRICE = "\$lastProductPurchasePrice"
        const val LAST_PRODUCT_SALE_PRICE = "\$lastProductSalePrice"
        const val LAST_SALE_ID = "\$lastSaleId"
        const val LAST_PURCHASE_ID = "\$lastPurchaseId"
        const val LAST_INVENTORY_STOCK = "\$lastInventoryStock"
    }
}
