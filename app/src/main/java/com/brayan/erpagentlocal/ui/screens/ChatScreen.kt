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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.brayan.erpagentlocal.agent.ToolCatalogLoader
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.ai.AgentService
import com.brayan.erpagentlocal.ai.LocalModelService
import com.brayan.erpagentlocal.speech.VoskSpeechService
import com.brayan.erpagentlocal.ui.components.ChatMessageUi
import com.brayan.erpagentlocal.ui.components.MessageBubble
import com.brayan.erpagentlocal.ui.components.StatusCard
import com.brayan.erpagentlocal.ui.theme.ErpColors
import com.brayan.erpagentlocal.util.ModelFileManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val loadedCatalog = remember {
        val catalog = ToolCatalogLoader.loadFromAssets(context)
        ToolRegistry.setCatalog(catalog)
        catalog
    }

    val agentService = remember { AgentService() }
    val localModelService = remember { LocalModelService() }
    val voskSpeechService = remember { VoskSpeechService() }

    val messages = remember {
        mutableStateListOf(
            ChatMessageUi(
                role = "assistant",
                content = """
                    Hola. Soy tu agente ERP local.

                    Puedo ayudarte a trabajar con clientes, productos, compras, ventas e inventario usando lenguaje natural.

                    Puedes escribir una instrucción o tocar el micrófono para dictarla.
                """.trimIndent()
            )
        )
    }

    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var backendStatus by remember { mutableStateOf("Sin verificar") }
    var modelName by remember { mutableStateOf(localModelService.getLoadedModelName().orEmpty()) }
    var modelReady by remember { mutableStateOf(localModelService.isInitialized()) }

    var speechReady by remember { mutableStateOf(false) }
    var speechStatus by remember { mutableStateOf("Voz no activada") }
    var isRecording by remember { mutableStateOf(false) }
    var partialSpeechText by remember { mutableStateOf("") }

    fun refreshModelStatus() {
        modelReady = localModelService.isInitialized()
        modelName = localModelService.getLoadedModelName().orEmpty()
    }

    fun addMessage(role: String, content: String) {
        messages.add(
            ChatMessageUi(
                role = role,
                content = content
            )
        )
    }

    fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun runAction(
        userText: String,
        showUserMessage: Boolean = true,
        action: suspend () -> String
    ) {
        scope.launch {
            try {
                loading = true

                if (showUserMessage) {
                    addMessage("user", userText)
                }

                val result = action()

                addMessage(
                    role = "assistant",
                    content = result.ifBlank {
                        "No recibí una respuesta válida."
                    }
                )

                refreshModelStatus()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                addMessage(
                    role = "assistant",
                    content = """
                        Ocurrió un error, pero la app pudo recuperarse.

                        Detalle:
                        ${throwable.message ?: throwable::class.java.simpleName}
                    """.trimIndent()
                )
            } finally {
                loading = false
            }
        }
    }

    fun sendMessage(messageToSend: String) {
        val cleanMessage = messageToSend.trim()

        if (cleanMessage.isBlank()) {
            return
        }

        input = ""

        runAction(cleanMessage) {
            when {
                cleanMessage.startsWith("/") -> {
                    agentService.processUserMessage(cleanMessage)
                }

                cleanMessage.startsWith("{") -> {
                    agentService.processUserMessage(cleanMessage)
                }

                localModelService.isInitialized() -> {
                    agentService.processNaturalLanguageWithModel(
                        userMessage = cleanMessage,
                        localModelService = localModelService,
                        maxSteps = 8
                    )
                }

                else -> {
                    """
                    El modelo local no está inicializado.

                    Pasos:
                    1. Presiona “Modelo”.
                    2. Selecciona tu archivo .litertlm.
                    3. Presiona “Inicializar”.
                    4. Vuelve a enviar tu instrucción.
                    """.trimIndent()
                }
            }
        }
    }

    fun clearChatAndAgent() {
        messages.clear()
        messages.add(
            ChatMessageUi(
                role = "assistant",
                content = "Chat limpiado. Puedes escribir o grabar una nueva instrucción."
            )
        )

        input = ""
        partialSpeechText = ""
        backendStatus = "Sin verificar"

        if (isRecording) {
            voskSpeechService.cancelListening()
            isRecording = false
        }

        scope.launch {
            try {
                agentService.processUserMessage("/clear")
            } catch (_: Exception) {
            }
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            speechStatus = "Preparando reconocimiento de voz..."

            scope.launch {
                voskSpeechService.initialize(
                    context = context,
                    onReady = {
                        scope.launch {
                            speechReady = true
                            speechStatus = "Voz lista"
                            addMessage("assistant", "Reconocimiento de voz listo. Toca el micrófono para hablar.")
                        }
                    },
                    onError = { error ->
                        scope.launch {
                            speechReady = false
                            speechStatus = "Error de voz"
                            addMessage("assistant", error)
                        }
                    }
                )
            }
        } else {
            speechReady = false
            speechStatus = "Permiso de micrófono denegado"
            addMessage("assistant", "Necesito permiso de micrófono para grabar audio.")
        }
    }

    fun prepareSpeechIfNeeded() {
        if (!hasAudioPermission()) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (speechReady) {
            return
        }

        speechStatus = "Preparando reconocimiento de voz..."

        scope.launch {
            voskSpeechService.initialize(
                context = context,
                onReady = {
                    scope.launch {
                        speechReady = true
                        speechStatus = "Voz lista"
                        addMessage("assistant", "Reconocimiento de voz listo. Toca el micrófono para hablar.")
                    }
                },
                onError = { error ->
                    scope.launch {
                        speechReady = false
                        speechStatus = "Error de voz"
                        addMessage("assistant", error)
                    }
                }
            )
        }
    }

    fun startRecording() {
        if (!hasAudioPermission()) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (!speechReady) {
            prepareSpeechIfNeeded()
            return
        }

        if (isRecording) {
            return
        }

        partialSpeechText = ""
        isRecording = true
        speechStatus = "Escuchando..."

        voskSpeechService.startListening(
            onPartialResult = { partial ->
                scope.launch {
                    partialSpeechText = partial
                }
            },
            onFinalResult = { finalText ->
                scope.launch {
                    val cleanText = finalText.trim()

                    isRecording = false
                    speechStatus = "Voz lista"
                    partialSpeechText = ""

                    if (cleanText.isNotBlank()) {
                        input = cleanText
                        sendMessage(cleanText)
                    } else {
                        addMessage("assistant", "No pude reconocer el audio. Intenta hablar un poco más claro.")
                    }
                }
            },
            onError = { error ->
                scope.launch {
                    isRecording = false
                    speechStatus = "Error de voz"
                    partialSpeechText = ""
                    addMessage("assistant", error)
                }
            }
        )
    }

    fun stopRecording() {
        if (!isRecording) {
            return
        }

        speechStatus = "Procesando audio..."
        voskSpeechService.stopListening()
    }

    fun toggleRecording() {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    val modelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            addMessage("assistant", "No se seleccionó ningún modelo.")
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            try {
                loading = true
                addMessage("user", "Seleccionar modelo")

                val file = ModelFileManager.copyModelToInternalStorage(
                    context = context,
                    uri = uri
                )

                refreshModelStatus()

                addMessage(
                    role = "assistant",
                    content = buildString {
                        appendLine("Modelo copiado correctamente.")
                        appendLine()
                        appendLine("Archivo:")
                        appendLine(file.absolutePath)
                        appendLine()
                        appendLine("Ahora presiona “Inicializar”.")
                    }
                )
            } catch (exception: Exception) {
                addMessage(
                    role = "assistant",
                    content = "ERROR AL COPIAR MODELO:\n${exception.message}"
                )
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        prepareSpeechIfNeeded()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            localModelService.close()
            voskSpeechService.release()
        }
    }

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
                            text = speechStatus,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (speechReady) {
                                ErpColors.Success
                            } else {
                                ErpColors.TextMuted
                            }
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

            StatusCard(
                modelReady = modelReady,
                backendStatus = backendStatus,
                toolsCount = loadedCatalog.count(),
                modelName = modelName
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ErpColors.Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
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
                            enabled = !loading && !isRecording,
                            onClick = {
                                modelPickerLauncher.launch(
                                    arrayOf(
                                        "application/octet-stream",
                                        "*/*"
                                    )
                                )
                            }
                        ) {
                            Text("Modelo")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = !loading && !isRecording,
                            onClick = {
                                runAction("Inicializar modelo") {
                                    val modelFile = ModelFileManager.getDefaultModelFile(context)

                                    localModelService.initialize(
                                        context = context,
                                        modelFile = modelFile
                                    )
                                }
                            }
                        ) {
                            Text("Inicializar")
                        }

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            enabled = !loading,
                            onClick = {
                                clearChatAndAgent()
                            }
                        ) {
                            Text("Limpiar")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (speechReady) ErpColors.SuccessSoft else ErpColors.WarningSoft,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                text = if (speechReady) "Voz local lista" else "Voz pendiente",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (speechReady) ErpColors.Success else ErpColors.Warning
                            )
                        }

                        TextButton(
                            enabled = !loading && !isRecording,
                            onClick = {
                                prepareSpeechIfNeeded()
                            }
                        ) {
                            Text("Activar voz")
                        }
                    }
                }
            }

            if (loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ErpColors.Surface,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isRecording || partialSpeechText.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = ErpColors.ErrorSoft,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(12.dp),
                                text = if (partialSpeechText.isBlank()) {
                                    "Escuchando..."
                                } else {
                                    "Escuchando: $partialSpeechText"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = ErpColors.Error
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = input,
                            onValueChange = { input = it },
                            placeholder = {
                                Text("Escribe o graba una instrucción")
                            },
                            minLines = 1,
                            maxLines = 4,
                            enabled = !loading && !isRecording,
                            shape = RoundedCornerShape(24.dp)
                        )

                        IconButton(
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    color = when {
                                        isRecording -> ErpColors.Error
                                        input.isNotBlank() -> ErpColors.Primary
                                        else -> ErpColors.Primary
                                    },
                                    shape = CircleShape
                                ),
                            enabled = !loading,
                            onClick = {
                                if (input.isNotBlank() && !isRecording) {
                                    sendMessage(input)
                                } else {
                                    toggleRecording()
                                }
                            }
                        ) {
                            Text(
                                text = when {
                                    isRecording -> "■"
                                    input.isNotBlank() -> "➤"
                                    else -> "🎙"
                                },
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            enabled = !loading && input.isNotBlank() && !isRecording,
                            onClick = {
                                input = ""
                            }
                        ) {
                            Text("Borrar")
                        }

                        Text(
                            text = when {
                                isRecording -> "Toca ■ para detener"
                                input.isNotBlank() -> "Toca ➤ para enviar"
                                speechReady -> "Toca 🎙 para hablar"
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