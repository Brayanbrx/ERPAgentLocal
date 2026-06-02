package com.brayan.erpagentlocal.agent

import android.content.Context
import org.json.JSONArray

object ToolCatalogLoader {

    private const val GENERATED_TOOLS_ASSET = "tools.generated.json"
    private const val DEFAULT_TOOLS_ASSET = "tools.json"

    fun loadFromAssets(
        context: Context,
        assetFileName: String? = null
    ): ToolCatalog {
        val candidates = if (assetFileName != null) {
            listOf(assetFileName)
        } else {
            listOf(GENERATED_TOOLS_ASSET, DEFAULT_TOOLS_ASSET)
        }

        val errors = mutableListOf<String>()

        candidates.forEach { candidate ->
            try {
                val jsonText = context.assets
                    .open(candidate)
                    .bufferedReader()
                    .use { reader ->
                        reader.readText()
                    }

                return parse(
                    jsonText = jsonText,
                    source = candidate,
                    loadError = errors.takeIf { it.isNotEmpty() }?.joinToString(" | ")
                )
            } catch (exception: Exception) {
                errors.add("$candidate: ${exception.message ?: exception::class.java.simpleName}")
            }
        }

        return ToolCatalog.default(
            loadError = errors.joinToString(" | ").ifBlank { "No asset catalog available." }
        )
    }

    fun parse(
        jsonText: String,
        source: String = "inline-json",
        loadError: String? = null
    ): ToolCatalog {
        val jsonArray = JSONArray(jsonText)
        val tools = mutableListOf<ToolDefinition>()

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index)

            val requiredArguments = item
                .optJSONArray("requiredArguments")
                .toStringList()

            val optionalArguments = item
                .optJSONArray("optionalArguments")
                .toStringList()

            tools.add(
                ToolDefinition(
                    name = item.getString("name"),
                    operationId = item.optString("operationId", item.getString("name")),
                    method = item.getString("method"),
                    path = item.getString("path"),
                    description = item.optString("description", ""),
                    requiredArguments = requiredArguments,
                    optionalArguments = optionalArguments
                )
            )
        }

        return ToolCatalog(
            tools = tools,
            source = source,
            loadError = loadError
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) {
            return emptyList()
        }

        val result = mutableListOf<String>()

        for (index in 0 until length()) {
            result.add(optString(index))
        }

        return result.filter { value ->
            value.isNotBlank()
        }
    }
}
