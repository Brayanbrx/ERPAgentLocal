package com.brayan.erpagentlocal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.ui.theme.ErpColors

data class ChatMessageUi(
    val role: String,
    val content: String
)

@Composable
fun MessageBubble(
    message: ChatMessageUi,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"
    val isSystem = message.role == "system"

    val backgroundColor = when {
        isUser -> ErpColors.UserBubble
        isSystem -> ErpColors.ToolBubble
        else -> ErpColors.AssistantBubble
    }

    val horizontalAlignment = if (isUser) {
        Alignment.End
    } else {
        Alignment.Start
    }

    val label = when {
        isUser -> "Tú"
        isSystem -> "Sistema"
        else -> "Agente"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ErpColors.TextMuted,
            modifier = Modifier.padding(
                start = 4.dp,
                end = 4.dp,
                bottom = 4.dp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.88f else 0.94f)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = ErpColors.TextPrimary
            )
        }
    }
}