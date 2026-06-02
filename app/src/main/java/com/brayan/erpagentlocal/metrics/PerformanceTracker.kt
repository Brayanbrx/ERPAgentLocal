package com.brayan.erpagentlocal.metrics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PerformanceTracker {

    private val lock = Any()
    private val activeEvents = mutableMapOf<String, Long>()
    private val durationsMs = linkedMapOf<String, Long>()
    private val counters = linkedMapOf<String, Long>()
    private val attributes = linkedMapOf<String, String>()

    private val _snapshot = MutableStateFlow(PerformanceSnapshot())
    val snapshot: StateFlow<PerformanceSnapshot> = _snapshot.asStateFlow()

    private val appStartAtMs = System.currentTimeMillis()

    init {
        start(PerformanceEvent.APP_START_MS)
    }

    fun markAppStarted(): Long {
        val elapsed = System.currentTimeMillis() - appStartAtMs
        record(PerformanceEvent.APP_START_MS, elapsed)
        return elapsed
    }

    fun start(event: PerformanceEvent) {
        synchronized(lock) {
            activeEvents[event.key] = System.currentTimeMillis()
        }
    }

    fun finish(event: PerformanceEvent): Long {
        synchronized(lock) {
            val startAt = activeEvents.remove(event.key) ?: System.currentTimeMillis()
            val elapsed = System.currentTimeMillis() - startAt
            durationsMs[event.key] = elapsed
            publishLocked()
            return elapsed
        }
    }

    fun record(event: PerformanceEvent, durationMs: Long) {
        synchronized(lock) {
            durationsMs[event.key] = durationMs.coerceAtLeast(0L)
            publishLocked()
        }
    }

    fun increment(event: PerformanceEvent, amount: Long = 1L) {
        synchronized(lock) {
            counters[event.key] = (counters[event.key] ?: 0L) + amount
            publishLocked()
        }
    }

    fun setAttribute(event: PerformanceEvent, value: String) {
        synchronized(lock) {
            attributes[event.key] = value
            publishLocked()
        }
    }

    fun getSnapshot(): PerformanceSnapshot {
        synchronized(lock) {
            return buildSnapshotLocked()
        }
    }

    fun clear() {
        synchronized(lock) {
            activeEvents.clear()
            durationsMs.clear()
            counters.clear()
            attributes.clear()
            publishLocked()
        }
    }

    private fun publishLocked() {
        _snapshot.value = buildSnapshotLocked()
    }

    private fun buildSnapshotLocked(): PerformanceSnapshot {
        return PerformanceSnapshot(
            durationsMs = durationsMs.toMap(),
            counters = counters.toMap(),
            attributes = attributes.toMap()
        )
    }
}
