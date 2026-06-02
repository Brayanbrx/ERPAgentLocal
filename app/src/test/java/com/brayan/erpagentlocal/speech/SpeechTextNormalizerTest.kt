package com.brayan.erpagentlocal.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeechTextNormalizerTest {

    private val normalizer = SpeechTextNormalizer()

    @Test
    fun normalizesCommonAsrErrorsAndNumbers() {
        val result = normalizer.normalize("vende dos asucar a juan por favor")

        assertFalse(result.isEmpty)
        assertEquals("vende 2 azucar a juan", result.normalizedText)
    }

    @Test
    fun normalizesCompositeSpanishNumbers() {
        val result = normalizer.normalize("compra ciento cincuenta unidades")

        assertEquals("compra 150 unidades", result.normalizedText)
    }

    @Test
    fun stripsSpeechNoise() {
        val result = normalizer.normalize("eh crea cliente llamado el Juan Perez mmm")

        assertEquals("crea cliente llamado juan perez", result.normalizedText)
    }

    @Test
    fun detectsEmptyText() {
        val result = normalizer.normalize("   ")

        assertTrue(result.isEmpty)
        assertEquals("", result.normalizedText)
    }
}
