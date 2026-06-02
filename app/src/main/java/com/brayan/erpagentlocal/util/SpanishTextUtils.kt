package com.brayan.erpagentlocal.util

private val spanishNumberWords = mapOf(
    "cero" to 0,
    "uno" to 1,
    "una" to 1,
    "dos" to 2,
    "tres" to 3,
    "cuatro" to 4,
    "cinco" to 5,
    "seis" to 6,
    "siete" to 7,
    "ocho" to 8,
    "nueve" to 9,
    "diez" to 10,
    "once" to 11,
    "doce" to 12,
    "trece" to 13,
    "catorce" to 14,
    "quince" to 15,
    "dieciseis" to 16,
    "diecisiete" to 17,
    "dieciocho" to 18,
    "diecinueve" to 19,
    "veinte" to 20,
    "treinta" to 30,
    "cuarenta" to 40,
    "cincuenta" to 50,
    "sesenta" to 60,
    "setenta" to 70,
    "ochenta" to 80,
    "noventa" to 90,
    "cien" to 100,
    "ciento" to 100,
    "doscientos" to 200,
    "trescientos" to 300,
    "cuatrocientos" to 400,
    "quinientos" to 500,
    "seiscientos" to 600,
    "setecientos" to 700,
    "ochocientos" to 800,
    "novecientos" to 900,
    "mil" to 1000
)

fun normalizeSpanishNumbers(text: String): String {
    return text
        .replace(Regex("(?i)\\b(cien(?:to)?|doscientos|trescientos|cuatrocientos|quinientos|seiscientos|setecientos|ochocientos|novecientos)\\s+(veinte|treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa|diez|once|doce|trece|catorce|quince|dieciseis|diecisiete|dieciocho|diecinueve|uno|una|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)(?:\\s+y\\s+(uno|una|dos|tres|cuatro|cinco|seis|siete|ocho|nueve))?\\b")) { match ->
            val hundreds = spanishNumberWords[match.groupValues[1].lowercase()] ?: 0
            val tens = spanishNumberWords[match.groupValues[2].lowercase()] ?: 0
            val units = if (match.groupValues.size > 3 && match.groupValues[3].isNotBlank()) {
                spanishNumberWords[match.groupValues[3].lowercase()] ?: 0
            } else {
                0
            }
            (hundreds + tens + units).toString()
        }
        .replace(Regex("(?i)\\b(cien(?:to)?|doscientos|trescientos|cuatrocientos|quinientos|seiscientos|setecientos|ochocientos|novecientos|mil)\\b")) { match ->
            spanishNumberWords[match.groupValues[1].lowercase()]?.toString() ?: match.value
        }
        .replace(Regex("(?i)\\bveintiuno\\b"), "21")
        .replace(Regex("(?i)\\bveintidos\\b"), "22")
        .replace(Regex("(?i)\\bveintitres\\b"), "23")
        .replace(Regex("(?i)\\bveinticuatro\\b"), "24")
        .replace(Regex("(?i)\\bveinticinco\\b"), "25")
        .replace(Regex("(?i)\\bveintiseis\\b"), "26")
        .replace(Regex("(?i)\\bveintisiete\\b"), "27")
        .replace(Regex("(?i)\\bveintiocho\\b"), "28")
        .replace(Regex("(?i)\\bveintinueve\\b"), "29")
        .replace(Regex("(?i)\\b(?:uno|una)\\b"), "1")
        .replace(Regex("(?i)\\bdos\\b"), "2")
        .replace(Regex("(?i)\\btres\\b"), "3")
        .replace(Regex("(?i)\\bcuatro\\b"), "4")
        .replace(Regex("(?i)\\bcinco\\b"), "5")
        .replace(Regex("(?i)\\bseis\\b"), "6")
        .replace(Regex("(?i)\\bsiete\\b"), "7")
        .replace(Regex("(?i)\\bocho\\b"), "8")
        .replace(Regex("(?i)\\bnueve\\b"), "9")
        .replace(Regex("(?i)\\bdiez\\b"), "10")
        .replace(Regex("(?i)\\bonce\\b"), "11")
        .replace(Regex("(?i)\\bdoce\\b"), "12")
        .replace(Regex("(?i)\\btrece\\b"), "13")
        .replace(Regex("(?i)\\bcatorce\\b"), "14")
        .replace(Regex("(?i)\\bquince\\b"), "15")
        .replace(Regex("(?i)\\bveinte\\b"), "20")
        .replace(Regex("(?i)\\btreinta\\b"), "30")
        .replace(Regex("(?i)\\bcuarenta\\b"), "40")
        .replace(Regex("(?i)\\bcincuenta\\b"), "50")
        .replace(Regex("(?i)\\bsesenta\\b"), "60")
        .replace(Regex("(?i)\\bsetenta\\b"), "70")
        .replace(Regex("(?i)\\bochenta\\b"), "80")
        .replace(Regex("(?i)\\bnoventa\\b"), "90")
}
