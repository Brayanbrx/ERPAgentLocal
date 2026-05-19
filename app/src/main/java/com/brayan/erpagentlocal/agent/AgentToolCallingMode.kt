package com.brayan.erpagentlocal.agent

enum class AgentToolCallingMode(
    val title: String,
    val description: String
) {
    JSON_MANUAL(
        title = "JSON tool_call manual",
        description = "El modelo responde JSON, Android parsea, valida y ejecuta tools."
    ),

    NATIVE_LITERT_EXPERIMENTAL(
        title = "LiteRT-LM native tools experimental",
        description = "El modelo usa tools registradas en ConversationConfig. Requiere modelo y API compatibles."
    )
}