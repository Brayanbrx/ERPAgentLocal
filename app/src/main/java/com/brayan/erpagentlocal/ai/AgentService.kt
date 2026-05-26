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
                    memoryText = memory.buildCompactContext(),
                    loopContext = buildLoopContext(
                        loopContext = loopContextBuilder.toString(),
                        state = agentState
                    ),
                    step = step,
                    maxSteps = maxSteps
                )

                val rawModelResponse = localModelService.generateDecisionFresh(prompt)
                val cleanedJson = JsonUtils.extractJsonObject(rawModelResponse)

                val parsedAction = parseOrRepairDecision(
                    rawOutput = cleanedJson,
                    localModelService = localModelService,
                    originalUserMessage = cleanUserMessage
                )

                when (parsedAction) {
                    is AgentAction.ToolCall -> {
                        // Inyecta contexto conocido (ej. unitCost desde lastProductPurchasePrice) antes de validar.
                        @Suppress("NAME_SHADOWING")
                        val parsedAction = enrichToolCallFromState(parsedAction, agentState)

                        val validationResult = toolCallValidator.validate(
                            action = parsedAction,
                            state = agentState
                        )

                        if (!validationResult.isValid) {
                            // La tarea ya fue completada en un paso anterior — devuelve éxito.
                            if (validationResult.isAlreadyDone) {
                                agentState = agentState.clearPendingQuestion()
                                val finalMessage = renderFinalResult(
                                    finalMessage = "Listo. La operación fue completada correctamente.",
                                    state = agentState
                                )
                                memory.addAssistant(finalMessage)
                                return finalMessage
                            }

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

                        // Evita crear entidades duplicadas dentro del bucle LLM.
                        // precheckBeforeCreate consulta el backend antes de ejecutar createProduct/createCustomer.
                        val duplicateMessage = precheckBeforeCreate(parsedAction)
                        if (duplicateMessage != null) {
                            agentState = agentState.clearPendingQuestion()
                            memory.addAssistant(duplicateMessage)
                            return duplicateMessage
                        }

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

                            // Las herramientas terminales completan la tarea — retorna sin dar otro turno al LLM.
                            if (isTerminalTool(parsedAction.tool)) {
                                val finalMessage = renderSuccessMessageFromTool(
                                    tool = parsedAction.tool,
                                    response = toolResponse,
                                    state = agentState
                                )
                                agentState = agentState.clearPendingQuestion()
                                memory.addAssistant(finalMessage)
                                return finalMessage
                            }
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

    suspend fun checkBackendStatus(): String = toolExecutor.pingBackend()

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

    // ─────────────────────────────────────────────────────────────────────────
    // Flujo directo en español — omite el LLM para patrones de comando conocidos
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun tryExecuteDirectSpanishFlow(userMessage: String): String? {
        // Convierte números en español hablado a dígitos para que los patrones regex funcionen.
        // ej. "precio de venta cinco" → "precio de venta 5"
        val msg = normalizeSpanishNumbers(userMessage)

        parseCreateCustomerAndSaleCommand(msg)?.let { command ->
            return executeCreateCustomerAndSaleFlow(command)
        }

        parseMultipleCreateCustomersCommand(msg)?.let { customerNames ->
            return executeCreateMultipleCustomersFlow(customerNames)
        }

        parseSaleCommand(msg)?.let { command ->
            return executeSaleFlow(command)
        }

        parsePurchaseCommand(msg)?.let { command ->
            return executePurchaseFlow(command)
        }

        parseInventoryLookupCommand(msg)?.let { productName ->
            return executeInventoryLookupFlow(productName)
        }

        return null
    }

    // ── Venta directa: "vende 5 audífonos" ───────────────────────────────────

    private fun parseSaleCommand(userMessage: String): SaleCommand? {
        val text = userMessage.trim()

        // Debe empezar con "vende[r]" pero NO con "véndele/vendele" (esos van al flujo cliente+venta)
        val regex = Regex("(?i)^vende[r]?\\s+(\\d+)\\s+(?:unidades?\\s+de\\s+)?(.+)")
        val match = regex.find(text) ?: return null

        // Descarta si contiene "vendele" o "véndele" — los gestiona parseCreateCustomerAndSaleCommand
        if (text.matches(Regex("(?i).*v[eé]ndel[ae].*"))) return null

        val quantity = match.groupValues[1].toIntOrNull() ?: return null
        val productName = cleanProductName(match.groupValues[2])
        if (productName.isBlank() || quantity <= 0) return null

        return SaleCommand(productName = productName, quantity = quantity)
    }

    private suspend fun executeSaleFlow(command: SaleCommand): String {
        val customerId = agentState.lastCustomerId
            ?: return "¿A quién le quieres vender ${command.quantity} unidades de \"${command.productName}\"? Dime el nombre del cliente."

        val product = findProductByName(command.productName)
        if (product == null || product.productId.isBlank()) {
            return "No encontré el producto \"${command.productName}\". " +
                "Asegúrate de que esté registrado en el ERP."
        }

        val saleItems = JSONArray().put(
            JSONObject()
                .put("productId", product.productId)
                .put("quantity", command.quantity)
        )

        val saleArguments = JSONObject()
            .put("customerId", customerId)
            .put("items", saleItems)

        val saleAction = AgentAction.ToolCall(tool = "createSale", arguments = saleArguments)

        val validationResult = toolCallValidator.validate(saleAction, agentState)
        if (!validationResult.isValid) {
            return validationResult.askUserMessage ?: validationResult.message
        }

        val saleResponse = safeExecuteTool(saleAction)
        storeToolResult("createSale", saleArguments, saleResponse)

        if (!saleResponse.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(saleResponse)
        }

        val customerName = agentState.lastCustomerName ?: customerId
        return renderFinalResult(
            finalMessage = "Listo. Registré la venta de ${command.quantity} unidades de " +
                "${command.productName} al cliente $customerName.",
            state = agentState
        )
    }

    // ── Compra: "compra 10 unidades de café a 18" ────────────────────────────

    private fun parsePurchaseCommand(userMessage: String): PurchaseCommand? {
        val text = userMessage.trim()

        // "compra[r] <cantidad> [unidades de] <producto> a <precio>"
        val withPriceRegex = Regex(
            "(?i)^compra[r]?\\s+(\\d+)\\s+(?:unidades?\\s+de\\s+)?(.+?)\\s+a\\s+(\\d+(?:[.,]\\d+)?)"
        )
        withPriceRegex.find(text)?.let { match ->
            val quantity = match.groupValues[1].toIntOrNull() ?: return null
            val productName = cleanProductName(match.groupValues[2])
            val unitCost = match.groupValues[3].replace(",", ".").toDoubleOrNull() ?: return null
            if (productName.isBlank() || quantity <= 0 || unitCost < 0) return null
            return PurchaseCommand(productName = productName, quantity = quantity, unitCost = unitCost)
        }

        // "compra[r] <cantidad> [unidades de] <producto>" — precio tomado del purchasePrice del producto en el estado
        val noPriceRegex = Regex(
            "(?i)^compra[r]?\\s+(\\d+)\\s+(?:unidades?\\s+de\\s+)?(.+)"
        )
        noPriceRegex.find(text)?.let { match ->
            val quantity = match.groupValues[1].toIntOrNull() ?: return null
            val productName = cleanProductName(match.groupValues[2])
            if (productName.isBlank() || quantity <= 0) return null
            return PurchaseCommand(productName = productName, quantity = quantity, unitCost = null)
        }

        return null
    }

    private suspend fun executePurchaseFlow(command: PurchaseCommand): String {
        val product = findProductByName(command.productName)

        if (product == null || product.productId.isBlank()) {
            return "No encontré el producto \"${command.productName}\". " +
                "Créalo primero con nombre, precio de venta y precio de compra."
        }

        // Use explicit price if provided; fall back to product's stored purchasePrice.
        val resolvedUnitCost = command.unitCost
            ?: agentState.lastProductPurchasePrice
            ?: return "¿A qué costo unitario deseas registrar la compra de \"${command.productName}\"?"

        val purchaseArgs = JSONObject()
            .put("productId", product.productId)
            .put("quantity", command.quantity)
            .put("unitCost", resolvedUnitCost)

        val purchaseAction = AgentAction.ToolCall(
            tool = "createPurchase",
            arguments = purchaseArgs
        )

        val validationResult = toolCallValidator.validate(purchaseAction, agentState)
        if (!validationResult.isValid) {
            return validationResult.askUserMessage ?: validationResult.message
        }

        val purchaseResponse = safeExecuteTool(purchaseAction)
        storeToolResult("createPurchase", purchaseArgs, purchaseResponse)

        if (!purchaseResponse.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(purchaseResponse)
        }

        return renderFinalResult(
            finalMessage = "Listo. Registré la compra de ${command.quantity} unidades de " +
                "${command.productName} a $resolvedUnitCost cada una.",
            state = agentState
        )
    }

    // ── Consulta de inventario: "consulta inventario de café" ────────────────

    private fun parseInventoryLookupCommand(userMessage: String): String? {
        val text = userMessage.trim()

        val primaryRegex = Regex(
            pattern = "(?i)(?:consulta?|verifica?|revisa?|mira|ve[r]?)\\s+(?:el\\s+)?inventario(?:\\s+de)?\\s+(.+)"
        )
        primaryRegex.find(text)?.let { match ->
            val name = cleanProductName(match.groupValues[1])
            if (name.isNotBlank()) return name
        }

        val stockRegex = Regex(
            pattern = "(?i)(?:cu[aá]nto[s]?|qu[eé]|c[oó]mo|cuanto)\\s+(?:stock|inventario|queda[n]?)\\s+" +
                "(?:hay\\s+)?(?:de\\s+)?(.+)"
        )
        stockRegex.find(text)?.let { match ->
            val name = cleanProductName(match.groupValues[1])
            if (name.isNotBlank()) return name
        }

        return null
    }

    private suspend fun executeInventoryLookupFlow(productName: String): String {
        val product = findProductByName(productName)

        if (product == null || product.productId.isBlank()) {
            return "No encontré el producto \"$productName\". " +
                "Asegúrate de que esté registrado en el ERP."
        }

        val inventoryArgs = JSONObject().put("productId", product.productId)
        val inventoryAction = AgentAction.ToolCall(
            tool = "getInventory",
            arguments = inventoryArgs
        )

        val validationResult = toolCallValidator.validate(inventoryAction, agentState)
        if (!validationResult.isValid) {
            return validationResult.askUserMessage ?: validationResult.message
        }

        val inventoryResponse = safeExecuteTool(inventoryAction)
        storeToolResult("getInventory", inventoryArgs, inventoryResponse)

        if (!inventoryResponse.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(inventoryResponse)
        }

        val data = inventoryResponse.optJSONObject("data")
        val stock = data?.optInt("stock", 0) ?: 0
        val minStock = data?.optInt("minStock", 0) ?: 0

        val stockMessage = when {
            stock <= 0 -> "El producto \"${product.productName}\" está sin stock."
            stock <= minStock -> "El producto \"${product.productName}\" tiene stock bajo: " +
                "$stock unidades (mínimo recomendado: $minStock)."
            else -> "El producto \"${product.productName}\" tiene $stock unidades en inventario."
        }

        return renderFinalResult(finalMessage = stockMessage, state = agentState)
    }

    // ── Múltiples clientes: "crea cliente Juan y cliente Ana" ─────────────────

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
            val searchArguments = JSONObject().put("name", fullName)

            val searchResponse = safeExecuteTool(
                AgentAction.ToolCall(tool = "searchCustomer", arguments = searchArguments)
            )

            val existingCustomer = findMatchingCustomer(
                response = searchResponse,
                fullName = fullName
            )

            if (existingCustomer != null) {
                storeToolResult("searchCustomer", searchArguments, searchResponse)
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
                AgentAction.ToolCall(tool = "createCustomer", arguments = createArguments)
            )

            storeToolResult("createCustomer", createArguments, createResponse)

            if (createResponse.optBoolean("success", false)) {
                created.add(fullName)
            } else {
                failed.add("$fullName: ${createResponse.optString("message", "no se pudo crear")}")
            }
        }

        return buildString {
            if (created.isNotEmpty()) {
                appendLine("Clientes creados correctamente:")
                created.forEach { name -> appendLine("- $name") }
            }

            if (existing.isNotEmpty()) {
                if (isNotEmpty()) appendLine()
                appendLine("Clientes que ya existían:")
                existing.forEach { name -> appendLine("- $name") }
            }

            if (failed.isNotEmpty()) {
                if (isNotEmpty()) appendLine()
                appendLine("No se pudieron procesar:")
                failed.forEach { item -> appendLine("- $item") }
            }
        }.trim()
    }

    private fun parseMultipleCreateCustomersCommand(userMessage: String): List<String>? {
        val text = normalizeText(userMessage)

        val looksLikeCreateCustomer =
            text.contains("crea") &&
                (text.contains("usuario") ||
                    text.contains("cliente") ||
                    text.contains("customer"))

        if (!looksLikeCreateCustomer) return null

        val cleaned = userMessage
            .replace(Regex("(?i)crea[r]?\\s+"), "")
            .replace(Regex("(?i)registra[r]?\\s+"), "")
            .replace(Regex("(?i)un[a]?\\s+"), "")
            .replace(Regex("(?i)al\\s+"), "")
            .replace(Regex("(?i)\\ba\\b\\s+"), "")
            .replace(Regex("(?i)cliente\\s+llamado\\s+"), "")
            .replace(Regex("(?i)usuario\\s+llamado\\s+"), "")
            .replace(Regex("(?i)cliente\\s+"), "")
            .replace(Regex("(?i)usuario\\s+"), "")
            .replace(Regex("(?i)customer\\s+"), "")
            .trim()

        val parts = cleaned
            .split(Regex("(?i)\\s+y\\s+otro\\s+|\\s+y\\s+otra\\s+|\\s+y\\s+|\\s*,\\s*"))
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
                if (words.size < 2) return@filter false
                // Reject parts that look like commands, not names
                val actionVerbs = setOf("vende", "vender", "vendele", "compra", "comprar", "registra", "busca", "consulta")
                if (words.first().lowercase() in actionVerbs) return@filter false
                if (name.any { it.isDigit() }) return@filter false
                true
            }

        if (parts.isEmpty()) return null

        return parts.distinctBy { normalizeText(it) }
    }

    // ── Crear cliente + venta: "crea a Emanuel y véndele 4 zapatos" ──────────

    private suspend fun executeCreateCustomerAndSaleFlow(
        command: CreateCustomerAndSaleCommand
    ): String {
        val customer = findOrCreateCustomer(command.customerFullName)

        if (customer == null || customer.customerId.isBlank()) {
            return "No pude crear o encontrar al cliente ${command.customerFullName}."
        }

        val product = findProductByName(command.productName)

        if (product == null || product.productId.isBlank()) {
            return "No encontré el producto \"${command.productName}\". " +
                "Créalo primero antes de registrar la venta."
        }

        val saleItems = JSONArray().put(
            JSONObject()
                .put("productId", product.productId)
                .put("quantity", command.quantity)
        )

        val saleArguments = JSONObject()
            .put("customerId", customer.customerId)
            .put("items", saleItems)

        val saleAction = AgentAction.ToolCall(tool = "createSale", arguments = saleArguments)

        val validationResult = toolCallValidator.validate(saleAction, agentState)
        if (!validationResult.isValid) {
            return validationResult.askUserMessage ?: validationResult.message
        }

        val saleResponse = safeExecuteTool(saleAction)
        storeToolResult("createSale", saleArguments, saleResponse)

        if (!saleResponse.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(saleResponse)
        }

        return renderFinalResult(
            finalMessage = "Listo. Creé o encontré al cliente ${command.customerFullName} " +
                "y registré la venta de ${command.quantity} unidades de ${command.productName}.",
            state = agentState
        )
    }

    private suspend fun findOrCreateCustomer(fullName: String): CustomerRef? {
        val searchArguments = JSONObject().put("name", fullName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(tool = "searchCustomer", arguments = searchArguments)
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        )

        if (existingCustomer != null) {
            storeToolResult("searchCustomer", searchArguments, searchResponse)
            val customerId = existingCustomer.optString("customerId", "")
            if (customerId.isNotBlank()) {
                return CustomerRef(customerId = customerId, customerName = fullName)
            }
        }

        val splitName = splitFullName(fullName)

        val createArguments = JSONObject()
            .put("firstName", splitName.first)
            .put("lastName", splitName.second)

        val createResponse = safeExecuteTool(
            AgentAction.ToolCall(tool = "createCustomer", arguments = createArguments)
        )

        storeToolResult("createCustomer", createArguments, createResponse)

        val customerObject = firstDataObject(createResponse)
        val customerId = customerObject?.optString("customerId", "").orEmpty()

        if (customerId.isBlank()) return null

        return CustomerRef(customerId = customerId, customerName = fullName)
    }

    private suspend fun findProductByName(productName: String): ProductRef? {
        val searchArguments = JSONObject().put("name", productName)

        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(tool = "searchProduct", arguments = searchArguments)
        )

        val productObject = findMatchingProduct(
            response = searchResponse,
            productName = productName
        )

        if (productObject == null) return null

        storeToolResult("searchProduct", searchArguments, searchResponse)

        val productId = productObject.optString("productId", "")
        val name = productObject.optString("name", productName)

        if (productId.isBlank()) return null

        return ProductRef(productId = productId, productName = name)
    }

    private fun parseCreateCustomerAndSaleCommand(userMessage: String): CreateCustomerAndSaleCommand? {
        val text = userMessage.trim()

        val saleRegex = Regex(
            pattern = "(?i)(v[eé]ndele|vendele|vende|véndele|venderle)\\s+(\\d+)\\s+(?:unidades?\\s+de\\s+)?(.+)$"
        )

        val saleMatch = saleRegex.find(text) ?: return null
        val quantity = saleMatch.groupValues[2].toIntOrNull() ?: return null
        val productName = cleanProductName(saleMatch.groupValues[3])

        if (productName.isBlank()) return null

        val beforeSale = text
            .substring(0, saleMatch.range.first)
            .replace(Regex("(?i)\\s+y\\s*$"), "")
            .trim()

        val customerRegex = Regex(
            pattern = "(?i)crea[r]?\\s+(?:al?\\s+|a\\s+|un[a]?\\s+)?(?:(?:el|la|los|las)\\s+)?(?:cliente|usuario|customer)?\\s*(.+)$"
        )

        val customerMatch = customerRegex.find(beforeSale) ?: return null
        val customerFullName = cleanCustomerName(customerMatch.groupValues[1])

        if (customerFullName.isBlank()) return null

        return CreateCustomerAndSaleCommand(
            customerFullName = customerFullName,
            quantity = quantity,
            productName = productName
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Auxiliares de ejecución de herramientas
    // ─────────────────────────────────────────────────────────────────────────

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

            is AgentAction.Invalid -> action.error
        }
    }

    private suspend fun executeValidatedToolCall(action: AgentAction.ToolCall): String {
        val validationResult = toolCallValidator.validate(action = action, state = agentState)

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
        if (existingEntityMessage != null) return existingEntityMessage

        val response = safeExecuteTool(action)
        storeToolResult(toolName = action.tool, arguments = action.arguments, response = response)

        if (!response.optBoolean("success", false)) {
            return buildToolErrorFinalMessage(response)
        }

        return renderSuccessMessageFromTool(
            tool = action.tool,
            response = response,
            state = agentState
        )
    }

    private suspend fun precheckBeforeCreate(action: AgentAction.ToolCall): String? {
        return when (action.tool) {
            "createProduct" -> precheckProductBeforeCreate(action)
            "createCustomer" -> precheckCustomerBeforeCreate(action)
            else -> null
        }
    }

    private suspend fun precheckProductBeforeCreate(action: AgentAction.ToolCall): String? {
        val productName = action.arguments.optString("name", "").trim()
        if (productName.isBlank()) return null

        val searchArguments = JSONObject().put("name", productName)
        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(tool = "searchProduct", arguments = searchArguments)
        )

        val existingProduct = findMatchingProduct(
            response = searchResponse,
            productName = productName
        ) ?: return null

        storeToolResult("searchProduct", searchArguments, searchResponse)

        val existingName = existingProduct.optString("name", productName)
        val existingId = existingProduct.optString("productId", "")

        return renderFinalResult(
            finalMessage = "El producto \"$existingName\" ya existe. No creé un duplicado.",
            state = agentState
        ) + if (existingId.isNotBlank()) "\nID existente: $existingId" else ""
    }

    private suspend fun precheckCustomerBeforeCreate(action: AgentAction.ToolCall): String? {
        val firstName = action.arguments.optString("firstName", "").trim()
        val lastName = action.arguments.optString("lastName", "").trim()
        val fullName = "$firstName $lastName".trim()
        if (fullName.isBlank()) return null

        val searchArguments = JSONObject().put("name", fullName)
        val searchResponse = safeExecuteTool(
            AgentAction.ToolCall(tool = "searchCustomer", arguments = searchArguments)
        )

        val existingCustomer = findMatchingCustomer(
            response = searchResponse,
            fullName = fullName
        ) ?: return null

        storeToolResult("searchCustomer", searchArguments, searchResponse)

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
        if (parsed !is AgentAction.Invalid) return parsed

        val repairPrompt = PromptProvider.buildJsonRepairPrompt(
            invalidOutput = rawOutput,
            error = parsed.error
        )

        val repairedRawOutput = localModelService.generateDecisionFresh(repairPrompt)
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
            memoryText = memory.buildCompactContext(),
            loopContext = buildLoopContext(loopContext = loopContext, state = agentState)
        )

        val rawSummary = localModelService.generateDecisionFresh(summaryPrompt)
        val cleanSummaryJson = JsonUtils.extractJsonObject(rawSummary)

        val finalMessage = when (val action = parser.parse(cleanSummaryJson)) {
            is AgentAction.Final -> action.message
            else -> "El agente llegó al límite de $maxSteps pasos. " +
                "Intenta escribir la instrucción de forma más específica."
        }

        memory.addAssistant(finalMessage)

        return renderFinalResult(finalMessage = finalMessage, state = agentState)
    }

    private suspend fun safeExecuteTool(action: AgentAction.ToolCall): JSONObject {
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

    // ─────────────────────────────────────────────────────────────────────────
    // Auxiliares de estado y memoria
    // ─────────────────────────────────────────────────────────────────────────

    private fun storeToolResult(
        toolName: String,
        arguments: JSONObject,
        response: JSONObject
    ) {
        // Registra hechos compactos para el contexto del LLM (no JSON completo)
        recordCompactFacts(toolName, response)

        agentState = stateUpdater.updateAndStoreExecution(
            toolName = toolName,
            arguments = arguments,
            response = response,
            currentState = agentState
        )
    }

    /**
     * Extrae solo los hechos clave de una respuesta de herramienta y los guarda en memoria.
     * Mantiene el contexto del LLM pequeño y enfocado en datos relevantes para decisiones.
     */
    private fun recordCompactFacts(toolName: String, response: JSONObject) {
        if (!response.optBoolean("success", false)) return
        val data = response.opt("data") ?: return

        when (toolName) {
            "createCustomer", "searchCustomer" -> {
                val customer = when (data) {
                    is JSONArray -> data.optJSONObject(0)
                    is JSONObject -> data
                    else -> null
                } ?: return
                val id = customer.optString("customerId", "")
                val name = "${customer.optString("firstName", "")} ${customer.optString("lastName", "")}".trim()
                if (id.isNotBlank()) memory.recordFact("lastCustomerId", id)
                if (name.isNotBlank()) memory.recordFact("lastCustomerName", name)
            }

            "createProduct", "searchProduct", "updateProduct" -> {
                val product = when (data) {
                    is JSONArray -> data.optJSONObject(0)
                    is JSONObject -> data
                    else -> null
                } ?: return
                val id = product.optString("productId", "")
                val name = product.optString("name", "")
                if (id.isNotBlank()) memory.recordFact("lastProductId", id)
                if (name.isNotBlank()) memory.recordFact("lastProductName", name)
            }

            "createPurchase" -> {
                val purchaseData = (data as? JSONObject)?.optJSONObject("purchase")
                val id = purchaseData?.optString("purchaseId", "") ?: ""
                if (id.isNotBlank()) memory.recordFact("lastPurchaseId", id)
            }

            "createSale" -> {
                val saleData = (data as? JSONObject)?.optJSONObject("sale")
                val id = saleData?.optString("saleId", "") ?: ""
                if (id.isNotBlank()) memory.recordFact("lastSaleId", id)
            }

            "getInventory" -> {
                val inv = data as? JSONObject ?: return
                if (!inv.has("stock")) return
                val stock = inv.optInt("stock")
                val productName = agentState.lastProductName
                    ?: inv.optString("productId", "producto")
                memory.recordFact("lastInventoryStock", "$productName: $stock unidades")
            }

            "deleteCustomer" -> {
                memory.recordFact("lastCustomerId", "")
                memory.recordFact("lastCustomerName", "")
            }

            "deleteProduct" -> {
                memory.recordFact("lastProductId", "")
                memory.recordFact("lastProductName", "")
            }
        }
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

    private fun buildToolErrorFinalMessage(toolResponse: JSONObject): String {
        val mappedError = AgentErrorMapper.fromHttpResponse(toolResponse)
        return mappedError.userMessage
    }

    private fun renderFinalResult(finalMessage: String, state: AgentState): String {
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
            "listProducts" -> buildListResultMessage(response, "productos", "name")
            "listCustomers" -> buildListResultMessage(response, "clientes", "firstName", "lastName")
            "listLowStock" -> buildLowStockMessage(response)
            "deleteCustomer" -> "Listo. El cliente fue eliminado correctamente."
            "deleteProduct" -> "Listo. El producto fue eliminado correctamente."
            else -> backendMessage.ifBlank { "Listo. La operación fue completada correctamente." }
        }

        return renderFinalResult(finalMessage = baseMessage, state = state)
    }

    private fun buildLoopContext(loopContext: String, state: AgentState): String {
        return buildString {
            appendLine("State:")
            appendLine("lastCustomerId=${state.lastCustomerId ?: "none"}")
            appendLine("lastCustomerName=${state.lastCustomerName ?: "none"}")
            appendLine("lastProductId=${state.lastProductId ?: "none"}")
            appendLine("lastProductName=${state.lastProductName ?: "none"}")
            appendLine("lastProductPurchasePrice=${state.lastProductPurchasePrice ?: "none"}")
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

    // ─────────────────────────────────────────────────────────────────────────
    // Funciones auxiliares de texto
    // ─────────────────────────────────────────────────────────────────────────

    private fun firstDataObject(response: JSONObject): JSONObject? {
        val data = response.opt("data")
        return when (data) {
            is JSONArray -> data.optJSONObject(0)
            is JSONObject -> data
            else -> null
        }
    }

    private fun findMatchingProduct(response: JSONObject, productName: String): JSONObject? {
        val data = response.opt("data")
        val target = normalizeText(productName)

        if (data is JSONObject) {
            return if (normalizeText(data.optString("name", "")) == target) data else null
        }

        if (data is JSONArray) {
            for (index in 0 until data.length()) {
                val item = data.optJSONObject(index) ?: continue
                if (normalizeText(item.optString("name", "")) == target) return item
            }
        }

        return null
    }

    private fun findMatchingCustomer(response: JSONObject, fullName: String): JSONObject? {
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

        if (data is JSONObject) return if (matches(data)) data else null

        if (data is JSONArray) {
            for (index in 0 until data.length()) {
                val item = data.optJSONObject(index) ?: continue
                if (matches(item)) return item
            }
        }

        return null
    }

    private fun splitFullName(fullName: String): Pair<String, String> {
        val parts = fullName.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        if (parts.isEmpty()) return Pair("", "")
        if (parts.size == 1) return Pair(parts[0], "")

        val lastName = parts.last()
        val firstName = parts.dropLast(1).joinToString(" ")

        return Pair(firstName, lastName)
    }

    private fun normalizeText(value: String): String {
        val noAccents = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}"), "")
        return noAccents.trim().lowercase().replace(Regex("\\s+"), " ")
    }

    private fun normalizeSpanishNumbers(text: String): String {
        return text
            .replace(Regex("(?i)\\bun[ao]\\b"), "1")
            .replace(Regex("(?i)\\bdos\\b"), "2")
            .replace(Regex("(?i)\\btres\\b"), "3")
            .replace(Regex("(?i)\\bcuatro\\b"), "4")
            .replace(Regex("(?i)\\bcinco\\b"), "5")
            .replace(Regex("(?i)\\bseis\\b"), "6")
            .replace(Regex("(?i)\\bsiete\\b"), "7")
            .replace(Regex("(?i)\\bocho\\b"), "8")
            .replace(Regex("(?i)\\bnueve\\b"), "9")
            .replace(Regex("(?i)\\bdiez\\b"), "10")
            .replace(Regex("(?i)\\bonce\\b"), "11")
            .replace(Regex("(?i)\\bdoce\\b"), "12")
            .replace(Regex("(?i)\\bquince\\b"), "15")
            .replace(Regex("(?i)\\bveinte\\b"), "20")
            .replace(Regex("(?i)\\btreinta\\b"), "30")
            .replace(Regex("(?i)\\bcuarenta\\b"), "40")
            .replace(Regex("(?i)\\bcincuenta\\b"), "50")
            .replace(Regex("(?i)\\bsesenta\\b"), "60")
            .replace(Regex("(?i)\\bsetenta\\b"), "70")
            .replace(Regex("(?i)\\bochenta\\b"), "80")
            .replace(Regex("(?i)\\bnoventa\\b"), "90")
            .replace(Regex("(?i)\\bcien(?:to)?\\b"), "100")
            .replace(Regex("(?i)\\bdoscien(?:tos|tas)?\\b"), "200")
            .replace(Regex("(?i)\\btrescien(?:tos|tas)?\\b"), "300")
            .replace(Regex("(?i)\\bcuatrocien(?:tos|tas)?\\b"), "400")
            .replace(Regex("(?i)\\bquinien(?:tos|tas)?\\b"), "500")
            .replace(Regex("(?i)\\bmil\\b"), "1000")
    }

    private fun cleanProductName(value: String): String {
        return value
            .trim()
            .replace(Regex("(?i)^de\\s+"), "")
            .replace(Regex("[.,;:]$"), "")
            .trim()
    }

    private fun cleanCustomerName(value: String): String {
        return value.trim().replace(Regex("[.,;:]$"), "").trim()
    }

    private fun buildListResultMessage(
        response: JSONObject,
        entityLabel: String,
        vararg nameFields: String
    ): String {
        val data = response.opt("data")
        val array = when (data) {
            is JSONArray -> data
            else -> null
        }

        if (array == null || array.length() == 0) {
            return "No hay $entityLabel registrados en el ERP."
        }

        return buildString {
            appendLine("Hay ${array.length()} $entityLabel registrados:")
            for (i in 0 until minOf(array.length(), 10)) {
                val item = array.optJSONObject(i) ?: continue
                val name = nameFields
                    .mapNotNull { field -> item.optString(field, "").ifBlank { null } }
                    .joinToString(" ")
                    .trim()
                if (name.isNotBlank()) appendLine("- $name")
            }
            if (array.length() > 10) appendLine("... y ${array.length() - 10} más.")
        }.trim()
    }

    private fun buildLowStockMessage(response: JSONObject): String {
        val data = response.opt("data")
        val array = when (data) {
            is JSONArray -> data
            else -> null
        }

        if (array == null || array.length() == 0) {
            return "No hay productos con stock bajo en este momento."
        }

        return buildString {
            appendLine("Hay ${array.length()} producto(s) con stock bajo:")
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val name = item.optString("name", item.optString("productId", "Producto $i"))
                val stock = item.optInt("stock", 0)
                val minStock = item.optInt("minStock", 0)
                appendLine("- $name: $stock unidades (mínimo: $minStock)")
            }
        }.trim()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ejemplos de depuración
    // ─────────────────────────────────────────────────────────────────────────

    private fun exampleCustomer(): String = """
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

    private fun exampleProduct(): String = """
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

    private fun examplePurchase(): String = """
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

    private fun exampleSale(): String = """
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

    // ─────────────────────────────────────────────────────────────────────────
    // Clases de datos privadas
    // ─────────────────────────────────────────────────────────────────────────

    private data class CreateCustomerAndSaleCommand(
        val customerFullName: String,
        val quantity: Int,
        val productName: String
    )

    private data class SaleCommand(
        val productName: String,
        val quantity: Int
    )

    private data class PurchaseCommand(
        val productName: String,
        val quantity: Int,
        val unitCost: Double?
    )

    private data class CustomerRef(
        val customerId: String,
        val customerName: String
    )

    private data class ProductRef(
        val productId: String,
        val productName: String
    )

    // Herramientas que completan la tarea en un solo paso.
    // Al tener éxito, el bucle retorna sin darle otro turno al LLM.
    private fun isTerminalTool(toolName: String): Boolean = toolName in setOf(
        "createProduct", "createCustomer",
        "deleteProduct", "deleteCustomer",
        "updateProduct",
        "createPurchase", "createSale",
        "listProducts", "listCustomers", "listLowStock",
        "getInventory"
    )

    // Enriquece una llamada a herramienta con valores conocidos del estado para evitar preguntas innecesarias.
    // Por ahora: inyecta unitCost desde lastProductPurchasePrice cuando createPurchase no lo incluye.
    private fun enrichToolCallFromState(
        action: AgentAction.ToolCall,
        state: AgentState
    ): AgentAction.ToolCall {
        if (action.tool != "createPurchase") return action
        if (action.arguments.has("unitCost")) return action
        val knownPrice = state.lastProductPurchasePrice ?: return action
        val enriched = JSONObject(action.arguments.toString()).put("unitCost", knownPrice)
        return action.copy(arguments = enriched)
    }
}
