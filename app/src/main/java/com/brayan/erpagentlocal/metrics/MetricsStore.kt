package com.brayan.erpagentlocal.metrics

import android.content.Context
import org.json.JSONObject

class MetricsStore(
    context: Context,
    private val maxRecords: Int = 10
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveSnapshot(
        label: String,
        snapshot: PerformanceSnapshot
    ) {
        val records = readRecords()
        val record = JSONObject()
            .put("label", label)
            .put("createdAtMs", System.currentTimeMillis())
            .put("updatedAtMs", snapshot.updatedAtMs)
            .put("durationsMs", JSONObject(snapshot.durationsMs))
            .put("counters", JSONObject(snapshot.counters))
            .put("attributes", JSONObject(snapshot.attributes))

        records.put(record)
        trim(records)
        preferences.edit().putString(KEY_RECORDS, records.toString()).apply()
    }

    fun clear() {
        preferences.edit().remove(KEY_RECORDS).apply()
    }

    private fun readRecords(): org.json.JSONArray {
        return try {
            org.json.JSONArray(preferences.getString(KEY_RECORDS, "[]") ?: "[]")
        } catch (_: Exception) {
            org.json.JSONArray()
        }
    }

    private fun trim(records: org.json.JSONArray) {
        while (records.length() > maxRecords) {
            records.remove(0)
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "erp_agent_metrics_store"
        private const val KEY_RECORDS = "records"
    }
}
