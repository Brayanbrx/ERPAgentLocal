package com.brayan.erpagentlocal.agent

enum class AgentErrorType {
    BAD_REQUEST,
    NOT_FOUND,
    CONFLICT,
    SERVER_ERROR,
    NETWORK_ERROR,
    MODEL_NOT_READY,
    INVALID_JSON,
    TOOL_NOT_FOUND,
    VALIDATION_ERROR,
    UNKNOWN
}