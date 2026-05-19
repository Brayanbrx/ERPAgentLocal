package com.brayan.erpagentlocal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.ui.theme.ErpColors

@Composable
fun TraceViewer(
    traceText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ErpColors.SurfaceSoft,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Trazabilidad del agente",
            style = MaterialTheme.typography.titleSmall,
            color = ErpColors.TextPrimary
        )

        Text(
            text = "Observe → Decide → Validate → Act → Store → Continue / Finish",
            style = MaterialTheme.typography.bodySmall,
            color = ErpColors.TextMuted
        )

        if (traceText.isBlank()) {
            Text(
                text = "Todavía no hay trazabilidad disponible. Ejecuta un caso de prueba para ver los pasos del agente.",
                style = MaterialTheme.typography.bodySmall,
                color = ErpColors.TextMuted
            )
        } else {
            TraceTextBlock(traceText = traceText)
        }
    }
}

@Composable
private fun TraceTextBlock(
    traceText: String
) {
    val highlightedText = traceText
        .replace("TRAZABILIDAD PARA DEFENSA", "📌 TRAZABILIDAD PARA DEFENSA")
        .replace("Paso ", "▶ Paso ")
        .replace("Observe", "Observe")
        .replace("Decide", "Decide")
        .replace("Validate", "Validate")
        .replace("Act", "Act")
        .replace("Store", "Store")
        .replace("Finish", "Finish")

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        text = highlightedText,
        style = MaterialTheme.typography.bodySmall,
        color = ErpColors.TextSecondary
    )
}