package com.brayan.erpagentlocal.metrics

enum class PerformanceEvent(val key: String) {
    APP_START_MS("app_start_ms"),
    MODEL_LOAD_MS("model_load_ms"),
    MODEL_WARMUP_MS("model_warmup_ms"),
    GENERATION_MS("generation_ms"),
    BACKEND_HEALTH_MS("backend_health_ms"),
    LAMBDA_LATENCY_MS("lambda_latency_ms"),
    TOTAL_TASK_MS("total_task_ms"),
    VOSK_LOAD_MS("vosk_load_ms"),
    SPEECH_TRANSCRIPTION_MS("speech_transcription_ms"),
    TOOL_SUCCESS_COUNT("tool_success_count"),
    TOOL_ERROR_COUNT("tool_error_count"),
    ACTIVE_BACKEND("active_backend")
}
