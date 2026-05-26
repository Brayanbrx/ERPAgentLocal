package com.brayan.erpagentlocal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brayan.erpagentlocal.ui.components.MessageBubble
import com.brayan.erpagentlocal.ui.components.StatusCard
import com.brayan.erpagentlocal.ui.theme.ErpColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // ── Lanzador de permisos ─────────────────────────────────────────────────
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.initializeSpeech()
        }
    }

    fun hasAudioPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

    fun onMicrophoneTap() {
        if (!hasAudioPermission()) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        if (!uiState.speechReady) {
            viewModel.initializeSpeech()
            return
        }
        viewModel.toggleRecording()
    }

    // ── Selector de archivo de modelo ────────────────────────────────────────
    val modelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.copyModel(uri)
        }
    }

    // ── Desplazamiento automático al recibir mensaje ─────────────────────────
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    // ── Inicializar voz al arrancar (si ya tiene permiso) ────────────────────
    LaunchedEffect(Unit) {
        if (hasAudioPermission() && !uiState.speechReady) {
            viewModel.initializeSpeech()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Interfaz de usuario
    // ─────────────────────────────────────────────────────────────────────────

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ERP Agent Local",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = uiState.speechStatus,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.speechReady) ErpColors.Success else ErpColors.TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ErpColors.Surface,
                    titleContentColor = ErpColors.TextPrimary
                )
            )
        },
        containerColor = ErpColors.SurfaceSoft
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .imePadding()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── Tarjeta de estado ─────────────────────────────────────────────
            StatusCard(
                modelReady = uiState.modelReady,
                backendStatus = uiState.backendStatus,
                toolsCount = uiState.toolsCount,
                modelName = uiState.modelName
            )

            // ── Botones de acción ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ErpColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Acciones principales",
                        style = MaterialTheme.typography.titleSmall,
                        color = ErpColors.TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.loading && !uiState.isRecording,
                            onClick = {
                                modelPickerLauncher.launch(
                                    arrayOf("application/octet-stream", "*/*")
                                )
                            }
                        ) { Text("Modelo") }

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.loading && !uiState.isRecording,
                            onClick = { viewModel.initializeModel() }
                        ) { Text("Inicializar") }

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.loading,
                            onClick = { viewModel.clearChat() }
                        ) { Text("Limpiar") }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (uiState.speechReady) ErpColors.SuccessSoft else ErpColors.WarningSoft,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                text = if (uiState.speechReady) "Voz local lista" else "Voz pendiente",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (uiState.speechReady) ErpColors.Success else ErpColors.Warning
                            )
                        }

                        TextButton(
                            enabled = !uiState.loading && !uiState.isRecording,
                            onClick = {
                                if (!hasAudioPermission()) {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    viewModel.initializeSpeech()
                                }
                            }
                        ) { Text("Activar voz") }
                    }
                }
            }

            // ── Indicador de carga ────────────────────────────────────────────
            if (uiState.loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // ── Lista de mensajes ─────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.messages) { message ->
                    MessageBubble(message = message)
                }
            }

            // ── Área de entrada ───────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ErpColors.Surface,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Indicador de grabación
                    if (uiState.isRecording || uiState.partialSpeechText.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = ErpColors.ErrorSoft,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(12.dp),
                                text = if (uiState.partialSpeechText.isBlank()) {
                                    "Escuchando..."
                                } else {
                                    "Escuchando: ${uiState.partialSpeechText}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = ErpColors.Error
                            )
                        }
                    }

                    // Campo de texto + botón de enviar/micrófono
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = uiState.input,
                            onValueChange = { viewModel.updateInput(it) },
                            placeholder = { Text("Escribe o graba una instrucción") },
                            minLines = 1,
                            maxLines = 4,
                            enabled = !uiState.loading && !uiState.isRecording,
                            shape = RoundedCornerShape(24.dp)
                        )

                        IconButton(
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    color = if (uiState.isRecording) ErpColors.Error else ErpColors.Primary,
                                    shape = CircleShape
                                ),
                            enabled = !uiState.loading,
                            onClick = {
                                if (uiState.input.isNotBlank() && !uiState.isRecording) {
                                    viewModel.sendMessage()
                                } else {
                                    onMicrophoneTap()
                                }
                            }
                        ) {
                            Text(
                                text = when {
                                    uiState.isRecording -> "■"
                                    uiState.input.isNotBlank() -> "➤"
                                    else -> "🎙"
                                },
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // Texto de ayuda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            enabled = !uiState.loading && uiState.input.isNotBlank() && !uiState.isRecording,
                            onClick = { viewModel.updateInput("") }
                        ) { Text("Borrar") }

                        Text(
                            text = when {
                                uiState.isRecording -> "Toca ■ para detener"
                                uiState.input.isNotBlank() -> "Toca ➤ para enviar"
                                uiState.speechReady -> "Toca 🎙 para hablar"
                                else -> "Activa voz para dictar"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = ErpColors.TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
