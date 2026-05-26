package com.brayan.erpagentlocal.agent

/*
 * Compatibilidad temporal:
 *
 * En fases posteriores AgentService usará ToolCatalog directamente.
 * Por ahora mantenemos ToolRegistry para no romper PromptProvider,
 * AgentService y ToolExecutor actuales.
 */
object ToolRegistry {

    private var catalog: ToolCatalog = ToolCatalog.default()

    val tools: List<ToolDefinition>
        get() = catalog.getAll()

    fun setCatalog(newCatalog: ToolCatalog) {
        catalog = newCatalog
    }

    fun getCatalog(): ToolCatalog {
        return catalog
    }

    fun exists(toolName: String): Boolean {
        return catalog.exists(toolName)
    }

    fun find(toolName: String): ToolDefinition? {
        return catalog.find(toolName)
    }

    fun describeTools(): String {
        return catalog.describeTools()
    }

    fun describeToolsForPrompt(): String {
        return catalog.describeToolsForPrompt()
    }

    fun getAllTools(): List<ToolDefinition> {
        return catalog.getAll()
    }

    fun count(): Int {
        return catalog.count()
    }
}