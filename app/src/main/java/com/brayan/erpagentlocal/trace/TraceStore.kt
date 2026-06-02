package com.brayan.erpagentlocal.trace

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class TraceStore(
    context: Context,
    private val maxRecords: Int = 10
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun save(record: TraceRecord) {
        val records = readRecords()
        records.put(record.toJson())
        trim(records)
        preferences.edit().putString(KEY_RECORDS, records.toString()).apply()
    }

    fun clear() {
        preferences.edit().remove(KEY_RECORDS).apply()
    }

    private fun TraceRecord.toJson(): JSONObject {
        return JSONObject()
            .put("originalText", originalText)
            .put("transcribedText", transcribedText)
            .put("normalizedText", normalizedText)
            .put("promptSummary", promptSummary)
            .put("understoodIntent", understoodIntent)
            .put("requestedTool", requestedTool)
            .put("argumentsText", argumentsText)
            .put("validationResult", validationResult)
            .put("endpointCalled", endpointCalled)
            .put("lambdaResponse", lambdaResponse)
            .put("updatedState", updatedState)
            .put("totalMs", totalMs ?: JSONObject.NULL)
            .put("modelName", modelName)
            .put("activeBackend", activeBackend)
            .put("toolsExecuted", toolsExecuted)
            .put("result", result)
            .put("error", error ?: "")
            .put("createdAtMs", createdAtMs)
    }

    private fun readRecords(): JSONArray {
        return try {
            JSONArray(preferences.getString(KEY_RECORDS, "[]") ?: "[]")
        } catch (_: Exception) {
            JSONArray()
        }
    }

    private fun trim(records: JSONArray) {
        while (records.length() > maxRecords) {
            records.remove(0)
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "erp_agent_trace_store"
        private const val KEY_RECORDS = "records"
    }
}
