package com.brayan.erpagentlocal.agent

import org.json.JSONObject

sealed class AgentAction {

    data class ToolCall(
        val tool: String,
        val arguments: JSONObject
    ) : AgentAction()

    data class AskUser(
        val message: String
    ) : AgentAction()

    data class Final(
        val message: String
    ) : AgentAction()

    data class Invalid(
        val error: String
    ) : AgentAction()
}