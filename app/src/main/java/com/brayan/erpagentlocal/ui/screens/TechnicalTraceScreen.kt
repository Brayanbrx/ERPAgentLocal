package com.brayan.erpagentlocal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.metrics.PerformanceEvent
import com.brayan.erpagentlocal.ui.theme.ErpColors

/**
 * Vista de SOLO LECTURA del Modo Tecnico. Consume el estado ya existente en
 * ChatUiState para mostrar trazabilidad, metricas y estado interno del agente.
 *
 * No ejecuta acciones, no modifica el modelo, la voz, las tools ni el backend.
 */
@Composable
fun TechnicalTraceScreen(
    uiState: ChatUiState,
    modifier: Modifier = Modifier
) {
    val snapshot = uiState.performanceSnapshot

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Estado del sistema ---
        TraceCard(title = "Estado del sistema") {
            InfoRow("Modelo", uiState.modelName.ifBlank { "N/D" })
            InfoRow("Backend activo", uiState.modelActiveBackend)
            InfoRow("Estado backend", uiState.backendStatus)
            InfoRow(
                "Latencia backend",
                uiState.backendLatencyMs?.let { "$it ms" } ?: "N/D"
            )
            InfoRow("Voz", if (uiState.speechReady) "Lista" else "No activada")
            InfoRow("Tools", "${uiState.toolsCount} (${uiState.toolsSource})")
        }

        // --- Metricas ---
        TraceCard(title = "Metricas") {
            InfoRow("Carga de modelo", formatMs(snapshot.duration(PerformanceEvent.MODEL_LOAD_MS)))
            InfoRow("Warm up", formatMs(snapshot.duration(PerformanceEvent.MODEL_WARMUP_MS)))
            InfoRow("Generacion", formatMs(snapshot.duration(PerformanceEvent.GENERATION_MS)))
            InfoRow("Vosk (carga)", formatMs(snapshot.duration(PerformanceEvent.VOSK_LOAD_MS)))
            InfoRow(
                "Transcripcion",
                formatMs(snapshot.duration(PerformanceEvent.SPEECH_TRANSCRIPTION_MS))
            )
            InfoRow("Lambda", formatMs(snapshot.duration(PerformanceEvent.LAMBDA_LATENCY_MS)))
            InfoRow("Total task", formatMs(snapshot.duration(PerformanceEvent.TOTAL_TASK_MS)))
        }

        // --- Voz / ASR ---
        TraceCard(title = "Voz / ASR") {
            InfoRow("Texto reconocido", uiState.lastRecognizedText.ifBlank { "N/D" })
            InfoRow("Parcial actual", uiState.partialSpeechText.ifBlank { "N/D" })
            InfoRow("Error de voz", uiState.voiceError ?: "N/D")
        }

        // --- Ultima trazabilidad ---
        TraceCard(title = "Ultima trazabilidad") {
            Text(
                text = uiState.lastTraceText.ifBlank { "Todavia no hay trazabilidad" },
                style = MaterialTheme.typography.bodySmall,
                color = ErpColors.TextSecondary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun TraceCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ErpColors.Surface,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ErpColors.TextPrimary
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = ErpColors.TextMuted,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = ErpColors.TextPrimary,
            modifier = Modifier.weight(0.6f)
        )
    }
}

private fun formatMs(value: Long?): String = value?.let { "$it ms" } ?: "N/D"
