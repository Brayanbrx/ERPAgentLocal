package com.brayan.erpagentlocal.ai

import com.brayan.erpagentlocal.agent.AgentAction
import com.brayan.erpagentlocal.agent.AgentActionParser
import com.brayan.erpagentlocal.agent.AgentState

class AgentPlanner(
    private val parser: AgentActionParser = AgentActionParser()
) {

    suspend fun plan(
        userMessage: String,
        state: AgentState,
        memoryText: String,
        localModelService: LocalModelService
    ): AgentAction {
        val prompt = PromptProvider.buildAgentDecisionPrompt(
            userMessage = userMessage,
            memoryText = memoryText,
            loopContext = state.toPromptBlock()
        )

        val raw = localModelService.generateDecisionFresh(prompt)
        return parser.parse(raw)
    }
}