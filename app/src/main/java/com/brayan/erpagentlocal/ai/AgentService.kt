package com.brayan.erpagentlocal.ai

import com.brayan.erpagentlocal.agent.AgentAction
import com.brayan.erpagentlocal.agent.AgentActionParser
import com.brayan.erpagentlocal.agent.AgentErrorMapper
import com.brayan.erpagentlocal.agent.AgentMemory
import com.brayan.erpagentlocal.agent.AgentState
import com.brayan.erpagentlocal.agent.AgentStateUpdater
import com.brayan.erpagentlocal.agent.ToolCallValidator
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.data.ToolExecutor
import com.brayan.erpagentlocal.util.JsonUtils
import org.json.JSONArray
import org.json.JSONObject

class AgentService(
    private val parser: AgentActionParser = AgentActionParser(),
    private val toolExecutor: ToolExecutor = ToolExecutor(),
    private val memory: AgentMemory = AgentMemory(),
    private val stateUpdater: AgentStateUpdater = AgentStateUpdater(),
    private val toolCallValidator: ToolCallValidator = ToolCallValidator()
) {

    private var agentState: AgentState = AgentState.empty()

    suspend fun processUserMessage(input: String): String {
        val trimmed = input.trim()

        if (trimmed.isBlank()) {
            return "Escribe una instrucción o un comando."
        }

        memory.addUser(trimmed)

        val response = when {
            trimmed == "/tools" -> ToolRegistry.describeTools()

            trimmed == "/prompt" -> PromptProvider.buildSystemPrompt()

            trimmed == "/memory" -> memory.asText().ifBlank {
                "La memoria está vacía."
            }

            trimmed == "/memory-short" -> memory.asShortSummary()

            trimmed == "/state" -> agentState.toDebugText()

            trimmed == "/clear" -> {
                memory.clear()
                agentState = AgentState.empty()
                "Memoria y estado limpiados."
            }

            trimmed == "/example-customer" -> exampleCustomer()

            trimmed == "/example-product" -> exampleProduct()

            trimmed == "/example-purchase" -> examplePurchase()

            trimmed == "/example-sale" -> exampleSale()

            trimmed == "/agent-demo" -> runFullDemo()

            trimmed.startsWith("{") -> executeParsedAction(trimmed)

            else -> {
                """
                Para lenguaje natural usa el modelo local inicializado.

                Ejemplos:
                - Crea un cliente llamado Ana López
                - Crea un producto llamado Café con precio de venta 25 y precio de compra 18
                - Compra 50 unidades de Café a 18
                - Crea un cliente Emanuel Blanco y véndele 4 zapatos
                """.trimIndent()
            }
        }

        memory.addAssistant(response)
        return response
    }

    suspend fun processNaturalLanguageWithModel(
        userMessage: String,
        localModelService: LocalModelService,
        maxSteps: Int = 8
    ): String {
        return try {
            if (!localModelService.isInitialized()) {
                return AgentErrorMapper.fromModelNotReady().userMessage
            }

            val cleanUserMessage = userMessage.trim()

            if (cleanUserMessage.isBlank()) {
                return "Escribe una instrucción para el agente."
            }

            memory.addUser(cleanUserMessage)

            val directFlowResult = tryExecuteDirectSpanishFlow(cleanUserMessage)

            if (directFlowResult != null) {
                memory.addAssistant(directFlowResult)
                return directFlowResult
            }

            val loopContextBuilder = StringBuilder()

            var consecutiveValidationErrors = 0
            var consecutiveToolErrors = 0

            for (step in 1..maxSteps) {
                val prompt = PromptProvider.buildAgentDecisionPrompt(
                    userMessage = cleanUserMessage,
                    memoryText = "",
                    loopContext = buildLoopContext(
                        loopContext = loopContextBuilder.toString(),
                        state = agentState
                    ),
                    step = step,
                    maxSteps = maxSteps
                )

                val rawModelResponse = localModelService.generateDecision(prompt)
                val cleanedJson = JsonUtils.extractJsonObject(rawModelResponse)

                val parsedAction = parseOrRepairDecision(
                    rawOutput = cleanedJson,
                    localModelService = localModelService,
                    originalUserMessage = cleanUserMessage
                )

                when (parsedAction) {
                    is AgentAction.ToolCall -> {
                        val validationResult = toolCallValidator.validate(
                            action = parsedAction,
                            state = agentState
                        )

                        if (!validationResult.isValid) {
                            consecutiveValidationErrors++

                            val validationMessage = if (validationResult.shouldAskUser) {
                                validationResult.askUserMessage ?: validationResult.message
                            } else {
                                validationResult.message
                            }

                            if (validationResult.shouldAskUser) {
                                agentState = agentState.withPendingQuestion(validationMessage)
                                memory.addAssistant(validationMessage)

                                return renderFinalResult(
                                    finalMessage = validationMessage,
                                    state = agentState
                                )
                            }

                            val validationContext = buildValidationErrorContext(
                                tool = parsedAction.tool,
                                arguments = parsedAction.arguments,
                                message = validationMessage
                            )

                            loopContextBuilder.appendLine(validationContext)

                            if (consecutiveValidationErrors >= 2) {
                                val finalMessage = """
                                    No pude ejecutar la acción porque faltan datos o el modelo intentó usar información inválida.

                                    $validationMessage
                                """.trimIndent()

                                memory.addAssistant(finalMessage)

                                return renderFinalResult(
                                    finalMessage = finalMessage,
                                    state = agentState
                                )
                            }

                            continue
                        }

                        consecutiveValidationErrors = 0

                        val toolResponse = safeExecuteTool(parsedAction)
                        val toolSuccess = toolResponse.optBoolean("success", false)

                        if (AgentErrorMapper.isEmptySearchResult(parsedAction.tool, toolResponse)) {
                            val message = AgentErrorMapper.emptySearchMessage(
                                toolName = parsedAction.tool,
                                arguments = parsedAction.arguments
                            )

                            agentState = agentState.withPendingQuestion(message)
                            memory.addAssistant(message)

                            return renderFinalResult(
                                finalMessage = message,
                                state = agentState
                            )
                        }

                        storeToolResult(
                            toolName = parsedAction.tool,
                            arguments = parsedAction.arguments,
                            response = toolResponse
                        )

                        val toolContext = buildToolContext(
                            tool = parsedAction.tool,
                            arguments = parsedAction.arguments,
                            response = toolResponse,
                            state = agentState
                        )

                        loopContextBuilder.appendLine(toolContext)

                        if (!toolSuccess) {
                            consecutiveToolErrors++

                            if (shouldStopAfterToolError(toolResponse, consecutiveToolErrors)) {
                                val finalMessage = buildToolErrorFinalMessage(toolResponse)
                                memory.addAssistant(finalMessage)

                                return renderFinalResult(
                                    finalMessage = finalMessage,
                                    state = agentState
                                )
                            }
                        } else {
                            consecutiveToolErrors = 0
                        }
                    }

                    is AgentAction.AskUser -> {
                        val message = parsedAction.message

                        agentState = agentState.withPendingQuestion(message)
                        memory.addAssistant(message)

                        return renderFinalResult(
                            finalMessage = message,
                            state = agentState
                        )
                    }

                    is AgentAction.Final -> {
                        val message = parsedAction.message

                        agentState = agentState.clearPendingQuestion()
                        memory.addAssistant(message)

                        return renderFinalResult(
                            finalMessage = message,
                            state = agentState
                        )
                    }

                    is AgentAction.Invalid -> {
                        val message = parsedAction.error
                        memory.addAssistant(message)

                        return renderFinalResult(
                            finalMessage = message,
                            state = agentState
                        )
                    }
                }
            }

            finishAfterStepLimit(
                userMessage = cleanUserMessage,
                localModelService = localModelService,
                loopContext = loopContextBuilder.toString(),
                maxSteps = maxSteps
            )
        } catch (exception: Exception) {
            val message = """
                Ocurrió un error durante la ejecución del agente.

                Detalle:
                ${exception.message ?: "Error desconocido"}
            """.trimIndent()

            memory.addAssistant(message)
            message
        }
    }

    suspend fun checkHealth(): String {
        val response = toolExecutor.checkHealth()
        memory.addTool("health", response)
        return response
    }

    suspend fun runFullDemo(): String {
        val response = toolExecutor.runFullErpDemo()
        memory.addTool("demo", response)
        return response
    }

    fun getStateText(): String {
        return agentState.toDebugText()
    }

    fun getMemoryText(): String {
        return memory.asText()
    }

    private suspend fun tryExecuteDirectSpanishFlow(
    userMessage: String
): String? {
    parseMultipleCreateCustomersCommand(userMessage)?.let { customerNames ->
        return executeCreateMultipleCustomersFlow(customerNames)
    }

    parseCreateCustomerAndSaleCommand(userMessage)?.let { command ->
        return executeCreateCustomerAndSaleFlow(command)
    }

    buildFallbackActionFromUserMessage(userMessage)?.let { action ->
        return executeValidatedToolCall(action)
    }

    return null
}
    private suspend fun executeCreateMultipleCustomersFlow(
    customerNames: List<String>
): String {
    if (customerNames.isEmpty()) {
        return "No encontré nombres de clientes para crear."
    }

    val created = mutableListOf<String>()
    val existing = mutableListOf<String>()
    val failed = mutableListOf<String>()

    customerNames.forEach { fullName ->
        val searchArguments = JSONObject()
            .put("name", fullName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchCustomer",
                arguments = searchArguments
            )
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        )

        if (existingCustomer != null) {
            storeToolResult(
                toolName = "searchCustomer",
                arguments = searchArguments,
                response = searchResponse
            )

            existing.add(fullName)
            return@forEach
        }

        val splitName = splitFullName(fullName)

        if (splitName.first.isBlank() || splitName.second.isBlank()) {
            failed.add("$fullName: falta nombre o apellido")
            return@forEach
        }

        val createArguments = JSONObject()
            .put("firstName", splitName.first)
            .put("lastName", splitName.second)

        val createResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "createCustomer",
                arguments = createArguments
            )
        )

        storeToolResult(
            toolName = "createCustomer",
            arguments = createArguments,
            response = createResponse
        )

        if (createResponse.optBoolean("success", false)) {
            created.add(fullName)
        } else {
            failed.add("$fullName: ${createResponse.optString("message", "no se pudo crear")}")
        }
    }

    return buildString {
        if (created.isNotEmpty()) {
            appendLine("Clientes creados correctamente:")
            created.forEach { name ->
                appendLine("- $name")
            }
        }

        if (existing.isNotEmpty()) {
            if (isNotEmpty()) appendLine()
            appendLine("Clientes que ya existían:")
            existing.forEach { name ->
                appendLine("- $name")
            }
        }

        if (failed.isNotEmpty()) {
            if (isNotEmpty()) appendLine()
            appendLine("No se pudieron procesar:")
            failed.forEach { item ->
                appendLine("- $item")
            }
        }
    }.trim()
}

private fun parseMultipleCreateCustomersCommand(
    userMessage: String
): List<String>? {
    val text = normalizeText(userMessage)

    val looksLikeCreateCustomer =
        text.contains("crea") &&
                (
                        text.contains("usuario") ||
                                text.contains("cliente") ||
                                text.contains("customer")
                        )

    if (!looksLikeCreateCustomer) {
        return null
    }

    val cleaned = userMessage
        .replace(Regex("(?i)crea\\s+"), "")
        .replace(Regex("(?i)crear\\s+"), "")
        .replace(Regex("(?i)registra\\s+"), "")
        .replace(Regex("(?i)registrar\\s+"), "")
        .replace(Regex("(?i)un\\s+"), "")
        .replace(Regex("(?i)una\\s+"), "")
        .replace(Regex("(?i)al\\s+"), "")
        .replace(Regex("(?i)a\\s+"), "")
        .replace(Regex("(?i)cliente\\s+llamado\\s+"), "")
        .replace(Regex("(?i)usuario\\s+llamado\\s+"), "")
        .replace(Regex("(?i)cliente\\s+"), "")
        .replace(Regex("(?i)usuario\\s+"), "")
        .replace(Regex("(?i)customer\\s+"), "")
        .trim()

    val parts = cleaned
        .split(
            Regex("(?i)\\s+y\\s+otro\\s+|\\s+y\\s+otra\\s+|\\s+y\\s+|\\s*,\\s*")
        )
        .map { raw ->
            raw
                .replace(Regex("(?i)^cliente\\s+llamado\\s+"), "")
                .replace(Regex("(?i)^usuario\\s+llamado\\s+"), "")
                .replace(Regex("(?i)^cliente\\s+"), "")
                .replace(Regex("(?i)^usuario\\s+"), "")
                .replace(Regex("[.,;:]$"), "")
                .trim()
        }
        .filter { name ->
            val words = name.split(Regex("\\s+")).filter { it.isNotBlank() }
            words.size >= 2
        }

    if (parts.isEmpty()) {
        return null
    }

    return parts.distinctBy { normalizeText(it) }
}

    private suspend fun executeCreateCustomerAndSaleFlow(
        command: CreateCustomerAndSaleCommand
    ): String {
        val customer = findOrCreateCustomer(command.customerFullName)

        if (customer == null || customer.customerId.isBlank()) {
            return "No pude crear o encontrar al cliente ${command.customerFullName}."
        }

        val product = findProductByName(command.productName)

        if (product == null || product.productId.isBlank()) {
            return "No encontré el producto \"${command.productName}\". Créalo primero antes de registrar la venta."
        }

        val saleItems = JSONArray()
            .put(
                JSONObject()
                    .put("productId", product.productId)
                    .put("quantity", command.quantity)
            )

        val saleArguments = JSONObject()
            .put("customerId", customer.customerId)
            .put("items", saleItems)

        val saleAction = AgentAction.ToolCall(
            tool = "createSale",
            arguments = saleArguments
        )

        val validationResult = toolCallValidator.validate(
            action = saleAction,
            state = agentState
        )

        if (!validationResult.isValid) {
            return validationResult.askUserMessage
                ?: validationResult.message
        }

        val saleResponse = safeExecuteTool(saleAction)

        storeToolResult(
            toolName = "createSale",
            arguments = saleArguments,
            response = saleResponse
        )

        if (!saleResponse.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(saleResponse)
        }

        return renderFinalResult(
            finalMessage = "Listo. Creé o encontré al cliente ${command.customerFullName} y registré la venta de ${command.quantity} unidades de ${command.productName}.",
            state = agentState
        )
    }

    private suspend fun findOrCreateCustomer(
        fullName: String
    ): CustomerRef? {
        val searchArguments = JSONObject()
            .put("name", fullName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchCustomer",
                arguments = searchArguments
            )
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        )

        if (existingCustomer != null) {
            storeToolResult(
                toolName = "searchCustomer",
                arguments = searchArguments,
                response = searchResponse
            )

            val customerId = existingCustomer.optString("customerId", "")

            if (customerId.isNotBlank()) {
                return CustomerRef(
                    customerId = customerId,
                    customerName = fullName
                )
            }
        }

        val splitName = splitFullName(fullName)

        val createArguments = JSONObject()
            .put("firstName", splitName.first)
            .put("lastName", splitName.second)

        val createResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "createCustomer",
                arguments = createArguments
            )
        )

        storeToolResult(
            toolName = "createCustomer",
            arguments = createArguments,
            response = createResponse
        )

        val customerObject = firstDataObject(createResponse)
        val customerId = customerObject?.optString("customerId", "").orEmpty()

        if (customerId.isBlank()) {
            return null
        }

        return CustomerRef(
            customerId = customerId,
            customerName = fullName
        )
    }

    private suspend fun findProductByName(
        productName: String
    ): ProductRef? {
        val searchArguments = JSONObject()
            .put("name", productName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchProduct",
                arguments = searchArguments
            )
        )

        val productObject = findMatchingProduct(
            response = searchResponse,
            productName = productName
        )

        if (productObject == null) {
            return null
        }

        storeToolResult(
            toolName = "searchProduct",
            arguments = searchArguments,
            response = searchResponse
        )

        val productId = productObject.optString("productId", "")
        val name = productObject.optString("name", productName)

        if (productId.isBlank()) {
            return null
        }

        return ProductRef(
            productId = productId,
            productName = name
        )
    }

    private suspend fun executeParsedAction(rawText: String): String {
        return when (val action = parser.parse(rawText)) {
            is AgentAction.ToolCall -> executeValidatedToolCall(action)

            is AgentAction.AskUser -> {
                agentState = agentState.withPendingQuestion(action.message)
                action.message
            }

            is AgentAction.Final -> {
                agentState = agentState.clearPendingQuestion()
                action.message
            }

            is AgentAction.Invalid -> {
                action.error
            }
        }
    }

    private suspend fun executeValidatedToolCall(
        action: AgentAction.ToolCall
    ): String {
        val validationResult = toolCallValidator.validate(
            action = action,
            state = agentState
        )

        if (!validationResult.isValid) {
            val message = if (validationResult.shouldAskUser) {
                validationResult.askUserMessage ?: validationResult.message
            } else {
                "No ejecuté la acción porque no pasó la validación interna:\n${validationResult.message}"
            }

            if (validationResult.shouldAskUser) {
                agentState = agentState.withPendingQuestion(message)
            }

            return message
        }

        val existingEntityMessage = precheckBeforeCreate(action)

        if (existingEntityMessage != null) {
            return existingEntityMessage
        }

        val response = safeExecuteTool(action)

        storeToolResult(
            toolName = action.tool,
            arguments = action.arguments,
            response = response
        )

        if (!response.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(response)
        }

        return renderSuccessMessageFromTool(
            tool = action.tool,
            response = response,
            state = agentState
        )
    }

    private suspend fun precheckBeforeCreate(
        action: AgentAction.ToolCall
    ): String? {
        return when (action.tool) {
            "createProduct" -> precheckProductBeforeCreate(action)
            "createCustomer" -> precheckCustomerBeforeCreate(action)
            else -> null
        }
    }

    private suspend fun precheckProductBeforeCreate(
        action: AgentAction.ToolCall
    ): String? {
        val productName = action.arguments.optString("name", "").trim()

        if (productName.isBlank()) {
            return null
        }

        val searchArguments = JSONObject()
            .put("name", productName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchProduct",
                arguments = searchArguments
            )
        )

        val existingProduct = findMatchingProduct(
            response = searchResponse,
            productName = productName
        )

        if (existingProduct == null) {
            return null
        }

        storeToolResult(
            toolName = "searchProduct",
            arguments = searchArguments,
            response = searchResponse
        )

        val existingName = existingProduct.optString("name", productName)
        val existingId = existingProduct.optString("productId", "")

        return renderFinalResult(
            finalMessage = "El producto \"$existingName\" ya existe. No creé un duplicado.",
            state = agentState
        ) + if (existingId.isNotBlank()) "\nID existente: $existingId" else ""
    }

    private suspend fun precheckCustomerBeforeCreate(
        action: AgentAction.ToolCall
    ): String? {
        val firstName = action.arguments.optString("firstName", "").trim()
        val lastName = action.arguments.optString("lastName", "").trim()
        val fullName = "$firstName $lastName".trim()

        if (fullName.isBlank()) {
            return null
        }

        val searchArguments = JSONObject()
            .put("name", fullName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(
                tool = "searchCustomer",
                arguments = searchArguments
            )
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        )

        if (existingCustomer == null) {
            return null
        }

        storeToolResult(
            toolName = "searchCustomer",
            arguments = searchArguments,
            response = searchResponse
        )

        val existingId = existingCustomer.optString("customerId", "")

        return renderFinalResult(
            finalMessage = "El cliente \"$fullName\" ya existe. No creé un duplicado.",
            state = agentState
        ) + if (existingId.isNotBlank()) "\nID existente: $existingId" else ""
    }

    private suspend fun parseOrRepairDecision(
        rawOutput: String,
        localModelService: LocalModelService,
        originalUserMessage: String
    ): AgentAction {
        val parsed = parser.parse(rawOutput)

        if (parsed !is AgentAction.Invalid) {
            return parsed
        }

        val fallbackAction = buildFallbackActionFromUserMessage(originalUserMessage)

        if (fallbackAction != null) {
            return fallbackAction
        }

        val repairPrompt = PromptProvider.buildJsonRepairPrompt(
            invalidOutput = rawOutput,
            error = parsed.error
        )

        val repairedRawOutput = localModelService.generateDecision(repairPrompt)
        val repairedJson = JsonUtils.extractJsonObject(repairedRawOutput)

        return parser.parse(repairedJson)
    }

    private suspend fun finishAfterStepLimit(
        userMessage: String,
        localModelService: LocalModelService,
        loopContext: String,
        maxSteps: Int
    ): String {
        val summaryPrompt = PromptProvider.buildFinalSummaryPrompt(
            userMessage = userMessage,
            memoryText = "",
            loopContext = buildLoopContext(
                loopContext = loopContext,
                state = agentState
            )
        )

        val rawSummary = localModelService.generateDecision(summaryPrompt)
        val cleanSummaryJson = JsonUtils.extractJsonObject(rawSummary)

        val finalMessage = when (val action = parser.parse(cleanSummaryJson)) {
            is AgentAction.Final -> action.message
            else -> "El agente llegó al límite de $maxSteps pasos. Intenta escribir la instrucción de forma más específica."
        }

        memory.addAssistant(finalMessage)

        return renderFinalResult(
            finalMessage = finalMessage,
            state = agentState
        )
    }

    private suspend fun safeExecuteTool(
        action: AgentAction.ToolCall
    ): JSONObject {
        return try {
            toolExecutor.executeRaw(action)
        } catch (exception: Exception) {
            JSONObject()
                .put("success", false)
                .put("message", exception.message ?: "Error ejecutando la acción.")
                .put("statusCode", 0)
                .put("data", JSONObject())
                .put("error", exception.message ?: "Unknown error")
        }
    }

    private fun buildFallbackActionFromUserMessage(
        userMessage: String
    ): AgentAction.ToolCall? {
        val text = userMessage.trim()

        val createProductRegex = Regex(
            pattern = "(?i)crea\\s+(?:un\\s+)?producto\\s+llamado\\s+(.+?)\\s+con\\s+precio\\s+de\\s+venta\\s+(\\d+(?:\\.\\d+)?)\\s+y\\s+precio\\s+de\\s+compra\\s+(\\d+(?:\\.\\d+)?)"
        )

        createProductRegex.find(text)?.let { match ->
            val name = match.groupValues[1].trim()
            val salePrice = match.groupValues[2].toDoubleOrNull()
            val purchasePrice = match.groupValues[3].toDoubleOrNull()

            if (name.isNotBlank() && salePrice != null && purchasePrice != null) {
                return AgentAction.ToolCall(
                    tool = "createProduct",
                    arguments = JSONObject()
                        .put("name", name)
                        .put("description", "Producto creado desde agente local")
                        .put("unit", "unit")
                        .put("salePrice", salePrice)
                        .put("purchasePrice", purchasePrice)
                )
            }
        }

        val searchProductRegex = Regex(
            pattern = "(?i)(busca|buscar|buscame|búscame|encuentra|consulta)\\s+(?:un\\s+)?producto(?:\\s+llamado)?\\s+(.+)"
        )

        searchProductRegex.find(text)?.let { match ->
            val name = cleanProductName(match.groupValues[2])

            if (name.isNotBlank()) {
                return AgentAction.ToolCall(
                    tool = "searchProduct",
                    arguments = JSONObject()
                        .put("name", name)
                )
            }
        }

        return null
    }

    private fun parseCreateCustomerAndSaleCommand(
        userMessage: String
    ): CreateCustomerAndSaleCommand? {
        val text = userMessage.trim()

        val saleRegex = Regex(
            pattern = "(?i)(v[eé]ndele|vendele|vendel[eé]|venderle|vende)\\s+(\\d+)\\s+(?:unidades?\\s+de\\s+)?(.+)$"
        )

        val saleMatch = saleRegex.find(text) ?: return null

        val quantity = saleMatch.groupValues[2].toIntOrNull() ?: return null
        val productName = cleanProductName(saleMatch.groupValues[3])

        if (productName.isBlank()) {
            return null
        }

        val beforeSale = text
            .substring(0, saleMatch.range.first)
            .replace(Regex("(?i)\\s+y\\s*$"), "")
            .trim()

        val customerRegex = Regex(
            pattern = "(?i)crea\\s+(?:al\\s+|a\\s+|un\\s+|una\\s+)?(?:cliente|usuario|customer)?\\s*(.+)$"
        )

        val customerMatch = customerRegex.find(beforeSale) ?: return null
        val customerFullName = cleanCustomerName(customerMatch.groupValues[1])

        if (customerFullName.isBlank()) {
            return null
        }

        return CreateCustomerAndSaleCommand(
            customerFullName = customerFullName,
            quantity = quantity,
            productName = productName
        )
    }

    private fun firstDataObject(
        response: JSONObject
    ): JSONObject? {
        val data = response.opt("data")

        return when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }
    }

    private fun findMatchingProduct(
        response: JSONObject,
        productName: String
    ): JSONObject? {
        val data = response.opt("data")
        val target = normalizeText(productName)

        if (data is JSONObject) {
            val name = normalizeText(data.optString("name", ""))

            if (name == target) {
                return data
            }

            return null
        }

        if (data is JSONArray) {
            for (index in 0 until data.length()) {
                val item = data.optJSONObject(index) ?: continue
                val name = normalizeText(item.optString("name", ""))

                if (name == target) {
                    return item
                }
            }
        }

        return null
    }

    private fun findMatchingCustomer(
        response: JSONObject,
        fullName: String
    ): JSONObject? {
        val data = response.opt("data")
        val target = normalizeText(fullName)

        fun matches(customer: JSONObject): Boolean {
            val full = normalizeText(
                customer.optString(
                    "customerName",
                    "${customer.optString("firstName", "")} ${customer.optString("lastName", "")}"
                )
            )

            val name = normalizeText(customer.optString("name", ""))

            return full == target || name == target
        }

        if (data is JSONObject) {
            return if (matches(data)) data else null
        }

        if (data is JSONArray) {
            for (index in 0 until data.length()) {
                val item = data.optJSONObject(index) ?: continue

                if (matches(item)) {
                    return item
                }
            }
        }

        return null
    }

    private fun splitFullName(
        fullName: String
    ): Pair<String, String> {
        val parts = fullName
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        if (parts.isEmpty()) {
            return Pair("", "")
        }

        if (parts.size == 1) {
            return Pair(parts[0], "")
        }

        val lastName = parts.last()
        val firstName = parts.dropLast(1).joinToString(" ")

        return Pair(firstName, lastName)
    }

    private fun normalizeText(value: String): String {
        return value
            .trim()
            .lowercase()
            .replace(Regex("\\s+"), " ")
    }

    private fun cleanProductName(value: String): String {
        return value
            .trim()
            .replace(Regex("(?i)^de\\s+"), "")
            .replace(Regex("[.,;:]$"), "")
            .trim()
    }

    private fun cleanCustomerName(value: String): String {
        return value
            .trim()
            .replace(Regex("[.,;:]$"), "")
            .trim()
    }

    private fun storeToolResult(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject
    ) {
        memory.addTool(
            toolName = toolName,
            content = response.toString()
        )

        agentState = stateUpdater.updateAndStoreExecution(
            toolName = toolName,
            arguments = arguments,
            response = response,
            currentState = agentState
        )
    }

    private fun shouldStopAfterToolError(
        toolResponse: JSONObject,
        consecutiveToolErrors: Int
    ): Boolean {
        val statusCode = toolResponse.optInt("statusCode", 0)
        val message = toolResponse.optString("message", "").lowercase()
        val userMessage = toolResponse.optString("userMessage", "").lowercase()

        if (statusCode == 400) return true
        if (statusCode == 404) return true
        if (statusCode == 409) return true
        if (message.contains("stock") || userMessage.contains("stock")) return true
        if (consecutiveToolErrors >= 2) return true

        return false
    }

    private fun buildToolErrorFinalMessage(
        toolResponse: JSONObject
    ): String {
        val mappedError = AgentErrorMapper.fromHttpResponse(toolResponse)
        return mappedError.userMessage
    }

    private fun renderFinalResult(
        finalMessage: String,
        state: AgentState
    ): String {
        return buildString {
            appendLine(finalMessage)

            if (
                !state.lastCustomerName.isNullOrBlank() ||
                !state.lastProductName.isNullOrBlank() ||
                !state.lastSaleId.isNullOrBlank() ||
                !state.lastPurchaseId.isNullOrBlank() ||
                state.lastInventoryStock != null
            ) {
                appendLine()
                appendLine("Resumen:")

                if (!state.lastCustomerName.isNullOrBlank()) {
                    appendLine("- Cliente: ${state.lastCustomerName}")
                }

                if (!state.lastProductName.isNullOrBlank()) {
                    appendLine("- Producto: ${state.lastProductName}")
                }

                if (!state.lastSaleId.isNullOrBlank()) {
                    appendLine("- Venta: ${state.lastSaleId}")
                }

                if (!state.lastPurchaseId.isNullOrBlank()) {
                    appendLine("- Compra: ${state.lastPurchaseId}")
                }

                if (state.lastInventoryStock != null) {
                    appendLine("- Stock actual: ${state.lastInventoryStock}")
                }
            }
        }.trim()
    }

    private fun renderSuccessMessageFromTool(
        tool: String,
        response: JSONObject,
        state: AgentState
    ): String {
        val backendMessage = response.optString("message", "")

        val baseMessage = when (tool) {
            "createCustomer" -> "Listo. El cliente fue creado correctamente."
            "createProduct" -> "Listo. El producto fue creado correctamente."
            "createPurchase" -> "Listo. La compra fue registrada correctamente."
            "createSale" -> "Listo. La venta fue registrada correctamente."
            "searchProduct" -> "Listo. Encontré la información del producto."
            "searchCustomer" -> "Listo. Encontré la información del cliente."
            "getInventory" -> "Listo. Consulté el inventario correctamente."
            else -> backendMessage.ifBlank { "Listo. La operación fue completada correctamente." }
        }

        return renderFinalResult(
            finalMessage = baseMessage,
            state = state
        )
    }

    private fun buildLoopContext(
        loopContext: String,
        state: AgentState
    ): String {
        return buildString {
            appendLine("State:")
            appendLine("lastCustomerId=${state.lastCustomerId ?: "none"}")
            appendLine("lastCustomerName=${state.lastCustomerName ?: "none"}")
            appendLine("lastProductId=${state.lastProductId ?: "none"}")
            appendLine("lastProductName=${state.lastProductName ?: "none"}")
            appendLine("lastSaleId=${state.lastSaleId ?: "none"}")
            appendLine("lastPurchaseId=${state.lastPurchaseId ?: "none"}")
            appendLine("lastInventoryStock=${state.lastInventoryStock ?: "none"}")

            if (loopContext.isNotBlank()) {
                appendLine()
                appendLine("Last tool result:")
                appendLine(loopContext.takeLast(1200))
            }
        }
    }

    private fun buildToolContext(
        tool: String,
        arguments: JSONObject,
        response: JSONObject,
        state: AgentState
    ): String {
        return buildString {
            appendLine("tool=$tool")
            appendLine("success=${response.optBoolean("success", false)}")
            appendLine("message=${response.optString("message", "")}")

            val data = response.opt("data")

            if (data != null) {
                appendLine("data=${data.toString().take(800)}")
            }

            appendLine("lastCustomerId=${state.lastCustomerId ?: "none"}")
            appendLine("lastProductId=${state.lastProductId ?: "none"}")
        }
    }

    private fun buildValidationErrorContext(
        tool: String,
        arguments: JSONObject,
        message: String
    ): String {
        return buildString {
            appendLine("validation_error")
            appendLine("tool=$tool")
            appendLine("arguments=${arguments.toString()}")
            appendLine("message=${escapeForJson(message)}")
        }
    }

    private fun escapeForJson(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }

    private fun exampleCustomer(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createCustomer",
              "arguments": {
                "firstName": "Carlos",
                "lastName": "Rojas",
                "phone": "70000000",
                "email": "carlos@email.com"
              }
            }
        """.trimIndent()
    }

    private fun exampleProduct(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createProduct",
              "arguments": {
                "name": "Arroz",
                "description": "Bolsa de arroz 1kg",
                "unit": "unit",
                "salePrice": 18,
                "purchasePrice": 12
              }
            }
        """.trimIndent()
    }

    private fun examplePurchase(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createPurchase",
              "arguments": {
                "productId": "PEGA_AQUI_EL_PRODUCT_ID",
                "quantity": 50,
                "unitCost": 12
              }
            }
        """.trimIndent()
    }

    private fun exampleSale(): String {
        return """
            {
              "type": "tool_call",
              "tool": "createSale",
              "arguments": {
                "customerId": "PEGA_AQUI_EL_CUSTOMER_ID",
                "items": [
                  {
                    "productId": "PEGA_AQUI_EL_PRODUCT_ID",
                    "quantity": 2
                  }
                ]
              }
            }
        """.trimIndent()
    }

    private data class CreateCustomerAndSaleCommand(
        val customerFullName: String,
        val quantity: Int,
        val productName: String
    )

    private data class CustomerRef(
        val customerId: String,
        val customerName: String
    )

    private data class ProductRef(
        val productId: String,
        val productName: String
    )
}