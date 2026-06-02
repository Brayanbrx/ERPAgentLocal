package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceholderResolverTest {

    private val resolver = PlaceholderResolver()

    @Test
    fun resolvesNestedKnownPlaceholders() {
        val state = AgentState(
            lastCustomerId = "CUST-1",
            lastProductId = "PROD-1",
            lastInventoryStock = 48
        )
        val arguments = JSONObject()
            .put("customerId", PlaceholderResolver.LAST_CUSTOMER_ID)
            .put(
                "items",
                JSONArray().put(
                    JSONObject()
                        .put("productId", PlaceholderResolver.LAST_PRODUCT_ID)
                        .put("quantity", 2)
                )
            )
            .put("stock", PlaceholderResolver.LAST_INVENTORY_STOCK)

        val args = resolver.resolveArguments(arguments, state)

        assertEquals("CUST-1", args.getString("customerId"))
        assertEquals("PROD-1", args.getJSONArray("items").getJSONObject(0).getString("productId"))
        assertEquals(48, args.getInt("stock"))
    }

    @Test
    fun keepsUnresolvedPlaceholderLiteral() {
        val arguments = JSONObject()
            .put("productId", PlaceholderResolver.LAST_PRODUCT_ID)
            .put("quantity", 50)

        val args = resolver.resolveArguments(arguments, AgentState.empty())

        assertEquals(PlaceholderResolver.LAST_PRODUCT_ID, args.getString("productId"))
    }
}
