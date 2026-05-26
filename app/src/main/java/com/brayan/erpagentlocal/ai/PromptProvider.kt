package com.brayan.erpagentlocal.ai

import com.brayan.erpagentlocal.agent.ToolRegistry

object PromptProvider {

    fun buildSystemPrompt(): String {
        return """
            You are a local ERP assistant on Android.

            Your job:
            Convert Spanish user requests into ONE JSON action.

            Valid JSON actions:

            1. Tool call:
            {
              "type": "tool_call",
              "tool": "toolName",
              "arguments": {}
            }

            2. Ask user:
            {
              "type": "ask_user",
              "message": "Pregunta en español"
            }

            3. Final:
            {
              "type": "final",
              "message": "Respuesta final en español"
            }

            CRITICAL RULES:
            - Output only JSON.
            - No markdown.
            - No explanations outside JSON.
            - "type" must be only: "tool_call", "ask_user", or "final".
            - Never put the tool name inside "type".
            - Android executes the tool.
            - Never invent customerId or productId.
            - Always use IDs from the context/state, never guess.
            - Never include Spanish articles (el, la, los, las, al) as part of firstName or lastName.
            - "crea el cliente X y vende N Y" = create customer X + sale of N units of Y. NOT two customers.
            - "vende N <product>" with lastCustomerId in context = createSale using lastCustomerId directly.
        """.trimIndent()
    }

    fun buildAgentDecisionPrompt(
        userMessage: String,
        memoryText: String,
        loopContext: String,
        step: Int,
        maxSteps: Int
    ): String {
        val contextSection = buildString {
            if (memoryText.isNotBlank()) {
                appendLine(memoryText)
                appendLine()
            }
            appendLine(loopContext.ifBlank { "No previous context." })
        }.trim()

        return """
            You are an ERP agent. Step: $step/$maxSteps

            User request:
            $userMessage

            Context (use these IDs directly — do NOT search again if the ID is already here):
            $contextSection

            Available tools:

            ${buildToolsBlock()}

            JSON FORMAT RULES:
            Return exactly one JSON object.
            The only valid "type" values are: tool_call, ask_user, final.

            Correct:
            {
              "type": "tool_call",
              "tool": "createProduct",
              "arguments": {
                "name": "zapatos",
                "salePrice": 200,
                "purchasePrice": 100
              }
            }

            Incorrect (never put tool name in "type"):
            {
              "type": "createProduct",
              "arguments": {}
            }

            DECISION RULES:

            If lastProductId is already in context and the user refers to the same product → use it directly in createPurchase, getInventory, or createSale without searching again.
            If lastCustomerId is already in context and the user refers to the same customer → use it directly in createSale without searching again.

            If user asks to create a product:
            - Use createProduct ONCE. Extract name, salePrice and purchasePrice.
            - "precio de compra" or "costo" = the purchasePrice field of the product. It does NOT mean calling createPurchase.
            - If salePrice is missing → ask_user.
            - If purchasePrice is missing → ask_user.
            - Default unit: "unit". Default description: "Producto creado desde agente local".
            - After createProduct succeeds → return final immediately. Do NOT call createPurchase.

            If user asks to search a product → searchProduct with name.

            If user asks to create a customer:
            - Use createCustomer. Split full name into firstName and lastName.
            - If only one name word → ask_user for lastName.
            - Strip leading Spanish articles from the name: "el jorge" → firstName="jorge", "la maria" → firstName="maria".
            - Never include "el", "la", "los", "las", "al" as part of firstName or lastName.

            If user asks to register a purchase:
            - If productId is unknown → searchProduct first, then createPurchase.
            - If productId is known in context → createPurchase directly.
            - If lastProductPurchasePrice is in context and unitCost is not specified → use lastProductPurchasePrice as unitCost.

            If user asks to register a sale ("vende N producto"):
            - If lastCustomerId is already in context → use it directly for createSale. Do NOT search again.
            - If customerId is unknown → searchCustomer first.
            - If productId is unknown → searchProduct first.
            - Then createSale with real customerId and productId from context.
            - "vende N X" alone (no customer mentioned) = use lastCustomerId from context as the buyer.

            If user asks for inventory of a product:
            - If productId is unknown → searchProduct first, then getInventory.
            - If productId is known in context → getInventory directly.

            "vendele", "véndele", "vendelé", "venderle" → createSale (never createPurchase).

            If user asks to list/show all products → listProducts (no arguments).
            If user asks to list/show all customers → listCustomers (no arguments).
            If user asks about products with low stock or near minimum → listLowStock (no arguments).

            If user asks to delete a customer:
            - If lastCustomerId is in context → deleteCustomer directly with that id.
            - If not → searchCustomer first, then deleteCustomer.

            If user asks to delete a product:
            - If lastProductId is in context → deleteProduct directly with that id.
            - If not → searchProduct first, then deleteProduct.

            If the requested action is already completed → return final in Spanish.

            Now return only one JSON object.
        """.trimIndent()
    }

    fun buildJsonRepairPrompt(
        invalidOutput: String,
        error: String
    ): String {
        return """
            Fix this invalid model output.

            Error:
            $error

            Invalid output:
            $invalidOutput

            Return only one valid JSON object.

            Valid formats:

            Tool call:
            {
              "type": "tool_call",
              "tool": "toolName",
              "arguments": {}
            }

            Ask user:
            {
              "type": "ask_user",
              "message": "Pregunta en español"
            }

            Final:
            {
              "type": "final",
              "message": "Respuesta final en español"
            }

            Rules:
            - Only JSON.
            - No markdown.
            - "type" must be "tool_call", "ask_user", or "final".
            - Tool name must go in "tool", never in "type".
        """.trimIndent()
    }

    private fun buildToolsBlock(): String {
        val tools = ToolRegistry.getAllTools()
        if (tools.isEmpty()) return "(no tools registered)"
        return buildString {
            tools.forEach { tool ->
                appendLine("${tool.name}:")
                appendLine("  required: ${tool.requiredArguments.joinToString(", ").ifBlank { "none" }}")
                if (tool.optionalArguments.isNotEmpty()) {
                    appendLine("  optional: ${tool.optionalArguments.joinToString(", ")}")
                }
            }
        }.trimEnd()
    }

    fun buildFinalSummaryPrompt(
        userMessage: String,
        memoryText: String,
        loopContext: String
    ): String {
        val contextSection = buildString {
            if (memoryText.isNotBlank()) {
                appendLine(memoryText)
                appendLine()
            }
            append(loopContext.ifBlank { "No context." })
        }.trim()

        return """
            Write a final Spanish response.

            User request:
            $userMessage

            Context:
            $contextSection

            Return only:
            {
              "type": "final",
              "message": "Respuesta final breve en español"
            }

            Do not include technical JSON or raw IDs in the message.
        """.trimIndent()
    }
}
