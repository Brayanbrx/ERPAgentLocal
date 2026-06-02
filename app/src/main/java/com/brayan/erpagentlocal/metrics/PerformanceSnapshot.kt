package com.brayan.erpagentlocal.metrics

data class PerformanceSnapshot(
    val durationsMs: Map<String, Long> = emptyMap(),
    val counters: Map<String, Long> = emptyMap(),
    val attributes: Map<String, String> = emptyMap(),
    val updatedAtMs: Long = System.currentTimeMillis()
) {
    fun duration(event: PerformanceEvent): Long? = durationsMs[event.key]
}
