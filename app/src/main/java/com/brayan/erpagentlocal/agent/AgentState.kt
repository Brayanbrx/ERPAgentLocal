package com.brayan.erpagentlocal.agent

data class AgentState(
    val lastCustomerId: String? = null,
    val lastCustomerName: String? = null,
    val lastProductId: String? = null,
    val lastProductName: String? = null,
    val lastSaleId: String? = null,
    val lastPurchaseId: String? = null,
    val lastInventoryStock: Int? = null,
    val pendingQuestion: String? = null,
    val executedTools: List<ExecutedTool> = emptyList()
) {

    fun withCustomer(
        customerId: String?,
        customerName: String?
    ): AgentState {
        return copy(
            lastCustomerId = customerId ?: lastCustomerId,
            lastCustomerName = customerName ?: lastCustomerName
        )
    }

    fun withProduct(
        productId: String?,
        productName: String?
    ): AgentState {
        return copy(
            lastProductId = productId ?: lastProductId,
            lastProductName = productName ?: lastProductName
        )
    }

    fun withSale(
        saleId: String?
    ): AgentState {
        return copy(
            lastSaleId = saleId ?: lastSaleId
        )
    }

    fun withPurchase(
        purchaseId: String?
    ): AgentState {
        return copy(
            lastPurchaseId = purchaseId ?: lastPurchaseId
        )
    }

    fun withInventoryStock(
        stock: Int?
    ): AgentState {
        return copy(
            lastInventoryStock = stock ?: lastInventoryStock
        )
    }

    fun withPendingQuestion(
        question: String?
    ): AgentState {
        return copy(
            pendingQuestion = question
        )
    }

    fun clearPendingQuestion(): AgentState {
        return copy(
            pendingQuestion = null
        )
    }

    fun addExecutedTool(
        executedTool: ExecutedTool
    ): AgentState {
        return copy(
            executedTools = executedTools + executedTool
        )
    }

    fun keepLastExecutedTools(
        maxItems: Int
    ): AgentState {
        if (maxItems <= 0) {
            return copy(executedTools = emptyList())
        }

        return copy(
            executedTools = executedTools.takeLast(maxItems)
        )
    }

    fun hasCustomer(): Boolean {
        return !lastCustomerId.isNullOrBlank()
    }

    fun hasProduct(): Boolean {
        return !lastProductId.isNullOrBlank()
    }

    fun hasSale(): Boolean {
        return !lastSaleId.isNullOrBlank()
    }

    fun hasPurchase(): Boolean {
        return !lastPurchaseId.isNullOrBlank()
    }

    fun hasPendingQuestion(): Boolean {
        return !pendingQuestion.isNullOrBlank()
    }

    fun toPromptBlock(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"lastCustomerId\": ${jsonStringOrNull(lastCustomerId)},")
            appendLine("  \"lastCustomerName\": ${jsonStringOrNull(lastCustomerName)},")
            appendLine("  \"lastProductId\": ${jsonStringOrNull(lastProductId)},")
            appendLine("  \"lastProductName\": ${jsonStringOrNull(lastProductName)},")
            appendLine("  \"lastSaleId\": ${jsonStringOrNull(lastSaleId)},")
            appendLine("  \"lastPurchaseId\": ${jsonStringOrNull(lastPurchaseId)},")
            appendLine("  \"lastInventoryStock\": ${lastInventoryStock?.toString() ?: "null"},")
            appendLine("  \"pendingQuestion\": ${jsonStringOrNull(pendingQuestion)},")
            appendLine("  \"executedTools\": [")

            executedTools.forEachIndexed { index, tool ->
                append(tool.toPromptBlock())
                if (index < executedTools.lastIndex) {
                    appendLine(",")
                } else {
                    appendLine()
                }
            }

            appendLine("  ]")
            appendLine("}")
        }
    }

    fun toDebugText(): String {
        return buildString {
            appendLine("AGENT STATE")
            appendLine()
            appendLine("lastCustomerId: ${lastCustomerId ?: "none"}")
            appendLine("lastCustomerName: ${lastCustomerName ?: "none"}")
            appendLine("lastProductId: ${lastProductId ?: "none"}")
            appendLine("lastProductName: ${lastProductName ?: "none"}")
            appendLine("lastSaleId: ${lastSaleId ?: "none"}")
            appendLine("lastPurchaseId: ${lastPurchaseId ?: "none"}")
            appendLine("lastInventoryStock: ${lastInventoryStock ?: "none"}")
            appendLine("pendingQuestion: ${pendingQuestion ?: "none"}")
            appendLine()
            appendLine("Executed tools: ${executedTools.size}")

            executedTools.forEachIndexed { index, executedTool ->
                appendLine()
                appendLine("Executed tool ${index + 1}:")
                appendLine(executedTool.toDebugText())
            }
        }
    }

    private fun jsonStringOrNull(value: String?): String {
        if (value.isNullOrBlank()) {
            return "null"
        }

        return "\"${escape(value)}\""
    }

    private fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }

    companion object {
        fun empty(): AgentState {
            return AgentState()
        }
    }
}