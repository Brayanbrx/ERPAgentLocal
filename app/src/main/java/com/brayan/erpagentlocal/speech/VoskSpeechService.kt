package com.brayan.erpagentlocal.speech

import android.content.Context
import com.brayan.erpagentlocal.metrics.PerformanceEvent
import com.brayan.erpagentlocal.metrics.PerformanceTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

/**
 * Servicio de reconocimiento de voz local basado en Vosk
 *
 * Centraliza la carga del modelo, el ciclo de vida del microfono y la entrega
 * de resultados parciales y finales para que la capa de UI no dependa de los
 * detalles propios del SDK de Vosk
 */
class VoskSpeechService {

    private var model: Model? = null        
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    // Banderas compartidas entre callbacks de Vosk y llamadas de la aplicación
    @Volatile private var initialized = false
    @Volatile private var recording = false

    /*
     * Protege contra entregas duplicadas: Vosk puede invocar onResult y
     * onFinalResult durante el cierre de una misma sesión de escucha
     */
    @Volatile private var resultDelivered = false
    @Volatile private var lastPartialText = ""
    @Volatile private var lastResultText = ""

    /**
     * Carga el modelo local de Vosk desde los assets hacia el almacenamiento interno
     *
     * La operación se ejecuta en un dispatcher de entrada/salida porque puede
     * desempaquetar archivos grandes. Si el modelo ya está cargado, responde
     * inmediatamente mediante [onReady].
     */
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
            val startedAtMs = System.currentTimeMillis()
            try {
                StorageService.unpack(
                    context,
                    "model-es",
                    "model-es",
                    { unpackedModel ->
                        model = unpackedModel
                        initialized = true

                        // metricas de rendimiento
                        PerformanceTracker.record(
                            PerformanceEvent.VOSK_LOAD_MS,
                            System.currentTimeMillis() - startedAtMs
                        )
                        onReady()
                    },
                    { exception ->
                        PerformanceTracker.record(
                            PerformanceEvent.VOSK_LOAD_MS,
                            System.currentTimeMillis() - startedAtMs
                        )
                        onError("No se pudo cargar el modelo Vosk: ${exception.message}")
                    }
                )
            } catch (exception: Exception) {
                PerformanceTracker.record(
                    PerformanceEvent.VOSK_LOAD_MS,
                    System.currentTimeMillis() - startedAtMs
                )
                onError("Error inicializando Vosk: ${exception.message}")
            }
        }
    }

    /**
     * Inicia una sesión de escucha desde el micrófono
     *
     * Entrega transcripciones parciales mientras el usuario habla y una unica
     * transcripción final cuando la sesión se detiene, llega a timeout o Vosk
     * confirma el cierre del audio.
     */
    fun startListening(
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val startedAtMs = System.currentTimeMillis()
        val activeModel = model
        if (activeModel == null) {
            onError("El modelo de voz todavía no está listo.")
            return
        }

        if (recording) return

        try {
            // Libera cualquier sesión anterior antes de crear un recognizer nuevo
            try { speechService?.stop(); speechService?.shutdown() } catch (_: Exception) {}
            try { recognizer?.close() } catch (_: Exception) {}
            speechService = null
            recognizer = null

            // Reinicia el estado acumulado de la sesión actual
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

                /*
                 * onResult se dispara cuando Vosk detecta silencio y confirma
                 * una frase. Se acumula el texto, pero el callback final se
                 * reserva para onFinalResult u onTimeout
                 */
                override fun onResult(hypothesis: String?) {
                    val text = extractText(hypothesis)
                    if (text.isNotBlank()) {
                        lastResultText = if (lastResultText.isBlank()) text
                        else "$lastResultText $text"
                    }
                }

                override fun onFinalResult(hypothesis: String?) {
                    if (resultDelivered) return

                    val text = extractText(hypothesis)
                    val finalText = lastResultText
                        .ifBlank { text }
                        .ifBlank { lastPartialText }
                        .trim()

                    recording = false
                    resultDelivered = true
                    PerformanceTracker.record(
                        PerformanceEvent.SPEECH_TRANSCRIPTION_MS,
                        System.currentTimeMillis() - startedAtMs
                    )

                    if (finalText.isNotBlank()) {
                        onFinalResult(finalText)
                    }
                }

                override fun onError(exception: Exception?) {
                    recording = false
                    PerformanceTracker.record(
                        PerformanceEvent.SPEECH_TRANSCRIPTION_MS,
                        System.currentTimeMillis() - startedAtMs
                    )
                    onError(exception?.message ?: "Error desconocido reconociendo audio.")
                }

                override fun onTimeout() {
                    if (resultDelivered) return
                    recording = false
                    resultDelivered = true
                    PerformanceTracker.record(
                        PerformanceEvent.SPEECH_TRANSCRIPTION_MS,
                        System.currentTimeMillis() - startedAtMs
                    )

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
            PerformanceTracker.record(
                PerformanceEvent.SPEECH_TRANSCRIPTION_MS,
                System.currentTimeMillis() - startedAtMs
            )
            onError("No se pudo iniciar la grabación: ${exception.message}")
        }
    }

    // Detiene la escucha activa y permite que Vosk emita el resultado final
    fun stopListening() {
        try {
            speechService?.stop()
        } catch (_: Exception) {}
        recording = false
    }

    /**
     * Cancela la escucha actual descartando resultados parciales o tardíos
     * Debe usarse cuando la interacción se abandona y no se quiere enviar una
     * transcripción final a la aplicación
     */
    fun cancelListening() {
        try {
            speechService?.cancel()
        } catch (_: Exception) {}
        // Ignora callbacks tardíos que puedan llegar después de cancelar.
        resultDelivered = true
        recording = false
        lastPartialText = ""
        lastResultText = ""
    }

    fun isInitialized() = initialized   // Indica si el modelo local ya fue cargado correctamente.
    fun isRecording() = recording       // Indica si existe una sesión de escucha activa.

    /**
     * Libera el micrófono, el recognizer y el modelo de Vosk
     */
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

    /**
     * Extrae el texto reconocido desde las respuestas JSON emitidas por Vosk
     * Vosk puede devolver el contenido en la clave "text" para resultados
     * confirmados o en "partial" para resultados provisionales
     */
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
