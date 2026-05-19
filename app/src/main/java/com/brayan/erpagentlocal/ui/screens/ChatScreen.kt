package com.brayan.erpagentlocal.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.brayan.erpagentlocal.agent.ToolCatalogLoader
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.ai.AgentService
import com.brayan.erpagentlocal.ai.LocalModelService
import com.brayan.erpagentlocal.ui.components.ChatMessageUi
import com.brayan.erpagentlocal.ui.components.MessageBubble
import com.brayan.erpagentlocal.ui.components.StatusCard
import com.brayan.erpagentlocal.ui.theme.ErpColors
import com.brayan.erpagentlocal.util.ModelFileManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

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

    val messages = remember {
        mutableStateListOf(
            ChatMessageUi(
                role = "assistant",
                content = """
                    Hola. Soy tu agente ERP local.

                    Puedo ayudarte a trabajar con clientes, productos, compras, ventas e inventario usando lenguaje natural.

                    Primero selecciona e inicializa el modelo. Luego puedes escribir algo como:
                    “Crea un cliente llamado Ana López”
                """.trimIndent()
            )
        )
    }

    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var backendStatus by remember { mutableStateOf("Sin verificar") }
    var modelName by remember { mutableStateOf(localModelService.getLoadedModelName().orEmpty()) }
    var modelReady by remember { mutableStateOf(localModelService.isInitialized()) }

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
                content = "Chat limpiado. Puedes escribir una nueva instrucción."
            )
        )

        input = ""
        backendStatus = "Sin verificar"

        scope.launch {
            try {
                agentService.processUserMessage("/clear")
            } catch (_: Exception) {
            }
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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            localModelService.close()
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
                            enabled = !loading,
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
                            enabled = !loading,
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
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = input,
                        onValueChange = { input = it },
                        label = {
                            Text("Escribe una instrucción para el agente")
                        },
                        placeholder = {
                            Text("Ej: Véndele 2 unidades de Café a Ana López")
                        },
                        minLines = 2,
                        maxLines = 5,
                        enabled = !loading
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            enabled = !loading && input.isNotBlank(),
                            onClick = {
                                input = ""
                            }
                        ) {
                            Text("Borrar")
                        }

                        Button(
                            modifier = Modifier.weight(2f),
                            enabled = !loading && input.isNotBlank(),
                            onClick = {
                                sendMessage(input)
                            }
                        ) {
                            Text("Enviar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}