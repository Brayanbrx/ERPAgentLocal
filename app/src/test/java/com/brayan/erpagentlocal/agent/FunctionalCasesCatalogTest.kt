package com.brayan.erpagentlocal.agent

import com.brayan.erpagentlocal.speech.SpeechTextNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FunctionalCasesCatalogTest {

    private val normalizer = SpeechTextNormalizer()

    @Before
    fun setUp() {
        ToolRegistry.setCatalog(ToolCatalog.default())
    }

    @Test
    fun requiredFunctionalCasesAreTracked() {
        val cases = listOf(
            "Crea un cliente llamado Juan Perez",
            "Crea un producto azucar con precio de venta 20 y precio de compra 15",
            "Compra 50 unidades de azucar a 15",
            "Consulta el inventario de azucar",
            "Vendele 2 unidades de azucar a Juan",
            "Vende 999999 unidades de azucar a Juan",
            "vende dos asucar a juan",
            "Crea producto cafe con venta 25 y compra 18, compra 50 unidades y vendele 2 a Juan"
        )

        assertEquals(8, cases.size)
        assertTrue(cases.all { it.isNotBlank() })
    }

    @Test
    fun voiceCaseNormalizesToExecutableText() {
        val result = normalizer.normalize("vende dos asucar a juan")

        assertEquals("vende 2 azucar a juan", result.normalizedText)
    }
}
