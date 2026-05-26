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

    @Volatile private var initialized = false
    @Volatile private var recording = false

    // Protege contra el doble disparo de Vosk: tanto onResult como onFinalResult pueden
    // dispararse al llamar stop(), por eso el callback se entrega una sola vez por sesión.
    @Volatile private var resultDelivered = false
    @Volatile private var lastPartialText = ""
    @Volatile private var lastResultText = ""   // acumulado de llamadas intermedias a onResult

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

        if (recording) return

        try {
            // Libera la sesión anterior antes de iniciar una nueva
            try { speechService?.stop(); speechService?.shutdown() } catch (_: Exception) {}
            try { recognizer?.close() } catch (_: Exception) {}
            speechService = null
            recognizer = null

            // Reinicia el estado por sesión
            resultDelivered = false
            lastPartialText = ""
            lastResultText = ""

            val activeRecognizer = Recognizer(activeModel, 16000.0f)
            recognizer = activeRecognizer

            val activeSpeechService = SpeechService(activeRecognizer, 16000.0f)
            speechService = activeSpeechService

            activeSpeechService.startListening(object : RecognitionListener {

                override fun onPartialResult(hypothesis: String?) {
                    val text = extractText(hypothesis)
                    if (text.isNotBlank()) {
                        lastPartialText = text
                        onPartialResult(text)
                    }
                }

                // onResult se dispara cuando Vosk detecta silencio y confirma una frase.
                // Acumulamos el texto aquí pero NO entregamos el callback final —
                // eso lo maneja exclusivamente onFinalResult para evitar envíos dobles.
                override fun onResult(hypothesis: String?) {
                    val text = extractText(hypothesis)
                    if (text.isNotBlank()) {
                        lastResultText = if (lastResultText.isBlank()) text
                        else "$lastResultText $text"
                    }
                }

                // onFinalResult se dispara al llamar stop(). Es el único punto
                // donde entregamos la transcripción completa a la app.
                override fun onFinalResult(hypothesis: String?) {
                    if (resultDelivered) return

                    val text = extractText(hypothesis)
                    val finalText = lastResultText
                        .ifBlank { text }
                        .ifBlank { lastPartialText }
                        .trim()

                    recording = false
                    resultDelivered = true

                    if (finalText.isNotBlank()) {
                        onFinalResult(finalText)
                    }
                }

                override fun onError(exception: Exception?) {
                    recording = false
                    onError(exception?.message ?: "Error desconocido reconociendo audio.")
                }

                override fun onTimeout() {
                    if (resultDelivered) return
                    recording = false
                    resultDelivered = true

                    val finalText = lastResultText
                        .ifBlank { lastPartialText }
                        .trim()

                    if (finalText.isNotBlank()) {
                        onFinalResult(finalText)
                    }
                }
            })

            recording = true
        } catch (exception: Exception) {
            recording = false
            onError("No se pudo iniciar la grabación: ${exception.message}")
        }
    }

    fun stopListening() {
        try {
            speechService?.stop()
        } catch (_: Exception) {}
        recording = false
    }

    fun cancelListening() {
        try {
            speechService?.cancel()
        } catch (_: Exception) {}
        // Marca como entregado para ignorar callbacks tardíos de Vosk
        resultDelivered = true
        recording = false
        lastPartialText = ""
        lastResultText = ""
    }

    fun isInitialized() = initialized
    fun isRecording() = recording

    fun release() {
        try {
            speechService?.stop()
            speechService?.shutdown()
        } catch (_: Exception) {}

        try { recognizer?.close() } catch (_: Exception) {}
        try { model?.close() } catch (_: Exception) {}

        speechService = null
        recognizer = null
        model = null
        initialized = false
        recording = false
    }

    private fun extractText(rawJson: String?): String {
        if (rawJson.isNullOrBlank()) return ""
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
