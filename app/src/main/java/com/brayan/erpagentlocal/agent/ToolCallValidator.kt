package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

class ToolCallValidator(
    private val maxRepeatedSuccessfulCalls: Int = 2
) {

    fun validate(
        action: AgentAction.ToolCall,
        state: AgentState
    ): ValidationResult {
        val toolDefinition = ToolRegistry.find(action.tool)
            ?: return ValidationResult.invalid(
                "La tool '${action.tool}' no está registrada en el catálogo."
            )

        val requiredValidation = validateRequiredArguments(
            toolDefinition = toolDefinition,
            arguments = action.arguments
        )

        if (!requiredValidation.isValid) {
            return requiredValidation
        }

        val typeValidation = validateArgumentTypes(
            toolName = action.tool,
            arguments = action.arguments
        )

        if (!typeValidation.isValid) {
            return typeValidation
        }

        val idValidation = validateKnownIds(
            toolName = action.tool,
            arguments = action.arguments,
            state = state
        )

        if (!idValidation.isValid) {
            return idValidation
        }

        val repetitionValidation = validateRepeatedSuccessfulCalls(
            action = action,
            state = state
        )

        if (!repetitionValidation.isValid) {
            return repetitionValidation
        }

        return ValidationResult.valid()
    }

    private fun validateRequiredArguments(
        toolDefinition: ToolDefinition,
        arguments: JSONObject
    ): ValidationResult {
        val missingFields = toolDefinition.requiredArguments.filter { field ->
            !arguments.has(field) || isBlankJsonValue(arguments.opt(field))
        }

        if (missingFields.isEmpty()) {
            return ValidationResult.valid()
        }

        val message = when {
            missingFields.contains("customerId") ->
                "Falta el cliente para ejecutar '${toolDefinition.name}'. Primero debo buscar o crear el cliente."

            missingFields.contains("productId") ->
                "Falta el producto para ejecutar '${toolDefinition.name}'. Primero debo buscar o crear el producto."

            missingFields.contains("items") ->
                "Faltan los productos de la venta. Indica qué producto y qué cantidad deseas vender."

            missingFields.contains("quantity") ->
                "Falta la cantidad. Indica cuántas unidades deseas registrar."

            missingFields.contains("unitCost") ->
                "Falta el costo unitario de la compra."

            missingFields.contains("salePrice") ->
                "Falta el precio de venta del producto."

            missingFields.contains("purchasePrice") ->
                "Falta el precio de compra del producto."

            missingFields.contains("firstName") || missingFields.contains("lastName") ->
                "Falta nombre o apellido del cliente."

            else ->
                "Faltan campos requeridos para '${toolDefinition.name}': ${missingFields.joinToString(", ")}"
        }

        return ValidationResult.askUser(message)
    }

    private fun validateArgumentTypes(
        toolName: String,
        arguments: JSONObject
    ): ValidationResult {
        return when (toolName) {
            "createCustomer" -> validateCreateCustomer(arguments)
            "searchCustomer" -> validateSearchByName(arguments, "cliente")
            "createProduct" -> validateCreateProduct(arguments)
            "searchProduct" -> validateSearchByName(arguments, "producto")
            "updateProduct" -> validateUpdateProduct(arguments)
            "getInventory" -> validateGetInventory(arguments)
            "createPurchase" -> validateCreatePurchase(arguments)
            "createSale" -> validateCreateSale(arguments)
            else -> ValidationResult.valid()
        }
    }

    private fun validateCreateCustomer(arguments: JSONObject): ValidationResult {
        if (arguments.optString("firstName").trim().isBlank()) {
            return ValidationResult.askUser("¿Cuál es el nombre del cliente?")
        }

        if (arguments.optString("lastName").trim().isBlank()) {
            return ValidationResult.askUser("¿Cuál es el apellido del cliente?")
        }

        return ValidationResult.valid()
    }

    private fun validateSearchByName(
        arguments: JSONObject,
        entityName: String
    ): ValidationResult {
        val name = arguments.optString("name").trim()

        if (name.isBlank()) {
            return ValidationResult.askUser("¿Qué $entityName deseas buscar?")
        }

        return ValidationResult.valid()
    }

    private fun validateCreateProduct(arguments: JSONObject): ValidationResult {
        if (arguments.optString("name").trim().isBlank()) {
            return ValidationResult.askUser("¿Cuál es el nombre del producto?")
        }

        if (!isNumber(arguments, "salePrice")) {
            return ValidationResult.askUser("¿Cuál es el precio de venta del producto?")
        }

        if (!isNumber(arguments, "purchasePrice")) {
            return ValidationResult.askUser("¿Cuál es el precio de compra del producto?")
        }

        val salePrice = arguments.optDouble("salePrice", -1.0)
        val purchasePrice = arguments.optDouble("purchasePrice", -1.0)

        if (salePrice < 0) {
            return ValidationResult.invalid("El precio de venta no puede ser negativo.")
        }

        if (purchasePrice < 0) {
            return ValidationResult.invalid("El precio de compra no puede ser negativo.")
        }

        return ValidationResult.valid()
    }

    private fun validateUpdateProduct(arguments: JSONObject): ValidationResult {
        val productId = arguments.optString("productId").trim()

        if (productId.isBlank()) {
            return ValidationResult.askUser("Falta el producto que deseas actualizar.")
        }

        val hasAnyUpdateField =
            arguments.has("name") ||
                    arguments.has("description") ||
                    arguments.has("unit") ||
                    arguments.has("salePrice") ||
                    arguments.has("purchasePrice")

        if (!hasAnyUpdateField) {
            return ValidationResult.askUser("¿Qué dato del producto deseas actualizar?")
        }

        if (arguments.has("salePrice") && !isNumber(arguments, "salePrice")) {
            return ValidationResult.askUser("El precio de venta debe ser un número válido.")
        }

        if (arguments.has("purchasePrice") && !isNumber(arguments, "purchasePrice")) {
            return ValidationResult.askUser("El precio de compra debe ser un número válido.")
        }

        return ValidationResult.valid()
    }

    private fun validateGetInventory(arguments: JSONObject): ValidationResult {
        val productId = arguments.optString("productId").trim()

        if (productId.isBlank()) {
            return ValidationResult.askUser("¿De qué producto deseas consultar el inventario?")
        }

        return ValidationResult.valid()
    }

    private fun validateCreatePurchase(arguments: JSONObject): ValidationResult {
        val productId = arguments.optString("productId").trim()

        if (productId.isBlank()) {
            return ValidationResult.askUser(
                "Falta el producto para registrar la compra. Primero debo buscar o crear el producto."
            )
        }

        if (!isPositiveInt(arguments, "quantity")) {
            return ValidationResult.askUser("¿Cuántas unidades deseas comprar?")
        }

        if (!isNumber(arguments, "unitCost")) {
            return ValidationResult.askUser("¿Cuál es el costo unitario de la compra?")
        }

        val unitCost = arguments.optDouble("unitCost", -1.0)

        if (unitCost < 0) {
            return ValidationResult.invalid("El costo unitario no puede ser negativo.")
        }

        return ValidationResult.valid()
    }

    private fun validateCreateSale(arguments: JSONObject): ValidationResult {
        val customerId = arguments.optString("customerId").trim()

        if (customerId.isBlank()) {
            return ValidationResult.askUser(
                "Falta el cliente para registrar la venta. ¿A qué cliente deseas vender?"
            )
        }

        val items = arguments.optJSONArray("items")

        if (items == null || items.length() == 0) {
            return ValidationResult.askUser(
                "Faltan los productos de la venta. Indica producto y cantidad."
            )
        }

        for (index in 0 until items.length()) {
            val item = items.optJSONObject(index)
                ?: return ValidationResult.invalid("Cada item de venta debe ser un objeto JSON.")

            val productId = item.optString("productId").trim()

            if (productId.isBlank()) {
                return ValidationResult.askUser(
                    "Falta el producto en la venta. Primero debo buscar o crear el producto."
                )
            }

            if (!isPositiveInt(item, "quantity")) {
                return ValidationResult.askUser(
                    "Falta una cantidad válida para el producto de la venta."
                )
            }
        }

        return ValidationResult.valid()
    }

    private fun validateKnownIds(
        toolName: String,
        arguments: JSONObject,
        state: AgentState
    ): ValidationResult {
        return when (toolName) {
            "createPurchase" -> {
                val productId = arguments.optString("productId").trim()

                if (!isKnownProductId(productId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentó usar un productId que no está en el estado ni en resultados previos: $productId"
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            "getInventory" -> {
                val productId = arguments.optString("productId").trim()

                if (!isKnownProductId(productId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentó consultar inventario con un productId desconocido: $productId"
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            "updateProduct" -> {
                val productId = arguments.optString("productId").trim()

                if (!isKnownProductId(productId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentó actualizar un producto con productId desconocido: $productId"
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            "createSale" -> {
                val customerId = arguments.optString("customerId").trim()

                if (!isKnownCustomerId(customerId, state)) {
                    return ValidationResult.invalid(
                        "El modelo intentó usar un customerId que no está en el estado ni en resultados previos: $customerId"
                    )
                }

                val items = arguments.optJSONArray("items") ?: JSONArray()

                for (index in 0 until items.length()) {
                    val item = items.optJSONObject(index) ?: continue
                    val productId = item.optString("productId").trim()

                    if (!isKnownProductId(productId, state)) {
                        return ValidationResult.invalid(
                            "El modelo intentó vender un producto con productId desconocido: $productId"
                        )
                    }
                }

                ValidationResult.valid()
            }

            "deleteCustomer" -> {
                val customerId = arguments.optString("customerId").trim()
                if (!isKnownCustomerId(customerId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentó eliminar un cliente con customerId desconocido: $customerId. Primero debo buscar al cliente."
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            "deleteProduct" -> {
                val productId = arguments.optString("productId").trim()
                if (!isKnownProductId(productId, state)) {
                    ValidationResult.invalid(
                        "El modelo intentó eliminar un producto con productId desconocido: $productId. Primero debo buscar el producto."
                    )
                } else {
                    ValidationResult.valid()
                }
            }

            else -> ValidationResult.valid()
        }
    }

    private fun validateRepeatedSuccessfulCalls(
        action: AgentAction.ToolCall,
        state: AgentState
    ): ValidationResult {
        val currentArguments = action.arguments.toString()

        val repeatedSuccessfulCalls = state.executedTools.count { executedTool ->
            executedTool.toolName == action.tool &&
                    executedTool.success &&
                    normalizeJsonText(executedTool.argumentsJson) == normalizeJsonText(currentArguments)
        }

        if (repeatedSuccessfulCalls >= maxRepeatedSuccessfulCalls) {
            return ValidationResult.alreadyDone(
                "La acción '${action.tool}' ya fue ejecutada correctamente. La tarea está completada."
            )
        }

        return ValidationResult.valid()
    }

    private fun isKnownCustomerId(
        customerId: String,
        state: AgentState
    ): Boolean {
        if (customerId.isBlank()) {
            return false
        }

        if (state.lastCustomerId == customerId) {
            return true
        }

        return state.executedTools.any { executedTool ->
            executedTool.resultJson?.contains(customerId) == true
        }
    }

    private fun isKnownProductId(
        productId: String,
        state: AgentState
    ): Boolean {
        if (productId.isBlank()) {
            return false
        }

        if (state.lastProductId == productId) {
            return true
        }

        return state.executedTools.any { executedTool ->
            executedTool.resultJson?.contains(productId) == true
        }
    }

    private fun isBlankJsonValue(value: Any?): Boolean {
        return when (value) {
            null -> true
            JSONObject.NULL -> true
            is String -> value.trim().isBlank()
            is JSONArray -> value.length() == 0
            else -> false
        }
    }

    private fun isNumber(
        jsonObject: JSONObject,
        field: String
    ): Boolean {
        if (!jsonObject.has(field)) {
            return false
        }

        return try {
            jsonObject.getDouble(field)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun isPositiveInt(
        jsonObject: JSONObject,
        field: String
    ): Boolean {
        if (!jsonObject.has(field)) {
            return false
        }

        return try {
            jsonObject.getInt(field) > 0
        } catch (_: Exception) {
            false
        }
    }

    private fun normalizeJsonText(value: String): String {
        return value
            .replace("\\s".toRegex(), "")
            .trim()
    }
}