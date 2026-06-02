package com.brayan.erpagentlocal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val isError = message.role == "error"
    val isWarning = message.role == "warning"
    val isConfirmation = message.role == "confirmation"
    val isSystem = message.role == "system" || message.role == "tool-trace"

    val backgroundColor = when {
        isUser -> ErpColors.UserBubble
        isError -> ErpColors.ErrorSoft
        isWarning || isConfirmation -> ErpColors.WarningSoft
        isSystem -> ErpColors.ToolBubble
        else -> ErpColors.AssistantBubble
    }

    val textColor = when {
        isUser -> Color.White
        isError -> Color(0xFFFFB4B4)
        isWarning || isConfirmation -> Color(0xFFFFD98A)
        else -> ErpColors.TextPrimary
    }

    val horizontalAlignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isUser) 54.dp else 0.dp,
                    end = if (isUser) 0.dp else 54.dp
                ),
            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 6.dp,
                            bottomEnd = if (isUser) 6.dp else 20.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }
}