package com.brayan.erpagentlocal.util

import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {

    fun extractJsonObject(rawText: String): String {
        val trimmed = rawText.trim()

        val fencedRegex = Regex(
            pattern = "```(?:json)?\\s*([\\s\\S]*?)\\s*```",
            option = RegexOption.IGNORE_CASE
        )

        val fencedMatch = fencedRegex.find(trimmed)

        if (fencedMatch != null) {
            return fencedMatch.groupValues[1].trim()
        }

        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')

        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1).trim()
        }

        return trimmed
    }

    fun isValidJsonObject(rawText: String): Boolean {
        return try {
            JSONObject(extractJsonObject(rawText))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun toPrettyJson(rawText: String): String {
        return try {
            val json = JSONObject(extractJsonObject(rawText))
            json.toString(2)
        } catch (_: Exception) {
            rawText
        }
    }

    fun toPrettyJson(jsonObject: JSONObject): String {
        return jsonObject.toString(2)
    }

    fun toPrettyJson(jsonArray: JSONArray): String {
        return jsonArray.toString(2)
    }

    fun safeGetString(
        jsonObject: JSONObject?,
        key: String,
        defaultValue: String = ""
    ): String {
        if (jsonObject == null) return defaultValue
        return jsonObject.optString(key, defaultValue)
    }

    fun safeGetObject(
        jsonObject: JSONObject?,
        key: String
    ): JSONObject? {
        if (jsonObject == null) return null
        return jsonObject.optJSONObject(key)
    }

    fun safeGetArray(
        jsonObject: JSONObject?,
        key: String
    ): JSONArray? {
        if (jsonObject == null) return null
        return jsonObject.optJSONArray(key)
    }
}