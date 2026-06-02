package com.brayan.erpagentlocal.agent

import com.brayan.erpagentlocal.util.JsonUtils
import org.json.JSONArray
import org.json.JSONObject

class AgentActionParser {

    fun parse(rawText: String): AgentAction {
        return try {
            val cleaned = JsonUtils.extractJsonObject(rawText).trim()

            if (cleaned.startsWith("[")) {
                return parseArrayAsQueue(JSONArray(cleaned))
            }

            val json = JSONObject(cleaned)
            val type = json.optString("type").trim()
            val tool = json.optString("tool").trim()

            when {
                type == "tool_queue" -> parseToolQueue(json)
                type == "tool_call" -> parseToolCall(json)
                type == "ask_user" -> parseAskUser(json)
                type == "final" -> parseFinal(json)
                tool.isNotBlank() -> parseToolCall(json)
                else -> AgentAction.Invalid("El JSON no contiene una acción válida.")
            }
        } catch (exception: Exception) {
            AgentAction.Invalid(
                "No pude interpretar la acción del modelo. Respuesta inválida: ${exception.message}"
            )
        }
    }

    private fun parseToolCall(json: JSONObject): AgentAction {
        val tool = json.optString("tool").trim()
        val arguments = json.optJSONObject("arguments") ?: JSONObject()

        if (tool.isBlank()) {
            return AgentAction.Invalid("Falta el campo tool.")
        }

        if (!ToolRegistry.exists(tool)) {
            return AgentAction.Invalid("La tool '$tool' no existe.")
        }

        return AgentAction.ToolCall(tool, arguments)
    }

    private fun parseToolQueue(json: JSONObject): AgentAction {
        val actions = json.optJSONArray("actions")
            ?: return AgentAction.Invalid("tool_queue necesita actions.")

        return parseArrayAsQueue(actions)
    }

    private fun parseArrayAsQueue(array: JSONArray): AgentAction {
        val actions = mutableListOf<AgentAction.ToolCall>()

        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i)
                ?: return AgentAction.Invalid("Cada acción debe ser un objeto JSON.")

            val tool = item.optString("tool").trim()
            val arguments = item.optJSONObject("arguments") ?: JSONObject()

            if (tool.isBlank()) {
                return AgentAction.Invalid("Una acción no tiene tool.")
            }

            if (!ToolRegistry.exists(tool)) {
                return AgentAction.Invalid("La tool '$tool' no existe.")
            }

            actions.add(AgentAction.ToolCall(tool, arguments))
        }

        if (actions.isEmpty()) {
            return AgentAction.Invalid("La cola de acciones está vacía.")
        }

        return AgentAction.ToolQueue(actions)
    }

    private fun parseAskUser(json: JSONObject): AgentAction {
        val message = json.optString("message").trim()
        if (message.isBlank()) return AgentAction.Invalid("ask_user necesita message.")
        return AgentAction.AskUser(message)
    }

    private fun parseFinal(json: JSONObject): AgentAction {
        val message = json.optString("message").trim()
        if (message.isBlank()) return AgentAction.Invalid("final necesita message.")
        return AgentAction.Final(message)
    }
}