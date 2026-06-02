package com.brayan.erpagentlocal.speech

/**
 * Representa el estado actual del permiso de grabación de audio.
 */
enum class AudioPermissionState {

    UNKNOWN,     // El permiso aun no fue consultado o no hay información suficiente
    GRANTED,     // El usuario concedio permiso para acceder al micro
    DENIED       // El usuario denego el permiso para acceder al micro
}
