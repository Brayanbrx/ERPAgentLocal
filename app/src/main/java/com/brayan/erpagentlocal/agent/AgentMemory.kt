package com.brayan.erpagentlocal.agent

data class AgentMemoryItem(
    val role: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun asTextLine(): String {
        return "[$createdAt] $role: $content"
    }

    fun asPromptLine(): String {
        return "${role.uppercase()}: $content"
    }
}

class AgentMemory(
    private val maxItems: Int = 30,
    private val maxPromptItems: Int = 12
) {

    private val items: MutableList<AgentMemoryItem> = mutableListOf()

    fun add(role: String, content: String) {
        val cleanRole = role.trim().ifBlank { "unknown" }
        val cleanContent = content.trim()

        if (cleanContent.isBlank()) {
            return
        }

        items.add(
            AgentMemoryItem(
                role = cleanRole,
                content = cleanContent
            )
        )

        trimToMaxItems()
    }

    fun addUser(content: String) {
        add("user", content)
    }

    fun addAssistant(content: String) {
        add("assistant", content)
    }

    fun addTool(
        toolName: String,
        content: String
    ) {
        add("tool:$toolName", content)
    }

    fun addSystem(content: String) {
        add("system", content)
    }

    fun getAll(): List<AgentMemoryItem> {
        return items.toList()
    }

    fun getLast(limit: Int): List<AgentMemoryItem> {
        if (limit <= 0) {
            return emptyList()
        }

        return items.takeLast(limit)
    }

    fun getLastForPrompt(): List<AgentMemoryItem> {
        return getLast(maxPromptItems)
    }

    fun clear() {
        items.clear()
    }

    fun size(): Int {
        return items.size
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun asText(): String {
        if (items.isEmpty()) {
            return ""
        }

        return buildString {
            items.forEach { item ->
                appendLine(item.asTextLine())
            }
        }
    }

    fun asPromptText(): String {
        if (items.isEmpty()) {
            return "No previous memory."
        }

        return buildString {
            getLastForPrompt().forEach { item ->
                appendLine(item.asPromptLine())
            }
        }
    }

    fun asShortSummary(): String {
        if (items.isEmpty()) {
            return "La memoria está vacía."
        }

        return buildString {
            appendLine("Mensajes guardados: ${items.size}")
            appendLine()

            getLastForPrompt().forEach { item ->
                appendLine("- ${item.role}: ${item.content.take(160)}")
            }
        }
    }

    private fun trimToMaxItems() {
        if (items.size <= maxItems) {
            return
        }

        val overflow = items.size - maxItems

        repeat(overflow) {
            if (items.isNotEmpty()) {
                items.removeAt(0)
            }
        }
    }
}