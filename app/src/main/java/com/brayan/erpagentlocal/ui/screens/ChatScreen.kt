package com.brayan.erpagentlocal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brayan.erpagentlocal.speech.AudioPermissionState
import com.brayan.erpagentlocal.ui.components.MessageBubble
import com.brayan.erpagentlocal.ui.theme.ErpColors

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onAudioPermissionResult(granted)
    }

    val modelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) viewModel.copyModel(uri)
    }

    fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(Unit) {
        if (hasAudioPermission()) {
            viewModel.syncAudioPermission(true)
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = ErpColors.Background,
        topBar = {
            ChatHeader(
                uiState = uiState,
                onClear = { viewModel.clearChat() },
                onPickModel = {
                    modelPickerLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                },
                onInitializeModel = { viewModel.initializeModel() },
                onToggleTechnical = { viewModel.toggleTechnicalMode() }
            )
        },
        bottomBar = {
            ChatInputBar(
                input = uiState.input,
                loading = uiState.loading,
                isRecording = uiState.isRecording,
                partialSpeechText = uiState.partialSpeechText,
                speechReady = uiState.speechReady,
                audioPermissionGranted = uiState.audioPermissionState == AudioPermissionState.GRANTED,
                onInputChange = { viewModel.updateInput(it) },
                onSend = { viewModel.sendMessage() },
                onRequestAudioPermission = {
                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                onActivateVoice = { viewModel.initializeSpeech() },
                onStartRecording = { viewModel.startRecording() },
                onStopRecording = { viewModel.stopRecording() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ErpColors.Background)
                .padding(paddingValues)
        ) {
            if (uiState.showDebugPanel) {
                // Modo Tecnico: vista de solo lectura. No altera el chat ni el agente.
                TechnicalTraceScreen(uiState = uiState)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.loading) {
                        item {
                            LoadingIndicator()
                        }
                    }

                    items(uiState.messages) { message ->
                        MessageBubble(message = message)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatHeader(
    uiState: ChatUiState,
    onClear: () -> Unit,
    onPickModel: () -> Unit,
    onInitializeModel: () -> Unit,
    onToggleTechnical: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ErpColors.Background,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "ERP Agent Local",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ErpColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(
                    onClick = onToggleTechnical
                ) {
                    Text(
                        text = if (uiState.showDebugPanel) "Chat" else "Técnico",
                        color = ErpColors.Primary
                    )
                }

                TextButton(
                    onClick = onClear
                ) {
                    Text(
                        text = "Limpiar",
                        color = ErpColors.Primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    label = if (uiState.modelReady) "Modelo ON" else "Modelo OFF",
                    tone = if (uiState.modelReady) ChipTone.SUCCESS else ChipTone.WARNING
                )

                val backendOnline = uiState.backendStatus.lowercase().contains("online")

                StatusChip(
                    label = if (backendOnline) "Backend ON" else "Backend OFF",
                    tone = if (backendOnline) ChipTone.SUCCESS else ChipTone.WARNING
                )

                StatusChip(
                    label = if (uiState.speechReady) "Voz ON" else "Voz OFF",
                    tone = if (uiState.speechReady) ChipTone.SUCCESS else ChipTone.WARNING
                )

                StatusChip(
                    label = "Tools ${uiState.toolsCount}",
                    tone = ChipTone.NEUTRAL
                )
            }

            ModelControls(
                modelReady = uiState.modelReady,
                modelName = uiState.modelName,
                loading = uiState.loading,
                isRecording = uiState.isRecording,
                onPickModel = onPickModel,
                onInitializeModel = onInitializeModel
            )
        }
    }
}

@Composable
private fun ModelControls(
    modelReady: Boolean,
    modelName: String,
    loading: Boolean,
    isRecording: Boolean,
    onPickModel: () -> Unit,
    onInitializeModel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ErpColors.Surface,
        shape = RoundedCornerShape(22.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (modelReady) {
                    "Modelo activo: ${modelName.ifBlank { "N/D" }}"
                } else {
                    "Selecciona e inicializa el modelo local"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (modelReady) ErpColors.Success else ErpColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = !loading && !isRecording,
                    onClick = onPickModel,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ErpColors.TextPrimary
                    )
                ) {
                    Text(
                        text = "Modelo",
                        maxLines = 1
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !loading && !isRecording,
                    onClick = onInitializeModel,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErpColors.Primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (modelReady) "Reiniciar" else "Iniciar",
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ErpColors.Surface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ErpColors.Primary,
                strokeWidth = 2.dp
            )

            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Procesando...",
                style = MaterialTheme.typography.bodySmall,
                color = ErpColors.TextMuted
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    input: String,
    loading: Boolean,
    isRecording: Boolean,
    partialSpeechText: String,
    speechReady: Boolean,
    audioPermissionGranted: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onRequestAudioPermission: () -> Unit,
    onActivateVoice: () -> Unit,
    onStartRecording: () -> Boolean,
    onStopRecording: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = ErpColors.Background,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isRecording || partialSpeechText.isNotBlank()) {
                RecordingPreview(
                    isRecording = isRecording,
                    partialSpeechText = partialSpeechText
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = "Escribe una instrucción...",
                            color = ErpColors.TextMuted
                        )
                    },
                    minLines = 1,
                    maxLines = 4,
                    enabled = !loading && !isRecording,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ErpColors.TextPrimary,
                        unfocusedTextColor = ErpColors.TextPrimary,
                        disabledTextColor = ErpColors.TextDisabled,
                        focusedContainerColor = ErpColors.InputBackground,
                        unfocusedContainerColor = ErpColors.InputBackground,
                        disabledContainerColor = ErpColors.InputBackground,
                        focusedBorderColor = ErpColors.Primary,
                        unfocusedBorderColor = ErpColors.Border,
                        disabledBorderColor = ErpColors.Border,
                        cursorColor = ErpColors.Primary
                    )
                )

                RoundSendMicButton(
                    input = input,
                    loading = loading,
                    isRecording = isRecording,
                    speechReady = speechReady,
                    audioPermissionGranted = audioPermissionGranted,
                    onSend = onSend,
                    onRequestAudioPermission = onRequestAudioPermission,
                    onActivateVoice = onActivateVoice,
                    onStartRecording = onStartRecording,
                    onStopRecording = onStopRecording
                )
            }
        }
    }
}

@Composable
private fun RecordingPreview(
    isRecording: Boolean,
    partialSpeechText: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ErpColors.ErrorSoft,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "●",
                color = ErpColors.Error,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                text = when {
                    partialSpeechText.isNotBlank() -> partialSpeechText
                    isRecording -> "Grabando... toca stop para enviar"
                    else -> "Procesando audio..."
                },
                style = MaterialTheme.typography.bodySmall,
                color = ErpColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RoundSendMicButton(
    input: String,
    loading: Boolean,
    isRecording: Boolean,
    speechReady: Boolean,
    audioPermissionGranted: Boolean,
    onSend: () -> Unit,
    onRequestAudioPermission: () -> Unit,
    onActivateVoice: () -> Unit,
    onStartRecording: () -> Boolean,
    onStopRecording: () -> Unit
) {
    val hasText = input.isNotBlank()

    val background = when {
        loading -> ErpColors.TextDisabled
        isRecording -> ErpColors.Error
        hasText -> ErpColors.Primary
        speechReady -> ErpColors.Success
        else -> ErpColors.Primary
    }

    val label = when {
        loading -> "…"
        isRecording -> "■"
        hasText -> "➤"
        else -> "🎙"
    }

    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable {
                if (loading) return@clickable

                when {
                    isRecording -> onStopRecording()
                    hasText -> onSend()
                    !audioPermissionGranted -> onRequestAudioPermission()
                    !speechReady -> onActivateVoice()
                    else -> onStartRecording()
                }
            },
        color = background,
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    tone: ChipTone
) {
    val background = when (tone) {
        ChipTone.SUCCESS -> ErpColors.SuccessSoft
        ChipTone.WARNING -> ErpColors.WarningSoft
        ChipTone.NEUTRAL -> ErpColors.Surface
    }

    val foreground = when (tone) {
        ChipTone.SUCCESS -> ErpColors.Success
        ChipTone.WARNING -> ErpColors.Warning
        ChipTone.NEUTRAL -> ErpColors.TextMuted
    }

    Text(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = foreground,
        maxLines = 1
    )
}

private enum class ChipTone {
    SUCCESS,
    WARNING,
    NEUTRAL
}