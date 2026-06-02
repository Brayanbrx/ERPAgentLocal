package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AgentStateUpdaterTest {

    private val updater = AgentStateUpdater()

    @Test
    fun updatesCustomerFromCreateResponse() {
        val response = JSONObject()
            .put("success", true)
            .put("message", "ok")
            .put(
                "data",
                JSONObject()
                    .put("customerId", "CUST-1")
                    .put("firstName", "Juan")
                    .put("lastName", "Perez")
            )

        val state = updater.updateAndStoreExecution(
            toolName = "createCustomer",
            arguments = JSONObject().put("firstName", "Juan").put("lastName", "Perez"),
            response = response,
            currentState = AgentState.empty()
        )

        assertEquals("CUST-1", state.lastCustomerId)
        assertEquals("Juan Perez", state.lastCustomerName)
        assertEquals(1, state.executedTools.size)
    }

    @Test
    fun updatesProductAndPurchaseFromPurchaseResponse() {
        val response = JSONObject()
            .put("success", true)
            .put("message", "ok")
            .put(
                "data",
                JSONObject()
                    .put(
                        "purchase",
                        JSONObject()
                            .put("purchaseId", "PUR-1")
                            .put("productId", "PROD-1")
                            .put("productName", "azucar")
                    )
                    .put("inventory", JSONObject().put("productId", "PROD-1").put("stock", 50))
            )

        val state = updater.updateAndStoreExecution(
            toolName = "createPurchase",
            arguments = JSONObject().put("productId", "PROD-1"),
            response = response,
            currentState = AgentState(lastProductId = "PROD-1")
        )

        assertEquals("PUR-1", state.lastPurchaseId)
        assertEquals("PROD-1", state.lastProductId)
        assertEquals(50, state.lastInventoryStock)
    }

    @Test
    fun ignoresPlaceholderIdsFromResponses() {
        val response = JSONObject()
            .put("success", true)
            .put("message", "ok")
            .put(
                "data",
                JSONArray().put(JSONObject().put("productId", "\$lastProductId").put("name", "azucar"))
            )

        val state = updater.updateFromToolResponse(
            toolName = "searchProduct",
            arguments = JSONObject().put("name", "azucar"),
            response = response,
            currentState = AgentState.empty()
        )

        assertNull(state.lastProductId)
        assertEquals("azucar", state.lastProductName)
    }

    @Test
    fun listProductsDoesNotReplaceCurrentProductState() {
        val response = JSONObject()
            .put("success", true)
            .put("message", "Products retrieved")
            .put(
                "data",
                JSONArray().put(
                    JSONObject()
                        .put("productId", "PROD-2")
                        .put("name", "pantalla")
                )
            )

        val initialState = AgentState(
            lastProductId = "PROD-1",
            lastProductName = "cafe"
        )

        val state = updater.updateFromToolResponse(
            toolName = "listProducts",
            arguments = JSONObject(),
            response = response,
            currentState = initialState
        )

        assertEquals("PROD-1", state.lastProductId)
        assertEquals("cafe", state.lastProductName)
    }
}
