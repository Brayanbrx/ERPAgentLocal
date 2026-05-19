package com.brayan.erpagentlocal.ai

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
        """.trimIndent()
    }

    fun buildAgentDecisionPrompt(
        userMessage: String,
        memoryText: String,
        loopContext: String,
        step: Int,
        maxSteps: Int
    ): String {
        return """
            You are an ERP agent.

            Step: $step/$maxSteps

            User request:
            $userMessage

            Context:
            ${loopContext.ifBlank { "No context yet." }}

            Available tools:

            createCustomer:
            required: firstName, lastName
            optional: phone, email

            searchCustomer:
            required: name

            createProduct:
            required: name, salePrice, purchasePrice
            optional: description, unit

            searchProduct:
            required: name

            updateProduct:
            required: productId
            optional: name, description, unit, salePrice, purchasePrice

            getInventory:
            required: productId

            createPurchase:
            required: productId, quantity, unitCost

            createSale:
            required: customerId, items
            item fields: productId, quantity

            JSON FORMAT RULES:
            Return exactly one JSON object.
            The only valid type values are:
            - tool_call
            - ask_user
            - final

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

            Incorrect:
            {
              "type": "createProduct",
              "tool": "createProduct",
              "arguments": {}
            }

            DECISION RULES:

            If user asks to create a product:
            - Use createProduct.
            - Extract name, salePrice and purchasePrice.
            - If salePrice is missing, ask_user.
            - If purchasePrice is missing, ask_user.
            - If unit is missing, use "unit".
            - If description is missing, use "Producto creado desde agente local".

            If user asks to search a product:
            - Use searchProduct with name.

            If user asks to create a customer:
            - Use createCustomer.
            - Split full name into firstName and lastName.
            - If lastName is missing, ask_user.

            If user asks to register a purchase:
            - If only product name is given, first use searchProduct.
            - Then use createPurchase with productId, quantity and unitCost.

            If user asks to register a sale:
            - If only customer name is given, first use searchCustomer.
            - If only product name is given, first use searchProduct.
            - Then use createSale with real customerId and productId.

            Do it in steps:
1. createCustomer with firstName = "Emanuel" and lastName = "Blanco"
2. searchProduct with name = "zapatos"
3. createSale with the real customerId and productId
4. final response in Spanish

Important:
- "vendele", "véndele", "vendelé" and "venderle" mean createSale.
- Never use createPurchase when the user says sell/vendele.
- Before createProduct, searchProduct should be used to avoid duplicates.
- Before createCustomer, searchCustomer should be used to avoid duplicates.

            If the requested action is already completed:
            - Return final in Spanish.

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

    fun buildFinalSummaryPrompt(
        userMessage: String,
        memoryText: String,
        loopContext: String
    ): String {
        return """
            Write a final Spanish response.

            User request:
            $userMessage

            Context:
            ${loopContext.ifBlank { "No context." }}

            Return only:
            {
              "type": "final",
              "message": "Respuesta final breve en español"
            }

            Do not include technical JSON in the message.
        """.trimIndent()
    }
}