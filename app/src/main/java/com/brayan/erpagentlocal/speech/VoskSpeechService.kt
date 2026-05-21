package com.brayan.erpagentlocal.speech

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class VoskSpeechService {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    private var initialized: Boolean = false
    private var recording: Boolean = false
    private var lastPartialText: String = ""
    private var lastFinalText: String = ""

    suspend fun initialize(
        context: Context,
        onReady: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (initialized && model != null) {
            onReady()
            return
        }

        withContext(Dispatchers.IO) {
            try {
                StorageService.unpack(
                    context,
                    "model-es",
                    "model-es",
                    { unpackedModel ->
                        model = unpackedModel
                        initialized = true
                        onReady()
                    },
                    { exception ->
                        onError("No se pudo cargar el modelo Vosk: ${exception.message}")
                    }
                )
            } catch (exception: Exception) {
                onError("Error inicializando Vosk: ${exception.message}")
            }
        }
    }

    fun startListening(
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val activeModel = model

        if (activeModel == null) {
            onError("El modelo de voz todavía no está listo.")
            return
        }

        if (recording) {
            return
        }

        try {
            lastPartialText = ""
            lastFinalText = ""

            val activeRecognizer = Recognizer(activeModel, 16000.0f)
            recognizer = activeRecognizer

            val activeSpeechService = SpeechService(activeRecognizer, 16000.0f)
            speechService = activeSpeechService

            activeSpeechService.startListening(
                object : RecognitionListener {

                    override fun onPartialResult(hypothesis: String?) {
                        val text = extractText(hypothesis)
                        if (text.isNotBlank()) {
                            lastPartialText = text
                            onPartialResult(text)
                        }
                    }

                    override fun onResult(hypothesis: String?) {
                        val text = extractText(hypothesis)
                        if (text.isNotBlank()) {
                            lastFinalText = text
                            onFinalResult(text)
                        }
                    }

                    override fun onFinalResult(hypothesis: String?) {
                        val text = extractText(hypothesis)
                        val finalText = text.ifBlank { lastFinalText.ifBlank { lastPartialText } }

                        if (finalText.isNotBlank()) {
                            onFinalResult(finalText)
                        }

                        recording = false
                    }

                    override fun onError(exception: Exception?) {
                        recording = false
                        onError(exception?.message ?: "Error desconocido reconociendo audio.")
                    }

                    override fun onTimeout() {
                        recording = false
                        val finalText = lastFinalText.ifBlank { lastPartialText }

                        if (finalText.isNotBlank()) {
                            onFinalResult(finalText)
                        }
                    }
                }
            )

            recording = true
        } catch (exception: Exception) {
            recording = false
            onError("No se pudo iniciar la grabación: ${exception.message}")
        }
    }

    fun stopListening() {
        try {
            speechService?.stop()
        } catch (_: Exception) {
        }

        recording = false
    }

    fun cancelListening() {
        try {
            speechService?.cancel()
        } catch (_: Exception) {
        }

        recording = false
        lastPartialText = ""
        lastFinalText = ""
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    fun isRecording(): Boolean {
        return recording
    }

    fun release() {
        try {
            speechService?.stop()
            speechService?.shutdown()
        } catch (_: Exception) {
        }

        try {
            recognizer?.close()
        } catch (_: Exception) {
        }

        try {
            model?.close()
        } catch (_: Exception) {
        }

        speechService = null
        recognizer = null
        model = null
        initialized = false
        recording = false
    }

    private fun extractText(rawJson: String?): String {
        if (rawJson.isNullOrBlank()) {
            return ""
        }

        return try {
            val json = JSONObject(rawJson)
            json.optString("text")
                .ifBlank { json.optString("partial") }
                .trim()
        } catch (_: Exception) {
            ""
        }
    }
}