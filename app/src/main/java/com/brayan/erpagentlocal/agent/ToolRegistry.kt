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

    fun setCatalog(newCatalog: ToolCatalog) {
        catalog = newCatalog
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

    fun count(): Int {
        return catalog.count()
    }
}