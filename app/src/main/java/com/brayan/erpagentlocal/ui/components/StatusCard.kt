package com.brayan.erpagentlocal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.ui.theme.ErpColors

@Composable
fun StatusCard(
    modelReady: Boolean,
    backendStatus: String,
    toolsCount: Int,
    modelName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ErpColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "ERP Agent Local",
                        style = MaterialTheme.typography.titleMedium,
                        color = ErpColors.TextPrimary
                    )

                    Text(
                        text = "Agente reactivo conectado al ERP serverless",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErpColors.TextSecondary
                    )
                }

                StatusPill(
                    text = if (modelReady) "Modelo listo" else "Modelo no cargado",
                    positive = modelReady
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoBox(
                    title = "Backend",
                    value = backendStatus.ifBlank { "Sin verificar" },
                    modifier = Modifier.weight(1f)
                )

                InfoBox(
                    title = "Tools",
                    value = "$toolsCount",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (modelName.isBlank()) {
                    "Modelo: todavía no seleccionado"
                } else {
                    "Modelo: $modelName"
                },
                style = MaterialTheme.typography.bodySmall,
                color = ErpColors.TextMuted
            )
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    positive: Boolean
) {
    val background = if (positive) {
        ErpColors.SuccessSoft
    } else {
        ErpColors.WarningSoft
    }

    val foreground = if (positive) {
        ErpColors.Success
    } else {
        ErpColors.Warning
    }

    Row(
        modifier = Modifier
            .background(
                color = background,
                shape = CircleShape
            )
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = foreground,
                    shape = CircleShape
                )
                .padding(4.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = foreground
        )
    }
}

@Composable
private fun InfoBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = ErpColors.SurfaceSoft,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = ErpColors.TextMuted
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = ErpColors.TextPrimary
        )
    }
}