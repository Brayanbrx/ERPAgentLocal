package com.brayan.erpagentlocal.ai

import android.content.Context
import com.brayan.erpagentlocal.metrics.PerformanceEvent
import com.brayan.erpagentlocal.metrics.PerformanceTracker
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.LogSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import java.io.File

class LocalModelService {

    private var engine: Engine? = null

    private var initialized: Boolean = false
    private var loadedModelPath: String? = null
    private var loadedModelName: String? = null
    private var loadedModelFileSize: Long = 0L
    private var backendMode: ModelBackendMode = ModelBackendMode.AUTO
    private var activeBackendName: String = "none"
    private var modelLoadMs: Long? = null
    private var warmupMs: Long? = null
    private var lastGenerationMs: Long? = null
    private var lastError: String? = null

    private var currentSystemInstruction: String =
        PromptProvider.buildSystemPrompt()

    suspend fun initialize(
        context: Context,
        modelFile: File
    ): String {
        return initialize(
            context = context,
            modelFile = modelFile,
            systemInstruction = PromptProvider.buildSystemPrompt(),
            requestedBackendMode = ModelBackendMode.AUTO
        )
    }

    suspend fun initialize(
        context: Context,
        modelFile: File,
        systemInstruction: String = PromptProvider.buildSystemPrompt(),
        requestedBackendMode: ModelBackendMode = ModelBackendMode.AUTO
    ): String {
        return withContext(Dispatchers.IO) {
            val loadStartedAtMs = System.currentTimeMillis()
            val fallbackNotes = mutableListOf<String>()

            try {
                validateModelFile(modelFile)
                Engine.setNativeMinLogSeverity(LogSeverity.ERROR)

                val loadResult = loadEngineWithFallback(
                    context = context,
                    modelFile = modelFile,
                    requestedBackendMode = requestedBackendMode,
                    fallbackNotes = fallbackNotes
                )

                val elapsedMs = System.currentTimeMillis() - loadStartedAtMs
                PerformanceTracker.record(PerformanceEvent.MODEL_LOAD_MS, elapsedMs)
                PerformanceTracker.setAttribute(PerformanceEvent.ACTIVE_BACKEND, loadResult.backendName)

                closeCurrentEngine()

                engine = loadResult.engine
                initialized = true
                loadedModelPath = modelFile.absolutePath
                loadedModelName = modelFile.name
                loadedModelFileSize = modelFile.length()
                backendMode = requestedBackendMode
                activeBackendName = loadResult.backendName
                modelLoadMs = elapsedMs
                currentSystemInstruction = systemInstruction
                lastError = null

                buildString {
                    appendLine("Modelo inicializado correctamente.")
                    appendLine()
                    appendLine("Backend solicitado: $requestedBackendMode")
                    appendLine("Backend activo: $activeBackendName")
                    appendLine("Carga: ${elapsedMs} ms")
                    if (fallbackNotes.isNotEmpty()) {
                        appendLine()
                        appendLine("Notas:")
                        fallbackNotes.forEach { appendLine("- $it") }
                    }
                    appendLine()
                    appendLine("Archivo:")
                    appendLine(modelFile.absolutePath)
                    appendLine()
                    appendLine("Estado:")
                    appendLine(getModelStatus().toDebugText())
                }
            } catch (exception: Exception) {
                val elapsedMs = System.currentTimeMillis() - loadStartedAtMs
                PerformanceTracker.record(PerformanceEvent.MODEL_LOAD_MS, elapsedMs)

                lastError = exception.message ?: "Error inicializando modelo."

                buildString {
                    appendLine("ERROR AL INICIALIZAR MODELO")
                    appendLine()
                    appendLine(lastError)
                    appendLine()
                    if (initialized && loadedModelName != null) {
                        appendLine("Se conserva el modelo anterior:")
                        appendLine(loadedModelName)
                    } else {
                        appendLine("No hay un modelo anterior cargado.")
                    }
                }
            }
        }
    }

    suspend fun warmUp(): String {
        return withContext(Dispatchers.IO) {
            val startedAtMs = System.currentTimeMillis()
            try {
                val response = generateDecisionFresh(
                    """
                    Respond only with this valid JSON:
                    {
                      "type": "final",
                      "message": "ready"
                    }
                    """.trimIndent()
                )
                val elapsedMs = System.currentTimeMillis() - startedAtMs
                warmupMs = elapsedMs
                PerformanceTracker.record(PerformanceEvent.MODEL_WARMUP_MS, elapsedMs)
                response
            } catch (exception: Exception) {
                val elapsedMs = System.currentTimeMillis() - startedAtMs
                warmupMs = elapsedMs
                PerformanceTracker.record(PerformanceEvent.MODEL_WARMUP_MS, elapsedMs)
                lastError = exception.message ?: "Error en warmup del modelo."
                throw exception
            }
        }
    }

    /**
     * Crea una Conversation nueva en cada llamada para que la cache KV no acumule
     * turnos no relacionados del bucle del agente. El Engine se reutiliza.
     */
    suspend fun generateDecisionFresh(prompt: String): String {
        return withContext(Dispatchers.IO) {
            val activeEngine = engine
            if (!initialized || activeEngine == null) {
                throw IllegalStateException("El modelo local todavia no esta inicializado.")
            }

            val freshConversation = activeEngine.createConversation(
                ConversationConfig(systemInstruction = Contents.of(currentSystemInstruction))
            )

            val startedAtMs = System.currentTimeMillis()
            try {
                val result = StringBuilder()
                freshConversation
                    .sendMessageAsync(prompt)
                    .catch { throwable -> throw throwable }
                    .collect { message -> result.append(message.toString()) }

                val elapsedMs = System.currentTimeMillis() - startedAtMs
                lastGenerationMs = elapsedMs
                lastError = null
                PerformanceTracker.record(PerformanceEvent.GENERATION_MS, elapsedMs)

                result.toString().trim()
            } catch (exception: Exception) {
                val elapsedMs = System.currentTimeMillis() - startedAtMs
                lastGenerationMs = elapsedMs
                lastError = exception.message ?: "Error generando respuesta del modelo."
                PerformanceTracker.record(PerformanceEvent.GENERATION_MS, elapsedMs)
                throw exception
            } finally {
                try {
                    freshConversation.close()
                } catch (_: Exception) {
                }
            }
        }
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    fun getModelStatus(): ModelStatus {
        return ModelStatus(
            initialized = initialized,
            modelPath = loadedModelPath,
            modelName = loadedModelName,
            modelFileSize = loadedModelFileSize,
            backendMode = backendMode,
            activeBackend = activeBackendName,
            modelLoadMs = modelLoadMs,
            warmupMs = warmupMs,
            lastGenerationMs = lastGenerationMs,
            lastError = lastError
        )
    }

    fun close() {
        closeCurrentEngine()
        initialized = false
        loadedModelPath = null
        loadedModelName = null
        loadedModelFileSize = 0L
        activeBackendName = "none"
        modelLoadMs = null
        warmupMs = null
        lastGenerationMs = null
        currentSystemInstruction = PromptProvider.buildSystemPrompt()
    }

    private fun loadEngineWithFallback(
        context: Context,
        modelFile: File,
        requestedBackendMode: ModelBackendMode,
        fallbackNotes: MutableList<String>
    ): EngineLoadResult {
        val attempts = when (requestedBackendMode) {
            ModelBackendMode.AUTO -> listOf(ModelBackendMode.GPU, ModelBackendMode.CPU)
            ModelBackendMode.GPU -> listOf(ModelBackendMode.GPU)
            ModelBackendMode.CPU -> listOf(ModelBackendMode.CPU)
        }

        var lastFailure: Exception? = null

        attempts.forEach { attempt ->
            try {
                return createEngine(
                    context = context,
                    modelFile = modelFile,
                    backendMode = attempt
                )
            } catch (exception: Exception) {
                lastFailure = exception
                if (requestedBackendMode == ModelBackendMode.AUTO && attempt == ModelBackendMode.GPU) {
                    fallbackNotes.add("GPU no disponible: ${exception.message ?: "error desconocido"}. Se reintenta con CPU.")
                }
            }
        }

        throw lastFailure ?: IllegalStateException("No se pudo inicializar ningun backend.")
    }

    private fun createEngine(
        context: Context,
        modelFile: File,
        backendMode: ModelBackendMode
    ): EngineLoadResult {
        val backend = when (backendMode) {
            ModelBackendMode.GPU -> Backend.GPU()
            ModelBackendMode.CPU,
            ModelBackendMode.AUTO -> Backend.CPU()
        }

        var newEngine: Engine? = null

        try {
            val engineConfig = EngineConfig(
                modelPath = modelFile.absolutePath,
                backend = backend,
                cacheDir = context.cacheDir.absolutePath
            )

            newEngine = Engine(engineConfig)
            newEngine.initialize()

            return EngineLoadResult(
                engine = newEngine,
                backendName = backendMode.name
            )
        } catch (exception: Exception) {
            try {
                newEngine?.close()
            } catch (_: Exception) {
            }
            throw exception
        }
    }

    private fun closeCurrentEngine() {
        try {
            engine?.close()
        } catch (_: Exception) {
        }

        engine = null
    }

    private fun validateModelFile(modelFile: File) {
        if (!modelFile.exists()) {
            throw IllegalStateException("No existe el modelo en: ${modelFile.absolutePath}")
        }

        if (!modelFile.isFile) {
            throw IllegalStateException("La ruta seleccionada no es un archivo valido: ${modelFile.absolutePath}")
        }

        if (modelFile.length() <= 0L) {
            throw IllegalStateException("El archivo del modelo esta vacio: ${modelFile.absolutePath}")
        }

        if (!modelFile.name.endsWith(".litertlm", ignoreCase = true)) {
            throw IllegalStateException("El archivo seleccionado no parece ser un modelo .litertlm")
        }
    }

    private data class EngineLoadResult(
        val engine: Engine,
        val backendName: String
    )
}
