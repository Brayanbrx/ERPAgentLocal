package com.brayan.erpagentlocal.speech

import com.brayan.erpagentlocal.util.normalizeSpanishNumbers
import java.text.Normalizer

/**
 * Normaliza texto del reconocimiento de voz en español
 *
 * Objetivo:
 * - Limpiar muletillas.
 * - Convertir números hablados a números.
 * - Corregir errores frecuentes de Vosk.
 * - Priorizar palabras clave relacionadas con las tools del ERP.
 * - Evitar modificar agresivamente nombres de clientes o productos.
 */
class SpeechTextNormalizer {

    /**
     * Convierte una transcripcion de voz en una versión más limpia y útil
     * para que el Modelo pueda interpretar mejor la intención.
     */
    fun normalize(rawText: String): SpeechNormalizationResult {
        val original = rawText.trim()

        if (original.isBlank()) {
            return SpeechNormalizationResult(
                originalText = rawText,
                normalizedText = "",
                isEmpty = true
            )
        }

        val normalized = original
            .lowercase()
            .let { normalizeSpanishNumbers(it) }
            .removeAccents()
            .normalizeCommandPhrases()
            .normalizeToolKeywords()
            .removeFillerWords()
            .normalizeSpaces()
            .trim()

        return SpeechNormalizationResult(
            originalText = original,
            normalizedText = normalized,
            isEmpty = normalized.isBlank()
        )
    }

    /**
     * Quita acentos para facilitar búsqueda y coincidencia
     * Ejemplo:
     * - "café" -> "cafe"
     * - "véndele" -> "vendele"
     */
    private fun String.removeAccents(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    /**
     * Normaliza frases completas que suelen venir mal desde voz
     * Se usa antes de palabras sueltas porque algunas correcciones dependen
     * del contexto de la frase.
     */
    private fun String.normalizeCommandPhrases(): String {
        return this
            // Cliente llamado "el/la X" -> cliente llamado X
            .replace(Regex("\\bcliente\\s+llamado\\s+el\\s+"), "cliente llamado ")
            .replace(Regex("\\bcliente\\s+llamada\\s+la\\s+"), "cliente llamada ")
            .replace(Regex("\\bcliente\\s+llamado\\s+la\\s+"), "cliente llamado ")
            .replace(Regex("\\bcliente\\s+llamada\\s+el\\s+"), "cliente llamada ")

            // Cliente de nombre X -> cliente llamado X
            .replace(Regex("\\bcliente\\s+de\\s+nombre\\s+"), "cliente llamado ")
            .replace(Regex("\\bcliente\\s+con\\s+nombre\\s+"), "cliente llamado ")

            // Producto llamado "el/la X" -> producto llamado X
            .replace(Regex("\\bproducto\\s+llamado\\s+el\\s+"), "producto llamado ")
            .replace(Regex("\\bproducto\\s+llamado\\s+la\\s+"), "producto llamado ")
            .replace(Regex("\\bproducto\\s+de\\s+nombre\\s+"), "producto llamado ")
            .replace(Regex("\\bproducto\\s+con\\s+nombre\\s+"), "producto llamado ")

            // Venta a cliente
            .replace(Regex("\\bvenderle\\s+a\\b"), "vende a")
            .replace(Regex("\\bvendele\\s+a\\b"), "vende a")
            .replace(Regex("\\bvendeme\\s+a\\b"), "vende a")
    }

    /**
     * Normaliza palabras relacionadas con las tools
     * Ayuda al LLM a decidir:
     * - createPurchase
     * - createSale
     * - createCustomer
     * - createProduct
     * - searchCustomer
     * - searchProduct
     * - listProducts
     * - getInventory
     */
    private fun String.normalizeToolKeywords(): String {
        var text = this

        val aliases = linkedMapOf(
            // Compra / createPurchase
            "conpra" to "compra",
            "conprar" to "comprar",
            "conprame" to "comprame",
            "kompra" to "compra",
            "komprar" to "comprar",
            "comprame" to "compra",
            "compranos" to "compra",

            // Venta / createSale
            "bende" to "vende",
            "bender" to "vender",
            "benderle" to "venderle",
            "vendeme" to "vende",
            "vendele" to "vende",
            "venderle" to "vende",
            "vendelo" to "vende",
            "vendela" to "vende",

            // Crear / registrar
            "krear" to "crear",
            "krea" to "crea",
            "rejistra" to "registra",
            "rejistrar" to "registrar",
            "rejistro" to "registro",
            "reguistrar" to "registrar",
            "agregame" to "agrega",
            "anade" to "agrega",
            "anadir" to "agregar",

            // Buscar / consultar
            "vusca" to "busca",
            "vuzca" to "busca",
            "buskar" to "buscar",
            "buscame" to "busca",
            "encuentrame" to "encuentra",
            "consuta" to "consulta",
            "consultame" to "consulta",

            // Listar / mostrar
            "muestrame" to "muestra",
            "mostrame" to "muestra",
            "muetrame" to "muestra",
            "muestreame" to "muestra",
            "listame" to "lista",

            // Inventario / stock
            "stok" to "stock",
            "estok" to "stock",
            "estoc" to "stock",
            "estoque" to "stock",
            "inbentario" to "inventario",
            "imbentario" to "inventario",
            "inventareo" to "inventario",
            "existensia" to "existencia",
            "existensias" to "existencias",

            // Entidades del ERP
            "clente" to "cliente",
            "clienta" to "cliente",
            "clientesito" to "cliente",
            "produsto" to "producto",
            "produto" to "producto",
            "prodcuto" to "producto",
            "articulo" to "producto",
            "item" to "producto",

            // Cantidades/unidades
            "unidade" to "unidad",
            "unidaddes" to "unidades",
            "piesas" to "piezas",
            "piesa" to "pieza"
        )

        aliases.forEach { (wrong, correct) ->
            text = text.replace(
                Regex("\\b${Regex.escape(wrong)}\\b"),
                correct
            )
        }

        return text
    }

    /**
     * Elimina palabras relleno que no aportan intención ni datos
     */
    private fun String.removeFillerWords(): String {
        return this
            .replace(Regex("\\bpor\\s+favor\\b"), " ")
            .replace(Regex("\\bporfa\\b"), " ")
            .replace(Regex("\\bporfis\\b"), " ")
            .replace(Regex("\\beste\\b"), " ")
            .replace(Regex("\\bo\\s+sea\\b"), " ")
            .replace(Regex("\\bbueno\\b"), " ")
            .replace(Regex("\\ba\\s+ver\\b"), " ")
            .replace(Regex("\\bemmm+\\b|\\bmmm+\\b|\\beh\\b|\\bah\\b|\\behh\\b|\\bam\\b"), " ")
    }

    /**
     * Compacta espacios multiples, saltos de línea y tabulaciones
     */
    private fun String.normalizeSpaces(): String {
        return this.replace(Regex("\\s+"), " ")
    }
}

/**
 * Resultado de normalizar una transcripción de voz
 *
 * @property originalText Texto recibido antes de aplicar reglas de limpieza.
 * @property normalizedText Texto listo para ser interpretado por la aplicación.
 * @property isEmpty Indica si el resultado normalizado quedó sin contenido útil.
 */
data class SpeechNormalizationResult(
    val originalText: String,
    val normalizedText: String,
    val isEmpty: Boolean
)