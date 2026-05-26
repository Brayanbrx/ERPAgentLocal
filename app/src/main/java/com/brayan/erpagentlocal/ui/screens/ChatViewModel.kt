package com.brayan.erpagentlocal.ui.screens

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brayan.erpagentlocal.agent.ToolCatalogLoader
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.ai.AgentService
import com.brayan.erpagentlocal.ai.LocalModelService
import com.brayan.erpagentlocal.speech.VoskSpeechService
import com.brayan.erpagentlocal.ui.components.ChatMessageUi
import com.brayan.erpagentlocal.util.ModelFileManager
import com.brayan.erpagentlocal.util.normalizeSpanishNumbers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessageUi> = listOf(
        ChatMessageUi(
            role = "assistant",
            content = """
                Hola. Soy tu agente ERP local.

                Puedo ayudarte con clientes, productos, compras, ventas e inventario usando lenguaje natural.

                Escribe una instrucción o toca el micrófono para dictarla.
            """.trimIndent()
        )
    ),
    val input: String = "",
    val loading: Boolean = false,
    val modelReady: Boolean = false,
    val modelName: String = "",
    val backendStatus: String = "Verificando...",
    val speechReady: Boolean = false,
    val speechStatus: String = "Voz no activada",
    val isRecording: Boolean = false,
    val partialSpeechText: String = "",
    val toolsCount: Int = 0
)

/**
 * Contiene toda la lógica de negocio de la pantalla de chat.
 * Los servicios (AgentService, LocalModelService, VoskSpeechService) viven aquí
 * para sobrevivir cambios de configuración (rotación, teclado).
 * El Composable solo lee el estado y delega los eventos del usuario.
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext

    private val agentService = AgentService()
    private val localModelService = LocalModelService()
    private val voskSpeechService = VoskSpeechService()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        val catalog = ToolCatalogLoader.loadFromAssets(appContext)
        ToolRegistry.setCatalog(catalog)
        _uiState.update { it.copy(toolsCount = catalog.count()) }

        viewModelScope.launch {
            val status = try { agentService.checkBackendStatus() } catch (_: Exception) { "Sin conexión" }
            _uiState.update { it.copy(backendStatus = status) }
        }
    }

    // ── Entrada de texto ──────────────────────────────────────────────────────

    fun updateInput(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    // ── Enviar mensaje de texto ───────────────────────────────────────────────

    fun sendMessage() {
        val cleanMessage = _uiState.value.input.trim()
        if (cleanMessage.isBlank()) return

        _uiState.update { it.copy(input = "") }

        runAction(userText = cleanMessage, showUserMessage = true) {
            when {
                cleanMessage.startsWith("/") || cleanMessage.startsWith("{") ->
                    agentService.processUserMessage(cleanMessage)

                localModelService.isInitialized() ->
                    agentService.processNaturalLanguageWithModel(
                        userMessage = cleanMessage,
                        localModelService = localModelService,
                        maxSteps = 8
                    )

                else ->
                    """
                    El modelo local no está inicializado.

                    Pasos:
                    1. Toca "Modelo" y selecciona tu archivo .litertlm.
                    2. Toca "Inicializar".
                    3. Vuelve a enviar tu instrucción.
                    """.trimIndent()
            }
        }
    }

    // ── Voz: llenar el campo de entrada sin enviar automáticamente ───────────

    private fun populateInputFromVoice(text: String) {
        val clean = normalizeSpanishNumbers(text.trim())
        if (clean.isBlank()) return
        _uiState.update { it.copy(input = clean, speechStatus = "Voz lista") }
    }

    // ── Ciclo de vida del modelo ──────────────────────────────────────────────

    fun initializeModel() {
        runAction(userText = "Inicializar modelo") {
            val modelFile = ModelFileManager.getDefaultModelFile(appContext)
            localModelService.initialize(context = appContext, modelFile = modelFile)
        }
    }

    fun copyModel(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true) }
                addMessage("user", "Seleccionar modelo")

                val file = ModelFileManager.copyModelToInternalStorage(
                    context = appContext,
                    uri = uri
                )

                refreshModelStatus()

                addMessage(
                    "assistant",
                    buildString {
                        appendLine("Modelo copiado correctamente.")
                        appendLine()
                        appendLine("Archivo: ${file.absolutePath}")
                        appendLine()
                        appendLine("Ahora presiona \"Inicializar\".")
                    }
                )
            } catch (exception: Exception) {
                addMessage("assistant", "ERROR AL COPIAR MODELO:\n${exception.message}")
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    // ── Ciclo de vida del reconocimiento de voz ───────────────────────────────

    fun initializeSpeech() {
        if (_uiState.value.speechReady) return

        _uiState.update { it.copy(speechStatus = "Preparando reconocimiento de voz...") }

        viewModelScope.launch {
            voskSpeechService.initialize(
                context = appContext,
                onReady = {
                    _uiState.update { it.copy(speechReady = true, speechStatus = "Voz lista") }
                    addMessage("assistant", "Reconocimiento de voz listo. Toca el micrófono para hablar.")
                },
                onError = { error ->
                    _uiState.update { it.copy(speechReady = false, speechStatus = "Error de voz") }
                    addMessage("assistant", error)
                }
            )
        }
    }

    fun startRecording() {
        val state = _uiState.value
        if (state.isRecording || !state.speechReady) return

        _uiState.update { it.copy(partialSpeechText = "", isRecording = true, speechStatus = "Escuchando...") }

        voskSpeechService.startListening(
            onPartialResult = { partial ->
                _uiState.update { it.copy(partialSpeechText = partial) }
            },
            onFinalResult = { finalText ->
                val clean = finalText.trim()
                _uiState.update { it.copy(isRecording = false, partialSpeechText = "") }
                if (clean.isNotBlank()) {
                    populateInputFromVoice(clean)
                } else {
                    _uiState.update { it.copy(speechStatus = "Voz lista") }
                    addMessage("assistant", "No pude reconocer el audio. Intenta hablar un poco más claro.")
                }
            },
            onError = { error ->
                _uiState.update { it.copy(isRecording = false, speechStatus = "Error de voz", partialSpeechText = "") }
                addMessage("assistant", error)
            }
        )
    }

    fun stopRecording() {
        if (!_uiState.value.isRecording) return
        _uiState.update { it.copy(speechStatus = "Procesando audio...") }
        voskSpeechService.stopListening()
    }

    fun toggleRecording() {
        if (_uiState.value.isRecording) stopRecording() else startRecording()
    }

    // ── Gestión del chat ──────────────────────────────────────────────────────

    fun clearChat() {
        if (_uiState.value.isRecording) {
            voskSpeechService.cancelListening()
        }

        _uiState.update { state ->
            state.copy(
                messages = listOf(
                    ChatMessageUi(
                        role = "assistant",
                        content = "Chat limpiado. Puedes escribir o grabar una nueva instrucción."
                    )
                ),
                input = "",
                partialSpeechText = "",
                isRecording = false
            )
        }

        viewModelScope.launch {
            try { agentService.processUserMessage("/clear") } catch (_: Exception) {}
        }
    }

    // ── Funciones internas auxiliares ─────────────────────────────────────────

    private fun runAction(
        userText: String,
        showUserMessage: Boolean = false,
        action: suspend () -> String
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true) }
                if (showUserMessage) addMessage("user", userText)

                val result = action()
                addMessage("assistant", result.ifBlank { "No recibí una respuesta válida." })
                refreshModelStatus()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                addMessage(
                    "assistant",
                    "Ocurrió un error, pero la app pudo recuperarse.\n" +
                        (throwable.message ?: throwable::class.java.simpleName)
                )
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    private fun addMessage(role: String, content: String) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessageUi(role = role, content = content)
            )
        }
    }

    private fun refreshModelStatus() {
        _uiState.update { state ->
            state.copy(
                modelReady = localModelService.isInitialized(),
                modelName = localModelService.getLoadedModelName().orEmpty()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        localModelService.close()
        voskSpeechService.release()
    }
}
