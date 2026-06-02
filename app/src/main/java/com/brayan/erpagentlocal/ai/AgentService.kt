package com.brayan.erpagentlocal.ai

import com.brayan.erpagentlocal.agent.AgentAction
import com.brayan.erpagentlocal.agent.AgentExecutor
import com.brayan.erpagentlocal.agent.AgentMemory
import com.brayan.erpagentlocal.agent.AgentPlanNormalizer
import com.brayan.erpagentlocal.agent.AgentResponseBuilder
import com.brayan.erpagentlocal.agent.AgentState
import com.brayan.erpagentlocal.agent.ToolRegistry

class AgentService(
    private val planner: AgentPlanner = AgentPlanner(),
    private val normalizer: AgentPlanNormalizer = AgentPlanNormalizer(),
    private val executor: AgentExecutor = AgentExecutor(),
    private val responseBuilder: AgentResponseBuilder = AgentResponseBuilder(),
    private val memory: AgentMemory = AgentMemory()
) {

    private var agentState: AgentState = AgentState.empty()
    private var lastExecutionTraceText: String = ""

    suspend fun processUserMessage(input: String): String {
        val trimmed = input.trim()

        if (trimmed.isBlank()) return "Escribe una instrucción."

        return when (trimmed) {
            "/tools" -> ToolRegistry.describeTools()
            "/prompt" -> PromptProvider.buildSystemPrompt()
            "/memory" -> memory.asText()
            "/memory-short" -> memory.asShortSummary()
            "/state" -> agentState.toDebugText()
            "/trace" -> lastExecutionTraceText.ifBlank { "Todavía no hay trazabilidad." }

            "/clear" -> {
                memory.clear()
                agentState = AgentState.empty()
                lastExecutionTraceText = ""
                "Chat limpiado. Puedes escribir o grabar una nueva instrucción."
            }

            else -> "Inicializa el modelo local para usar el agente."
        }
    }

    suspend fun processNaturalLanguageWithModel(
        userMessage: String,
        localModelService: LocalModelService
    ): String {
        val cleanUserMessage = userMessage.trim()

        if (cleanUserMessage.isBlank()) {
            return "Escribe una instrucción para el agente."
        }

        if (!localModelService.isInitialized()) {
            return "El modelo local no está inicializado."
        }

        memory.addUser(cleanUserMessage)

        return try {
            val plan = planner.plan(
                userMessage = cleanUserMessage,
                state = agentState,
                memoryText = compactMemory(),
                localModelService = localModelService
            )

            val actions = when (plan) {
                is AgentAction.ToolCall -> listOf(plan)
                is AgentAction.ToolQueue -> plan.actions
                is AgentAction.AskUser -> {
                    agentState = agentState.withPendingQuestion(plan.message)
                    memory.addAssistant(plan.message)
                    return plan.message
                }
                is AgentAction.Final -> {
                    memory.addAssistant(plan.message)
                    return plan.message
                }
                is AgentAction.Invalid -> {
                    val message = "No pude interpretar la decisión del modelo.\n\n${plan.error}"
                    memory.addAssistant(message)
                    return message
                }
            }

            val normalizedActions = normalizer.normalize(actions, agentState)
            val report = executor.execute(normalizedActions, agentState)

            agentState = report.state
            lastExecutionTraceText = buildTrace(report.actions.map { it.tool })

            val response = responseBuilder.build(report)

            memory.addAssistant(response)

            response
        } catch (exception: Exception) {
            val message = """
                Ocurrió un error durante la ejecución del agente.

                Detalle:
                ${exception.message ?: "Error desconocido"}
            """.trimIndent()

            memory.addAssistant(message)
            message
        }
    }

    fun getStateText(): String = agentState.toDebugText()

    fun getLastExecutionTraceText(): String = lastExecutionTraceText

    fun getLastToolTraceSummary(): String {
        val lastTool = agentState.executedTools.lastOrNull()
            ?: return "No tools executed yet."

        return buildString {
            appendLine("Tool: ${lastTool.toolName}")
            appendLine("Arguments: ${lastTool.argumentsJson}")
            appendLine("Success: ${lastTool.success}")
            appendLine("Result: ${lastTool.resultSummary}")
            if (!lastTool.resultJson.isNullOrBlank()) {
                appendLine("Relevant result: ${lastTool.resultJson}")
            }
        }.trim()
    }

    fun getLastEndpointTraceText(): String {
        val lastTool = agentState.executedTools.lastOrNull()
            ?: return "N/D"

        val toolDefinition = ToolRegistry.find(lastTool.toolName)
            ?: return lastTool.toolName

        return "${toolDefinition.method} ${toolDefinition.path}"
    }

    private fun compactMemory(): String {
        val raw = memory.asShortSummary()

        if (raw.isBlank() || raw.contains("vacía", ignoreCase = true)) {
            return "empty"
        }

        return raw
            .lines()
            .takeLast(8)
            .joinToString("\n")
            .take(800)
    }

    private fun buildTrace(tools: List<String>): String {
        if (tools.isEmpty()) return "Sin acciones ejecutadas."

        return buildString {
            appendLine("TRAZABILIDAD")
            tools.forEachIndexed { index, tool ->
                appendLine("${index + 1}. $tool")
            }
        }.trim()
    }
}