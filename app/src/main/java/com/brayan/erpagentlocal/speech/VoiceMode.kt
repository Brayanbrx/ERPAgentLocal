package com.brayan.erpagentlocal.speech

/**
 * Define como se comporta la interfaz despues de obtener una transcripción.
 */
enum class VoiceMode {

    REVIEW,     // Muestra el texto reconocido para que el usuario lo revise antes de enviarlo
    AUTO_SEND   // Envía automáticamente el texto reconocido cuando finaliza la escucha
}
