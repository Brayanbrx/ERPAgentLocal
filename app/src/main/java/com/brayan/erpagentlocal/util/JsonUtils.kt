package com.brayan.erpagentlocal.util

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
}