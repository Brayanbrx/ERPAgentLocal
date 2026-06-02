package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AgentResponseBuilderTest {

    private val builder = AgentResponseBuilder()

    @Test
    fun rendersCustomerListWithoutBackendMessageOrStaleSummary() {
        val response = JSONObject()
            .put("success", true)
            .put("message", "Customers retrieved")
            .put(
                "data",
                JSONArray().put(
                    JSONObject()
                        .put("customerId", "CUST-1")
                        .put("firstName", "Ana")
                        .put("lastName", "Rojas")
                )
            )

        val message = builder.build(
            AgentExecutionReport(
                completed = true,
                state = AgentState(lastProductName = "pantalla"),
                actions = listOf(
                    AgentExecutedAction(
                        tool = "listCustomers",
                        arguments = JSONObject(),
                        response = response,
                        success = true
                    )
                ),
                pausedMessage = null
            )
        )

        assertTrue(message.contains("Clientes registrados:"))
        assertTrue(message.contains("- Ana Rojas"))
        assertFalse(message.contains("Customers retrieved"))
        assertFalse(message.contains("Resumen:"))
        assertFalse(message.contains("pantalla"))
    }
}
