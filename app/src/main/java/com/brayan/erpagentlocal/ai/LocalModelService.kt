package com.brayan.erpagentlocal.ai

import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.LogSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.io.File

class LocalModelService {

    private var engine: Engine? = null
    private var conversation: Conversation? = null

    private var initialized: Boolean = false
    private var loadedModelPath: String? = null
    private var loadedModelName: String? = null
    private var lastError: String? = null

    private var currentSystemInstruction: String =
        PromptProvider.buildSystemPrompt()

    private val backendName: String = "CPU"

    suspend fun initialize(
        context: Context,
        modelFile: File
    ): String {
        return initialize(
            context = context,
            modelFile = modelFile,
            systemInstruction = PromptProvider.buildSystemPrompt()
        )
    }

    suspend fun initialize(
        context: Context,
        modelFile: File,
        systemInstruction: String = PromptProvider.buildSystemPrompt()
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                validateModelFile(modelFile)

                close()

                currentSystemInstruction = systemInstruction
                lastError = null

                Engine.setNativeMinLogSeverity(LogSeverity.ERROR)

                val engineConfig = EngineConfig(
                    modelPath = modelFile.absolutePath,
                    backend = Backend.CPU(),
                    cacheDir = context.cacheDir.absolutePath
                )

                val newEngine = Engine(engineConfig)
                newEngine.initialize()

                val conversationConfig = ConversationConfig(
                    systemInstruction = Contents.of(systemInstruction)
                )

                val newConversation = newEngine.createConversation(conversationConfig)

                engine = newEngine
                conversation = newConversation
                initialized = true
                loadedModelPath = modelFile.absolutePath
                loadedModelName = modelFile.name

                buildString {
                    appendLine("Modelo inicializado correctamente.")
                    appendLine()
                    appendLine("Backend: $backendName")
                    appendLine()
                    appendLine("Archivo:")
                    appendLine(modelFile.absolutePath)
                    appendLine()
                    appendLine("Estado:")
                    appendLine(getModelStatus().toDebugText())
                }
            } catch (exception: Exception) {
                close()

                lastError = exception.message ?: "Error inicializando modelo."

                buildString {
                    appendLine("ERROR AL INICIALIZAR MODELO")
                    appendLine()
                    appendLine(lastError)
                }
            }
        }
    }

    suspend fun generate(prompt: String): String {
        return generateResult(prompt).getOrThrow()
    }

    suspend fun generateDecision(prompt: String): String {
        return generateResult(prompt).getOrThrow()
    }

    suspend fun generateResult(prompt: String): ModelGenerationResult {
        return withContext(Dispatchers.IO) {
            if (!initialized) {
                val message = "El modelo local todavía no está inicializado."
                lastError = message

                return@withContext ModelGenerationResult.failure(
                    error = message,
                    promptLength = prompt.length
                )
            }

            val activeConversation = conversation

            if (activeConversation == null) {
                val message = "No existe una conversación activa con el modelo."
                lastError = message

                return@withContext ModelGenerationResult.failure(
                    error = message,
                    promptLength = prompt.length
                )
            }

            try {
                val result = StringBuilder()

                activeConversation
                    .sendMessageAsync(prompt)
                    .catch { throwable ->
                        throw throwable
                    }
                    .collect { message ->
                        result.append(message.toString())
                    }

                val output = result.toString().trim()

                lastError = null

                ModelGenerationResult.success(
                    text = output,
                    promptLength = prompt.length
                )
            } catch (exception: Exception) {
                val message = exception.message ?: "Error generando respuesta del modelo."
                lastError = message

                ModelGenerationResult.failure(
                    error = message,
                    promptLength = prompt.length
                )
            }
        }
    }

    suspend fun testJsonResponse(): String {
        val prompt = """
            Respond only with valid JSON.
            Do not use markdown.
            Do not explain.

            Return exactly this structure:
            {
              "type": "final",
              "message": "model_ready"
            }
        """.trimIndent()

        return generateDecision(prompt)
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    fun getLoadedModelPath(): String? {
        return loadedModelPath
    }

    fun getLoadedModelName(): String? {
        return loadedModelName
    }

    fun getSystemInstruction(): String {
        return currentSystemInstruction
    }

    fun getModelStatus(): ModelStatus {
        return ModelStatus(
            initialized = initialized,
            modelPath = loadedModelPath,
            modelName = loadedModelName,
            backend = backendName,
            lastError = lastError
        )
    }

    fun getStatus(): String {
        return getModelStatus().toDebugText()
    }

    fun close() {
        try {
            conversation?.close()
        } catch (_: Exception) {
        }

        try {
            engine?.close()
        } catch (_: Exception) {
        }

        conversation = null
        engine = null
        initialized = false
        loadedModelPath = null
        loadedModelName = null
        currentSystemInstruction = PromptProvider.buildSystemPrompt()
    }

    private fun validateModelFile(modelFile: File) {
        if (!modelFile.exists()) {
            throw IllegalStateException("No existe el modelo en: ${modelFile.absolutePath}")
        }

        if (!modelFile.isFile) {
            throw IllegalStateException("La ruta seleccionada no es un archivo válido: ${modelFile.absolutePath}")
        }

        if (modelFile.length() <= 0L) {
            throw IllegalStateException("El archivo del modelo está vacío: ${modelFile.absolutePath}")
        }

        if (!modelFile.name.endsWith(".litertlm", ignoreCase = true)) {
            throw IllegalStateException("El archivo seleccionado no parece ser un modelo .litertlm")
        }
    }
}