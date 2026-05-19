package com.brayan.erpagentlocal.agent

import com.brayan.erpagentlocal.util.JsonUtils
import org.json.JSONObject

class AgentActionParser {

    fun parse(rawText: String): AgentAction {
        val cleanedText = JsonUtils.extractJsonObject(rawText)

        return try {
            val json = JSONObject(cleanedText.trim())
            val type = json.optString("type").trim()
            val tool = json.optString("tool").trim()

            when {
                type == "tool_call" -> parseToolCall(json)

                type == "ask_user" -> parseAskUser(json)

                type == "final" -> parseFinal(json)

                tool.isNotBlank() && ToolRegistry.exists(tool) -> {
                    parseToolCall(json)
                }

                type.isNotBlank() && ToolRegistry.exists(type) -> {
                    val normalized = JSONObject()
                        .put("type", "tool_call")
                        .put("tool", type)
                        .put("arguments", json.optJSONObject("arguments") ?: JSONObject())

                    parseToolCall(normalized)
                }

                else -> {
                    AgentAction.Invalid(
                        "No pude interpretar la acción del modelo. Intenta escribir la instrucción de forma más específica."
                    )
                }
            }
        } catch (_: Exception) {
            AgentAction.Invalid(
                "No pude interpretar la acción del modelo. Intenta escribir la instrucción de forma más específica."
            )
        }
    }

    private fun parseToolCall(json: JSONObject): AgentAction {
        val tool = json.optString("tool").trim()
        val arguments = json.optJSONObject("arguments") ?: JSONObject()

        if (tool.isBlank()) {
            return AgentAction.Invalid("El campo 'tool' es obligatorio.")
        }

        return AgentAction.ToolCall(
            tool = tool,
            arguments = arguments
        )
    }

    private fun parseAskUser(json: JSONObject): AgentAction {
        val message = json.optString("message").trim()

        if (message.isBlank()) {
            return AgentAction.Invalid("El campo 'message' es obligatorio para ask_user.")
        }

        return AgentAction.AskUser(message)
    }

    private fun parseFinal(json: JSONObject): AgentAction {
        val message = json.optString("message").trim()

        if (message.isBlank()) {
            return AgentAction.Invalid("El campo 'message' es obligatorio para final.")
        }

        return AgentAction.Final(message)
    }
}