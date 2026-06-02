package com.brayan.erpagentlocal.speech

import com.brayan.erpagentlocal.util.normalizeSpanishNumbers

class SpeechTextNormalizer {

    fun normalize(rawText: String): SpeechNormalizationResult {
        val original = rawText.trim()
        if (original.isBlank()) {
            return SpeechNormalizationResult(
                originalText = rawText,
                normalizedText = "",
                isEmpty = true
            )
        }

        val normalized = normalizeSpanishNumbers(original.lowercase())
            .replace(Regex("\\basucar\\b"), "azucar")
            .replace(Regex("\\bazucar\\b"), "azucar")
            .replace(Regex("\\bcafe\\b"), "cafe")
            .replace(Regex("\\bbende\\b"), "vende")
            .replace(Regex("\\bbender\\b"), "vender")
            .replace(Regex("\\bconpra\\b"), "compra")
            .replace(Regex("\\bconprar\\b"), "comprar")
            .replace(Regex("\\bcliente\\s+llamado\\s+el\\s+"), "cliente llamado ")
            .replace(Regex("\\bcliente\\s+llamada\\s+la\\s+"), "cliente llamada ")
            .replace(Regex("\\bpor favor\\b"), "")
            .replace(Regex("\\beste\\b"), "")
            .replace(Regex("\\bemmm+\\b|\\bmmm+\\b|\\beh+\\b|\\bah+\\b"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        return SpeechNormalizationResult(
            originalText = original,
            normalizedText = normalized,
            isEmpty = normalized.isBlank()
        )
    }
}

data class SpeechNormalizationResult(
    val originalText: String,
    val normalizedText: String,
    val isEmpty: Boolean
)
