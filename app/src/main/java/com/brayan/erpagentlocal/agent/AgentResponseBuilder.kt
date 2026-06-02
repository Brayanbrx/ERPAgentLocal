package com.brayan.erpagentlocal.agent

import org.json.JSONArray
import org.json.JSONObject

class AgentResponseBuilder {

    fun build(report: AgentExecutionReport): String {
        if (!report.completed) {
            return buildPaused(report)
        }

        if (report.actions.isEmpty()) {
            return "No se ejecutó ninguna acción."
        }

        val successful = report.actions.filter { it.success }

        if (successful.size == 1) {
            return buildSingle(successful.first(), report.state)
        }

        return buildMultiple(successful, report.state)
    }

    private fun buildPaused(report: AgentExecutionReport): String {
        return buildString {
            appendLine(report.pausedMessage ?: "La operación se pausó.")
            appendSummary(report.state)
        }.trim()
    }

    private fun buildSingle(action: AgentExecutedAction, state: AgentState): String {
        val response = action.response
        val data = response.opt("data")

        val message = when (action.tool) {
            "createCustomer" -> {
                val customer = data as? JSONObject
                "Listo. Creé el cliente ${customerName(customer)}."
            }

            "createProduct" -> {
                val product = data as? JSONObject
                "Listo. Creé el producto ${clean(product?.optString("name", "") ?: "")}."
            }

            "searchProduct" -> buildSearchProduct(data)
            "searchCustomer" -> buildSearchCustomer(data)

            "createPurchase" -> buildPurchase(data)
            "createSale" -> buildSale(data)

            "listProducts" -> buildProductList(data)
            "listCustomers" -> buildCustomerList(data)
            "getInventory" -> buildInventory(data)
            "listLowStock" -> buildLowStock(data)
            "listPurchases" -> buildPurchaseList(data)
            "listSales", "listSalesByCustomer" -> buildSaleList(data)

            else -> response.optString("message", "Listo. Operación completada.")
        }

        return buildString {
            appendLine(message)
            if (shouldAppendSummary(action.tool)) {
                appendSummary(state)
            }
        }.trim()
    }

    private fun buildMultiple(actions: List<AgentExecutedAction>, state: AgentState): String {
        return buildString {
            appendLine("Listo. Completé la operación.")
            appendLine()

            val purchases = actions.filter { it.tool == "createPurchase" }
            val sales = actions.filter { it.tool == "createSale" }
            val customers = actions.filter { it.tool == "createCustomer" }
            val products = actions.filter { it.tool == "createProduct" }

            if (customers.isNotEmpty()) {
                appendLine("Clientes creados:")
                customers.forEach {
                    appendLine("- ${customerName(it.response.optJSONObject("data"))}")
                }
                appendLine()
            }

            if (products.isNotEmpty()) {
                appendLine("Productos creados:")
                products.forEach {
                    val product = it.response.optJSONObject("data")
                    appendLine("- ${clean(product?.optString("name", "") ?: "producto")}")
                }
                appendLine()
            }

            if (purchases.isNotEmpty()) {
                appendLine("Compras registradas:")
                purchases.forEach {
                    val data = it.response.optJSONObject("data")
                    val purchase = data?.optJSONObject("purchase")
                    val inventory = data?.optJSONObject("inventory")
                    appendLine("- Cantidad: ${purchase?.opt("quantity") ?: "-"} | Stock: ${inventory?.opt("stock") ?: "-"}")
                }
                appendLine()
            }

            if (sales.isNotEmpty()) {
                appendLine("Ventas registradas:")
                sales.forEach {
                    val data = it.response.optJSONObject("data")
                    val sale = data?.optJSONObject("sale")
                    appendLine("- Venta: ${sale?.optString("saleId", "-") ?: "-"} | Total: ${sale?.opt("total") ?: "-"}")
                }
                appendLine()
            }

            if (actions.any { shouldAppendSummary(it.tool) }) {
                appendSummary(state)
            }
        }.trim()
    }

    private fun buildSearchProduct(data: Any?): String {
        val products = data.asArray()

        if (products == null || products.length() == 0) {
            return "No encontré ese producto."
        }

        return buildString {
            appendLine("Encontré estos productos:")
            for (i in 0 until minOf(products.length(), 8)) {
                val product = products.optJSONObject(i) ?: continue
                append("- ${clean(product.optString("name", ""))}")
                if (product.has("purchasePrice")) append(" — compra: ${product.opt("purchasePrice")}")
                if (product.has("salePrice")) append(" — venta: ${product.opt("salePrice")}")
                appendLine()
            }
        }.trim()
    }

    private fun buildSearchCustomer(data: Any?): String {
        val customers = data.asArray()

        if (customers == null || customers.length() == 0) {
            return "No encontré ese cliente."
        }

        return buildString {
            appendLine("Encontré estos clientes:")
            for (i in 0 until minOf(customers.length(), 8)) {
                appendLine("- ${customerName(customers.optJSONObject(i))}")
            }
        }.trim()
    }

    private fun buildPurchase(data: Any?): String {
        val obj = data as? JSONObject
        val purchase = obj?.optJSONObject("purchase")
        val inventory = obj?.optJSONObject("inventory")

        return buildString {
            appendLine("Listo. Registré la compra correctamente.")
            appendLine()
            appendLine("Detalle:")
            appendLine("- Cantidad: ${purchase?.opt("quantity") ?: "-"}")
            appendLine("- Costo unitario: ${purchase?.opt("unitCost") ?: "-"}")
            appendLine("- Total: ${purchase?.opt("total") ?: "-"}")
            appendLine("- Stock actual: ${inventory?.opt("stock") ?: "-"}")
        }.trim()
    }

    private fun buildSale(data: Any?): String {
        val obj = data as? JSONObject
        val sale = obj?.optJSONObject("sale")

        return buildString {
            appendLine("Listo. Registré la venta correctamente.")
            appendLine("- Venta: ${sale?.optString("saleId", "-") ?: "-"}")
            appendLine("- Total: ${sale?.opt("total") ?: "-"}")
        }.trim()
    }

    private fun buildProductList(data: Any?): String {
        val products = data.asArray()

        if (products == null || products.length() == 0) {
            return "No hay productos registrados."
        }

        return buildString {
            appendLine("Productos registrados:")
            for (i in 0 until minOf(products.length(), 12)) {
                val product = products.optJSONObject(i) ?: continue
                appendLine("- ${clean(product.optString("name", ""))}")
            }
        }.trim()
    }

    private fun buildCustomerList(data: Any?): String {
        val customers = data.asArray()

        if (customers == null || customers.length() == 0) {
            return "No hay clientes registrados."
        }

        return buildString {
            appendLine("Clientes registrados:")
            for (i in 0 until minOf(customers.length(), 12)) {
                appendLine("- ${customerName(customers.optJSONObject(i))}")
            }

            if (customers.length() > 12) {
                appendLine("... y ${customers.length() - 12} clientes más.")
            }
        }.trim()
    }

    private fun buildInventory(data: Any?): String {
        val inventory = data as? JSONObject
            ?: return "No encontré inventario para ese producto."

        val productId = inventory.optString("productId", "")
        val stock = inventory.opt("stock")
        val minStock = inventory.opt("minStock")

        return buildString {
            appendLine("Inventario del producto:")
            if (productId.isNotBlank()) appendLine("- Producto ID: $productId")
            appendLine("- Stock actual: ${stock ?: 0}")
            if (minStock != null) appendLine("- Stock mínimo: $minStock")
        }.trim()
    }

    private fun buildLowStock(data: Any?): String {
        val items = data.asArray()

        if (items == null || items.length() == 0) {
            return "No hay productos con stock bajo."
        }

        return buildString {
            appendLine("Productos con stock bajo:")
            for (i in 0 until items.length()) {
                val item = items.optJSONObject(i) ?: continue
                appendLine("- ${item.optString("productId", "Producto")} | Stock: ${item.opt("stock") ?: "-"}")
            }
        }.trim()
    }

    private fun buildPurchaseList(data: Any?): String {
        val purchases = data.asArray()

        if (purchases == null || purchases.length() == 0) {
            return "No hay compras registradas."
        }

        return buildString {
            appendLine("Compras registradas:")
            for (i in 0 until minOf(purchases.length(), 10)) {
                val purchase = purchases.optJSONObject(i) ?: continue
                val purchaseId = purchase.optString("purchaseId", "")
                val quantity = purchase.opt("quantity")
                val total = purchase.opt("total")

                append("- ${purchaseId.ifBlank { "Compra" }}")
                if (quantity != null) append(" | Cantidad: $quantity")
                if (total != null) append(" | Total: $total")
                appendLine()
            }

            if (purchases.length() > 10) {
                appendLine("... y ${purchases.length() - 10} compras más.")
            }
        }.trim()
    }

    private fun buildSaleList(data: Any?): String {
        val sales = data.asArray()

        if (sales == null || sales.length() == 0) {
            return "No hay ventas registradas."
        }

        return buildString {
            appendLine("Ventas registradas:")
            for (i in 0 until minOf(sales.length(), 10)) {
                val sale = sales.optJSONObject(i) ?: continue
                val saleId = sale.optString("saleId", "")
                val customerName = clean(sale.optString("customerName", ""))
                val total = sale.opt("total")

                append("- ${saleId.ifBlank { "Venta" }}")
                if (customerName.isNotBlank()) append(" | Cliente: $customerName")
                if (total != null) append(" | Total: $total")
                appendLine()
            }

            if (sales.length() > 10) {
                appendLine("... y ${sales.length() - 10} ventas más.")
            }
        }.trim()
    }

    private fun StringBuilder.appendSummary(state: AgentState) {
        val hasSummary =
            !state.lastCustomerName.isNullOrBlank() ||
                    !state.lastProductName.isNullOrBlank() ||
                    !state.lastPurchaseId.isNullOrBlank() ||
                    !state.lastSaleId.isNullOrBlank() ||
                    state.lastInventoryStock != null

        if (!hasSummary) return

        appendLine()
        appendLine("Resumen:")

        if (!state.lastCustomerName.isNullOrBlank()) {
            appendLine("- Cliente: ${clean(state.lastCustomerName)}")
        }

        if (!state.lastProductName.isNullOrBlank()) {
            appendLine("- Producto: ${clean(state.lastProductName)}")
        }

        if (!state.lastPurchaseId.isNullOrBlank()) {
            appendLine("- Compra: ${state.lastPurchaseId}")
        }

        if (!state.lastSaleId.isNullOrBlank()) {
            appendLine("- Venta: ${state.lastSaleId}")
        }

        if (state.lastInventoryStock != null) {
            appendLine("- Stock actual: ${state.lastInventoryStock}")
        }
    }

    private fun customerName(customer: JSONObject?): String {
        if (customer == null) return "cliente"
        val firstName = clean(customer.optString("firstName", ""))
        val lastName = clean(customer.optString("lastName", ""))
        return "$firstName $lastName".replace(Regex("\\s+"), " ").trim()
    }

    private fun clean(value: String?): String {
        return value.orEmpty()
            .trim()
            .replace(Regex("(?i)^(el|la|los|las|al|a)\\s+"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun shouldAppendSummary(tool: String): Boolean {
        return tool !in setOf(
            "listProducts",
            "listCustomers",
            "getInventory",
            "listLowStock",
            "listPurchases",
            "listSales",
            "listSalesByCustomer"
        )
    }

    private fun Any?.asArray(): JSONArray? {
        return when (this) {
            is JSONArray -> this
            is JSONObject -> JSONArray().put(this)
            else -> null
        }
    }
}
