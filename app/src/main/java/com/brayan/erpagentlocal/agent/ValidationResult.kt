package com.brayan.erpagentlocal.agent

data class ValidationResult(
    val isValid: Boolean,
    val message: String = "",
    val shouldAskUser: Boolean = false,
    val askUserMessage: String? = null,
    val isAlreadyDone: Boolean = false
) {

    companion object {

        fun valid(): ValidationResult {
            return ValidationResult(isValid = true, message = "Valid tool call")
        }

        fun invalid(message: String): ValidationResult {
            return ValidationResult(isValid = false, message = message)
        }

        fun askUser(message: String): ValidationResult {
            return ValidationResult(
                isValid = false,
                message = message,
                shouldAskUser = true,
                askUserMessage = message
            )
        }

        fun alreadyDone(message: String): ValidationResult {
            return ValidationResult(isValid = false, message = message, isAlreadyDone = true)
        }
    }
}