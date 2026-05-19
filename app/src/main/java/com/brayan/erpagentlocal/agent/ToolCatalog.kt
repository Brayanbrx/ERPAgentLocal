package com.brayan.erpagentlocal.agent

class ToolCatalog(
    private val tools: List<ToolDefinition>
) {

    fun getAll(): List<ToolDefinition> {
        return tools.toList()
    }

    fun count(): Int {
        return tools.size
    }

    fun exists(toolName: String): Boolean {
        return tools.any { tool ->
            tool.name == toolName
        }
    }

    fun find(toolName: String): ToolDefinition? {
        return tools.firstOrNull { tool ->
            tool.name == toolName
        }
    }

    fun describeTools(): String {
        return buildString {
            appendLine("TOOLS FROM OPENAPI CONTRACT")
            appendLine()
            appendLine("Each tool name matches an OpenAPI operationId.")
            appendLine()

            tools.forEach { tool ->
                appendLine(tool.toDebugText())
                appendLine()
            }
        }
    }

    fun describeToolsForPrompt(): String {
        return buildString {
            tools.forEach { tool ->
                appendLine(tool.toPromptBlock())
                appendLine()
            }
        }
    }

    fun namesAsText(): String {
        return tools.joinToString(", ") { tool ->
            tool.name
        }
    }

    companion object {

        fun default(): ToolCatalog {
            return ToolCatalog(defaultTools())
        }

        fun defaultTools(): List<ToolDefinition> {
            return listOf(
                ToolDefinition(
                    name = "createCustomer",
                    operationId = "createCustomer",
                    method = "POST",
                    path = "/customers",
                    description = "Create a customer in the ERP.",
                    requiredArguments = listOf("firstName", "lastName"),
                    optionalArguments = listOf("phone", "email")
                ),
                ToolDefinition(
                    name = "searchCustomer",
                    operationId = "searchCustomer",
                    method = "GET",
                    path = "/customers/search?name={name}",
                    description = "Search customers by name or last name.",
                    requiredArguments = listOf("name")
                ),
                ToolDefinition(
                    name = "createProduct",
                    operationId = "createProduct",
                    method = "POST",
                    path = "/products",
                    description = "Create a product in the ERP.",
                    requiredArguments = listOf("name", "salePrice", "purchasePrice"),
                    optionalArguments = listOf("description", "unit")
                ),
                ToolDefinition(
                    name = "searchProduct",
                    operationId = "searchProduct",
                    method = "GET",
                    path = "/products/search?name={name}",
                    description = "Search products by name.",
                    requiredArguments = listOf("name")
                ),
                ToolDefinition(
                    name = "updateProduct",
                    operationId = "updateProduct",
                    method = "PATCH",
                    path = "/products/{productId}",
                    description = "Update product information.",
                    requiredArguments = listOf("productId"),
                    optionalArguments = listOf(
                        "name",
                        "description",
                        "unit",
                        "salePrice",
                        "purchasePrice"
                    )
                ),
                ToolDefinition(
                    name = "getInventory",
                    operationId = "getInventory",
                    method = "GET",
                    path = "/inventory/{productId}",
                    description = "Get inventory by product ID.",
                    requiredArguments = listOf("productId")
                ),
                ToolDefinition(
                    name = "createPurchase",
                    operationId = "createPurchase",
                    method = "POST",
                    path = "/purchases",
                    description = "Create a purchase and increase inventory.",
                    requiredArguments = listOf("productId", "quantity", "unitCost")
                ),
                ToolDefinition(
                    name = "createSale",
                    operationId = "createSale",
                    method = "POST",
                    path = "/sales",
                    description = "Create a sale and decrease inventory.",
                    requiredArguments = listOf("customerId", "items")
                )
            )
        }
    }
}