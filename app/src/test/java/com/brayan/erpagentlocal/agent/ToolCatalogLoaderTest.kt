package com.brayan.erpagentlocal.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ToolCatalogLoaderTest {

    @Test
    fun generatedCatalogContainsAllOpenApiOperations() {
        val catalog = loadGeneratedCatalog()

        assertEquals(19, catalog.count())
        assertEquals(
            listOf(
                "checkHealth",
                "createCustomer",
                "listCustomers",
                "searchCustomer",
                "updateCustomer",
                "deleteCustomer",
                "createProduct",
                "listProducts",
                "searchProduct",
                "updateProduct",
                "deleteProduct",
                "getInventory",
                "listLowStock",
                "createInventoryAdjustment",
                "createPurchase",
                "listPurchases",
                "createSale",
                "listSales",
                "listSalesByCustomer"
            ),
            catalog.getAll().map { it.name }
        )
    }

    @Test
    fun generatedCatalogPreservesRequiredArguments() {
        val catalog = loadGeneratedCatalog()

        assertEquals(
            listOf("firstName", "lastName"),
            requireTool(catalog, "createCustomer").requiredArguments
        )
        assertEquals(
            listOf("productId", "quantity", "type", "reason"),
            requireTool(catalog, "createInventoryAdjustment").requiredArguments
        )
        assertEquals(
            listOf("customerId", "items"),
            requireTool(catalog, "createSale").requiredArguments
        )
        assertEquals(
            listOf("customerId"),
            requireTool(catalog, "listSalesByCustomer").requiredArguments
        )
    }

    @Test
    fun generatedCatalogPreservesMethodsAndPaths() {
        val catalog = loadGeneratedCatalog()

        requireTool(catalog, "searchCustomer").also { tool ->
            assertEquals("GET", tool.method)
            assertEquals("/customers/search?name={name}", tool.path)
        }

        requireTool(catalog, "updateProduct").also { tool ->
            assertEquals("PATCH", tool.method)
            assertEquals("/products/{productId}", tool.path)
            assertEquals(
                listOf("name", "description", "unit", "salePrice", "purchasePrice"),
                tool.optionalArguments
            )
        }
    }

    @Test
    fun generatedJsonIsUpToDateWithOpenApi() {
        val root = projectRoot()
        val process = ProcessBuilder(
            "python",
            "scripts/openapi_to_tools.py",
            "--check"
        )
            .directory(root)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        assertEquals(output, 0, exitCode)
    }

    private fun loadGeneratedCatalog(): ToolCatalog {
        val generated = File(projectRoot(), "app/src/main/assets/tools.generated.json")
        assertTrue("Missing generated tools file: ${generated.absolutePath}", generated.exists())
        return ToolCatalogLoader.parse(
            jsonText = generated.readText(),
            source = "test"
        )
    }

    private fun requireTool(catalog: ToolCatalog, name: String): ToolDefinition {
        val tool = catalog.find(name)
        assertNotNull("Missing tool: $name", tool)
        return tool!!
    }

    private fun projectRoot(): File {
        val candidates = listOf(
            File("."),
            File("..")
        )

        return candidates.first { candidate ->
            File(candidate, "scripts/openapi_to_tools.py").exists()
        }.canonicalFile
    }
}
