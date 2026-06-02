package com.brayan.erpagentlocal.ui.screens

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brayan.erpagentlocal.agent.ToolCatalogLoader
import com.brayan.erpagentlocal.agent.ToolRegistry
import com.brayan.erpagentlocal.ai.ModelBackendMode
import com.brayan.erpagentlocal.di.AppServiceContainer
import com.brayan.erpagentlocal.metrics.PerformanceEvent
import com.brayan.erpagentlocal.metrics.PerformanceSnapshot
import com.brayan.erpagentlocal.metrics.PerformanceTracker
import com.brayan.erpagentlocal.speech.AudioPermissionState
import com.brayan.erpagentlocal.speech.SpeechTextNormalizer
import com.brayan.erpagentlocal.speech.VoiceMode
import com.brayan.erpagentlocal.trace.TraceRecord
import com.brayan.erpagentlocal.ui.components.ChatMessageUi
import com.brayan.erpagentlocal.util.ModelFileManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class ChatUiState(
    val messages: List<ChatMessageUi> = listOf(
        ChatMessageUi(
            role = "assistant",
            content = """
                Hola. Soy tu agente ERP local.

                Puedo ayudarte con clientes, productos, compras, ventas e inventario usando lenguaje natural.

                Escribe una instruccion o toca el microfono para dictarla.
            """.trimIndent()
        )
    ),
    val input: String = "",
    val loading: Boolean = false,
    val modelReady: Boolean = false,
    val modelName: String = "",
    val modelActiveBackend: String = "none",
    val modelBackendMode: String = ModelBackendMode.AUTO.name,
    val modelLoadMs: Long? = null,
    val modelWarmupMs: Long? = null,
    val lastGenerationMs: Long? = null,
    val modelLastError: String? = null,
    val backendStatus: String = "Verificando...",
    val backendLatencyMs: Long? = null,
    val audioPermissionState: AudioPermissionState = AudioPermissionState.UNKNOWN,
    val voiceMode: VoiceMode = VoiceMode.REVIEW,
    val autoSendVoice: Boolean = false,
    val speechReady: Boolean = false,
    val speechStatus: String = "Voz no activada",
    val speechLoadMs: Long? = null,
    val lastTranscriptionMs: Long? = null,
    val lastRecognizedText: String = "",
    val voiceError: String? = null,
    val isRecording: Boolean = false,
    val partialSpeechText: String = "",
    val toolsCount: Int = 0,
    val toolsSource: String = "tools.json",
    val showDebugPanel: Boolean = false,
    val lastTraceText: String = "",
    val performanceSnapshot: PerformanceSnapshot = PerformanceTracker.getSnapshot()
)

/**
 * Contiene toda la logica de negocio de la pantalla de chat.
 * Los servicios viven aqui para sobrevivir cambios de configuracion.
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val services = AppServiceContainer.get(application)

    private val agentService = services.agentService
    private val localModelService = services.localModelService
    private val voskSpeechService = services.voskSpeechService
    private val speechTextNormalizer = SpeechTextNormalizer()
    private val backendHealthMonitor = services.backendHealthMonitor
    private val modelProvisioningManager = services.modelProvisioningManager
    private val metricsStore = services.metricsStore
    private val traceStore = services.traceStore

    private var selectedModelFile: File? = null
    private var recordingStartedAtMs: Long = 0L

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        PerformanceTracker.markAppStarted()

        val catalog = ToolCatalogLoader.loadFromAssets(appContext)
        ToolRegistry.setCatalog(catalog)
        _uiState.update {
            it.copy(
                toolsCount = catalog.count(),
                toolsSource = catalog.source,
                showDebugPanel = false,
                voiceMode = VoiceMode.AUTO_SEND,
                autoSendVoice = true
            )
        }

        viewModelScope.launch {
            PerformanceTracker.snapshot.collect { snapshot ->
                _uiState.update {
                    it.copy(
                        performanceSnapshot = snapshot,
                        speechLoadMs = snapshot.duration(PerformanceEvent.VOSK_LOAD_MS)
                    )
                }
            }
        }

        refreshBackendHealth()
        autoInitializeModelOnStartup()
    }

    /**
     * Al abrir la app: si el modelo ya esta cargado (p. ej. tras una rotacion) solo refresca el
     * estado; si hay un modelo valido ya copiado/funcional pero sin cargar, lo inicializa solo
     * reutilizando el flujo manual. Si no hay ningun modelo, no hace nada: el usuario usa los
     * botones "Modelo" e "Inicializar" como siempre.
     */
    private fun autoInitializeModelOnStartup() {
        if (localModelService.isInitialized()) {
            refreshModelStatus()
            return
        }

        val preferredModel = modelProvisioningManager.getPreferredModelFile()
        if (!modelProvisioningManager.validateModelFile(preferredModel).valid) {
            return
        }

        initializeModel()
    }

    fun updateInput(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun syncAudioPermission(granted: Boolean) {
        if (granted) {
            _uiState.update {
                it.copy(
                    audioPermissionState = AudioPermissionState.GRANTED,
                    voiceError = null
                )
            }
            initializeSpeech()
        } else {
            _uiState.update {
                it.copy(
                    audioPermissionState = AudioPermissionState.DENIED,
                    speechReady = false,
                    speechStatus = "Modo texto disponible",
                    voiceError = "Permiso de microfono denegado"
                )
            }
        }
    }

    fun onAudioPermissionResult(granted: Boolean) {
        syncAudioPermission(granted)
    }

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
                        localModelService = localModelService
                    )

                else ->
                    """
                    El modelo local no esta inicializado.

                    Pasos:
                    1. Toca "Modelo" y selecciona tu archivo .litertlm.
                    2. Toca "Inicializar".
                    3. Vuelve a enviar tu instruccion.
                    """.trimIndent()
            }
        }
    }

    fun initializeModel() {
        runAction(userText = "Inicializar modelo") {
            val modelFile = selectedModelFile ?: modelProvisioningManager.getPreferredModelFile()
            val validation = modelProvisioningManager.validateModelFile(modelFile)

            if (!validation.valid) {
                return@runAction validation.message
            }

            val initResult = localModelService.initialize(
                context = appContext,
                modelFile = modelFile,
                requestedBackendMode = ModelBackendMode.AUTO
            )

            if (!localModelService.isInitialized()) {
                val fallback = modelProvisioningManager.getLastFunctionalModelFile()
                if (fallback != null && fallback.absolutePath != modelFile.absolutePath) {
                    val fallbackResult = localModelService.initialize(
                        context = appContext,
                        modelFile = fallback,
                        requestedBackendMode = ModelBackendMode.AUTO
                    )
                    if (localModelService.isInitialized()) {
                        selectedModelFile = fallback
                        refreshModelStatus()
                        return@runAction buildString {
                            appendLine(initResult)
                            appendLine()
                            appendLine("Se volvio al ultimo modelo funcional:")
                            appendLine(fallback.absolutePath)
                            appendLine(fallbackResult)
                        }
                    }
                }
                refreshModelStatus()
                return@runAction initResult
            }

            var warmupSucceeded = true
            val warmupResult = try {
                localModelService.warmUp()
            } catch (exception: Exception) {
                warmupSucceeded = false
                "Warmup fallido: ${exception.message ?: "error desconocido"}"
            }

            if (warmupSucceeded) {
                modelProvisioningManager.markModelFunctional(modelFile)
            }
            selectedModelFile = modelFile
            refreshModelStatus()

            buildString {
                appendLine(initResult)
                appendLine()
                appendLine("Warmup:")
                appendLine(warmupResult)
            }
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
                selectedModelFile = file

                val validation = modelProvisioningManager.validateModelFile(file)
                refreshModelStatus()

                addMessage(
                    "assistant",
                    buildString {
                        appendLine("Modelo copiado correctamente.")
                        appendLine()
                        appendLine("Archivo: ${file.absolutePath}")
                        appendLine("Validacion: ${validation.message}")
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

    fun initializeSpeech() {
        val state = _uiState.value
        if (state.speechReady) return
        if (state.audioPermissionState == AudioPermissionState.DENIED) {
            _uiState.update {
                it.copy(
                    speechStatus = "Modo texto disponible",
                    voiceError = "Permiso de microfono denegado"
                )
            }
            return
        }

        _uiState.update { it.copy(speechStatus = "Preparando reconocimiento de voz...", voiceError = null) }

        viewModelScope.launch {
            voskSpeechService.initialize(
                context = appContext,
                onReady = {
                    _uiState.update {
                        it.copy(
                            speechReady = true,
                            speechStatus = "Voz lista",
                            voiceError = null,
                            speechLoadMs = PerformanceTracker.getSnapshot().duration(PerformanceEvent.VOSK_LOAD_MS)
                        )
                    }
                    addMessage("assistant", "Reconocimiento de voz listo. Toca el microfono para hablar.")
                },
                onError = { error ->
                    _uiState.update {
                        it.copy(
                            speechReady = false,
                            speechStatus = "Error de voz",
                            voiceError = error
                        )
                    }
                    addMessage("assistant", error)
                }
            )
        }
    }

    fun startRecording(): Boolean {
        val state = _uiState.value
        if (state.isRecording) return false
        if (!state.speechReady) {
            _uiState.update {
                it.copy(
                    speechStatus = "Voz no lista",
                    voiceError = "El reconocimiento de voz todavia no esta listo."
                )
            }
            return false
        }

        recordingStartedAtMs = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                partialSpeechText = "",
                isRecording = true,
                speechStatus = "Escuchando...",
                voiceError = null
            )
        }

        voskSpeechService.startListening(
            onPartialResult = { partial ->
                _uiState.update { it.copy(partialSpeechText = partial) }
            },
            onFinalResult = { finalText ->
                handleFinalVoiceText(finalText)
            },
            onError = { error ->
                _uiState.update {
                    it.copy(
                        isRecording = false,
                        speechStatus = "Error de voz",
                        partialSpeechText = "",
                        voiceError = error
                    )
                }
                addMessage("assistant", error)
            }
        )

        return true
    }

    fun stopRecording() {
        if (!_uiState.value.isRecording) return
        _uiState.update { it.copy(speechStatus = "Procesando audio...") }
        voskSpeechService.stopListening()
    }

    fun clearChat() {
        if (_uiState.value.isRecording) {
            voskSpeechService.cancelListening()
        }

        _uiState.update { state ->
            state.copy(
                messages = listOf(
                    ChatMessageUi(
                        role = "assistant",
                        content = "Chat limpiado. Puedes escribir o grabar una nueva instruccion."
                    )
                ),
                input = "",
                partialSpeechText = "",
                isRecording = false
            )
        }

        viewModelScope.launch {
            try { agentService.processUserMessage("/clear") } catch (_: Exception) {}
            metricsStore.clear()
            traceStore.clear()
        }
    }

    private fun handleFinalVoiceText(finalText: String) {
    val normalization = speechTextNormalizer.normalize(finalText)
    val transcriptionMs = (System.currentTimeMillis() - recordingStartedAtMs).coerceAtLeast(0L)

    _uiState.update {
        it.copy(
            isRecording = false,
            partialSpeechText = "",
            input = "",
            lastTranscriptionMs = transcriptionMs,
            lastRecognizedText = normalization.originalText,
            voiceError = null,
            speechStatus = "Voz lista"
        )
    }

    if (normalization.isEmpty) {
        _uiState.update {
            it.copy(
                speechStatus = "Voz lista",
                voiceError = "No se reconocio texto util."
            )
        }
        addMessage("assistant", "No pude reconocer el audio. Intenta hablar un poco mas claro.")
        return
    }

    val clean = normalization.normalizedText.trim()

    if (clean.isBlank()) {
        addMessage("assistant", "No pude reconocer una instruccion clara.")
        return
    }

    runAction(userText = clean, showUserMessage = true) {
        _uiState.update {
            it.copy(
                input = "",
                partialSpeechText = "",
                speechStatus = "Voz lista"
            )
        }

        when {
            localModelService.isInitialized() ->
                agentService.processNaturalLanguageWithModel(
                    userMessage = clean,
                    localModelService = localModelService
                )

            else ->
                """
                Reconoci: $clean

                El modelo local no esta inicializado.

                Pasos:
                1. Toca "Modelo" y selecciona tu archivo .litertlm.
                2. Toca "Iniciar".
                3. Vuelve a grabar o escribir tu instruccion.
                """.trimIndent()
        }
    }
}

    private fun refreshBackendHealth() {
        viewModelScope.launch {
            val snapshot = try {
                backendHealthMonitor.checkHealth()
            } catch (_: Exception) {
                null
            }

            _uiState.update { state ->
                if (snapshot == null) {
                    state.copy(backendStatus = "Backend sin respuesta", backendLatencyMs = null)
                } else {
                    state.copy(
                        backendStatus = snapshot.toStatusText(),
                        backendLatencyMs = snapshot.latencyMs
                    )
                }
            }
        }
    }

    private fun runAction(
        userText: String,
        showUserMessage: Boolean = false,
        action: suspend () -> String
    ) {
        viewModelScope.launch {
            try {
                PerformanceTracker.start(PerformanceEvent.TOTAL_TASK_MS)
                _uiState.update { it.copy(loading = true) }
                if (showUserMessage) addMessage("user", userText)

                val result = action()
                addMessage("assistant", result.ifBlank { "No recibi una respuesta valida." })
                refreshModelStatus()
                updateBasicTrace(
                    userText = userText,
                    result = result,
                    source = if (userText.startsWith("/")) "debug" else "chat"
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                addMessage(
                    "assistant",
                    "Ocurrio un error, pero la app pudo recuperarse.\n" +
                        (throwable.message ?: throwable::class.java.simpleName)
                )
            } finally {
                PerformanceTracker.finish(PerformanceEvent.TOTAL_TASK_MS)
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    private fun updateBasicTrace(
        userText: String,
        result: String,
        source: String
    ) {
        val snapshot = PerformanceTracker.getSnapshot()
        val queueTrace = agentService.getLastExecutionTraceText()
        _uiState.update { state ->
            val traceText = buildTraceText(
                state = state,
                snapshot = snapshot,
                userText = userText,
                result = result,
                source = source,
                queueTrace = queueTrace
            )
            persistObservability(
                state = state,
                snapshot = snapshot,
                userText = userText,
                result = result,
                source = source
            )
            state.copy(
                lastTraceText = traceText
            )
        }
    }

    private fun buildTraceText(
        state: ChatUiState,
        snapshot: PerformanceSnapshot,
        userText: String,
        result: String,
        source: String,
        queueTrace: String
    ): String {
        val toolSummary = agentService.getLastToolTraceSummary()
        return buildString {
            if (queueTrace.isNotBlank()) {
                appendLine(queueTrace)
                appendLine()
            }
            appendLine("TRAZABILIDAD DETALLADA")
            appendLine("Fuente: $source")
            appendLine("Texto original: $userText")
            appendLine("Texto transcrito: ${state.lastRecognizedText.ifBlank { "N/D" }}")
            appendLine("Texto normalizado: $userText")
            appendLine("Prompt/resumen usado: ${if (queueTrace.isNotBlank()) "cola reactiva" else "prompt de decision local"}")
            appendLine("Intencion entendida: ${inferIntent(userText, result)}")
            appendLine("Tool solicitada:")
            appendLine(toolSummary)
            appendLine("Resultado de validacion: AgentExecutor.validateReady antes de ejecucion")
            appendLine("Endpoint llamado: ${agentService.getLastEndpointTraceText()}")
            appendLine("Respuesta Lambda: ${toolSummary.take(800)}")
            appendLine("Estado actualizado:")
            appendLine(agentService.getStateText().take(1200))
            appendLine("Modelo activo: ${state.modelName.ifBlank { "N/D" }}")
            appendLine("Backend activo: ${state.modelActiveBackend}")
            appendLine("Tools cargadas: ${state.toolsCount} (${state.toolsSource})")
            appendLine("Ultimo error: ${state.voiceError ?: "N/D"}")
            appendLine("Tiempo total: ${snapshot.duration(PerformanceEvent.TOTAL_TASK_MS) ?: "N/D"} ms")
            appendLine("Generation ms: ${snapshot.duration(PerformanceEvent.GENERATION_MS) ?: "N/D"}")
            appendLine("Lambda ms: ${snapshot.duration(PerformanceEvent.LAMBDA_LATENCY_MS) ?: "N/D"}")
            appendLine()
            appendLine("Resultado final:")
            appendLine(result.take(1200))
        }.trim()
    }

    private fun persistObservability(
        state: ChatUiState,
        snapshot: PerformanceSnapshot,
        userText: String,
        result: String,
        source: String
    ) {
        val toolSummary = agentService.getLastToolTraceSummary()
        metricsStore.saveSnapshot(label = source, snapshot = snapshot)
        traceStore.save(
            TraceRecord(
                originalText = userText,
                transcribedText = state.lastRecognizedText,
                normalizedText = userText,
                promptSummary = if (agentService.getLastExecutionTraceText().isNotBlank()) {
                    "cola reactiva"
                } else {
                    "prompt de decision local"
                },
                understoodIntent = inferIntent(userText, result),
                requestedTool = toolSummary,
                argumentsText = toolSummary,
                validationResult = "AgentExecutor.validateReady",
                endpointCalled = agentService.getLastEndpointTraceText(),
                lambdaResponse = toolSummary,
                updatedState = agentService.getStateText(),
                totalMs = snapshot.duration(PerformanceEvent.TOTAL_TASK_MS),
                modelName = state.modelName,
                activeBackend = state.modelActiveBackend,
                toolsExecuted = toolSummary,
                result = result,
                error = state.voiceError
            )
        )
    }

    private fun inferIntent(userText: String, result: String): String {
        val text = "$userText $result".lowercase()
        return when {
            "cliente" in text && ("crea" in text || "creado" in text) -> "crear cliente"
            "producto" in text && ("crea" in text || "creado" in text) -> "crear producto"
            "compra" in text -> "registrar compra"
            "venta" in text || "vende" in text -> "registrar venta"
            "inventario" in text || "stock" in text -> "consultar inventario"
            "delete" in text || "elimina" in text -> "accion destructiva"
            else -> "instruccion ERP"
        }
    }

    private fun addMessage(role: String, content: String) {
        val visualRole = classifyMessageRole(role, content)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessageUi(role = visualRole, content = content)
            )
        }
    }

    private fun classifyMessageRole(role: String, content: String): String {
        if (role != "assistant") return role
        val normalized = content.lowercase()
        return when {
            "confirmacion pendiente" in normalized -> "confirmation"
            normalized.startsWith("error") ||
                "ocurrio un error" in normalized ||
                "fallido" in normalized -> "error"
            "accion bloqueada" in normalized ||
                "bloqueada por politica" in normalized -> "warning"
            "trazabilidad" in normalized ||
                "tool:" in normalized ||
                "tools cargadas" in normalized -> "tool-trace"
            "demo mode" in normalized ||
                "memoria" in normalized && "estado" in normalized -> "system"
            else -> "assistant"
        }
    }

    private fun refreshModelStatus() {
        val status = localModelService.getModelStatus()
        _uiState.update { state ->
            state.copy(
                modelReady = status.initialized,
                modelName = status.modelName.orEmpty(),
                modelActiveBackend = status.activeBackend,
                modelBackendMode = status.backendMode.name,
                modelLoadMs = status.modelLoadMs,
                modelWarmupMs = status.warmupMs,
                lastGenerationMs = status.lastGenerationMs,
                modelLastError = status.lastError
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (_uiState.value.isRecording) {
            voskSpeechService.cancelListening()
        }
        // Servicios pesados viven en AppServiceContainer para sobrevivir recreaciones de pantalla.
    }
}
