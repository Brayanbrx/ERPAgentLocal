package com.brayan.erpagentlocal.agent

/**
 * Memoria compacta de sesión del agente ERP.
 *
 * Filosofía: esto NO es un log de chatbot.
 * Solo guardamos lo que el LLM necesita para tomar decisiones correctas:
 *   - IDs/nombres de entidades clave descubiertas en esta sesión (cliente, producto, venta, compra)
 *   - Los últimas instrucciones del usuario para contexto conversacional
 *
 * Las respuestas completas de herramientas NO se guardan aquí —
 * ya están en AgentState que se inyecta en cada prompt del LLM.
 * Guardar respuestas JSON completas solo desperdicia memoria y agrega ruido al prompt.
 */
class AgentMemory(private val maxUserMessages: Int = 3) {

    // Pares clave→valor compactos: ej. "lastCustomerId" → "CUS-ABC123"
    private val sessionFacts = LinkedHashMap<String, String>()

    // Últimas N instrucciones del usuario para contexto conversacional
    private val recentUserMessages = ArrayDeque<String>()

    fun addUser(content: String) {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return
        recentUserMessages.addLast(trimmed)
        while (recentUserMessages.size > maxUserMessages) {
            recentUserMessages.removeFirst()
        }
    }

    // Las respuestas del agente se resumen en AgentState — no hace falta duplicarlas aquí.
    fun addAssistant(content: String) {}

    // Las respuestas JSON de herramientas las gestiona AgentState/AgentStateUpdater.
    // Aquí solo se almacenan hechos compactos mediante recordFact(), no respuestas completas.
    fun addTool(toolName: String, content: String) {}

    fun addSystem(content: String) {}

    /**
     * Registra un hecho compacto de entidad tras ejecutar una herramienta exitosamente.
     * Lo llama AgentService con los datos clave extraídos del resultado.
     * Ejemplo: recordFact("lastCustomerId", "CUS-ABC123")
     */
    fun recordFact(key: String, value: String) {
        sessionFacts[key] = value
        // Limita el tamaño; los hechos más antiguos se eliminan primero
        while (sessionFacts.size > 20) {
            sessionFacts.remove(sessionFacts.keys.first())
        }
    }

    /**
     * Construye el contexto compacto para inyectar en los prompts del LLM.
     * Devuelve cadena vacía cuando aún no hay hechos (primer mensaje).
     */
    fun buildCompactContext(): String {
        if (sessionFacts.isEmpty()) return ""
        return buildString {
            appendLine("Known entities from this session:")
            sessionFacts.entries.forEach { (k, v) ->
                appendLine("  $k: $v")
            }
        }.trim()
    }

    fun clear() {
        sessionFacts.clear()
        recentUserMessages.clear()
    }

    fun isEmpty() = sessionFacts.isEmpty() && recentUserMessages.isEmpty()

    // API usada por los comandos /memory y /memory-short en AgentService
    fun asText(): String {
        if (isEmpty()) return "La memoria de sesión está vacía."
        return buildCompactContext()
    }

    fun asShortSummary(): String {
        if (isEmpty()) return "La memoria de sesión está vacía."
        return buildString {
            if (sessionFacts.isNotEmpty()) {
                appendLine("Entidades conocidas en esta sesión:")
                sessionFacts.entries.forEach { (k, v) ->
                    appendLine("  $k: $v")
                }
            }
            if (recentUserMessages.isNotEmpty()) {
                if (isNotEmpty()) appendLine()
                appendLine("Últimas instrucciones del usuario:")
                recentUserMessages.forEach { msg ->
                    appendLine("  - ${msg.take(100)}")
                }
            }
        }.trim()
    }
}
