package com.brayan.erpagentlocal.agent

import android.content.Context
import org.json.JSONArray

object ToolCatalogLoader {

    private const val DEFAULT_TOOLS_ASSET = "tools.json"

    fun loadFromAssets(
        context: Context,
        assetFileName: String = DEFAULT_TOOLS_ASSET
    ): ToolCatalog {
        return try {
            val jsonText = context.assets
                .open(assetFileName)
                .bufferedReader()
                .use { reader ->
                    reader.readText()
                }

            parse(jsonText)
        } catch (_: Exception) {
            /*
             * Si falla la lectura del archivo, usamos el catálogo por defecto
             * para no romper la app durante la presentación.
             */
            ToolCatalog.default()
        }
    }

    fun parse(jsonText: String): ToolCatalog {
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

        return ToolCatalog(tools)
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