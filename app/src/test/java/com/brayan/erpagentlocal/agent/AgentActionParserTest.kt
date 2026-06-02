package com.brayan.erpagentlocal.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AgentActionParserTest {

    private val parser = AgentActionParser()

    @Before
    fun setUp() {
        ToolRegistry.setCatalog(
            ToolCatalog(
                tools = listOf(
                    ToolDefinition(
                        name = "createCustomer",
                        operationId = "createCustomer",
                        method = "POST",
                        path = "/customers",
                        description = "Create customer",
                        requiredArguments = listOf("firstName", "lastName")
                    )
                )
            )
        )
    }

    @Test
    fun parsesToolCall() {
        val action = parser.parse(
            """
            {
              "type": "tool_call",
              "tool": "createCustomer",
              "arguments": {
                "firstName": "Juan",
                "lastName": "Perez"
              }
            }
            """.trimIndent()
        )

        assertTrue(action is AgentAction.ToolCall)
        action as AgentAction.ToolCall
        assertEquals("createCustomer", action.tool)
        assertEquals("Juan", action.arguments.getString("firstName"))
    }

    @Test
    fun repairsToolNameInType() {
        val action = parser.parse(
            """
            {
              "type": "createCustomer",
              "arguments": {
                "firstName": "Juan",
                "lastName": "Perez"
              }
            }
            """.trimIndent()
        )

        assertTrue(action is AgentAction.ToolCall)
        assertEquals("createCustomer", (action as AgentAction.ToolCall).tool)
    }

    @Test
    fun parsesAskUserAndFinal() {
        val ask = parser.parse("""{"type":"ask_user","message":"Falta el cliente"}""")
        val final = parser.parse("""{"type":"final","message":"Listo"}""")

        assertTrue(ask is AgentAction.AskUser)
        assertTrue(final is AgentAction.Final)
    }
}
