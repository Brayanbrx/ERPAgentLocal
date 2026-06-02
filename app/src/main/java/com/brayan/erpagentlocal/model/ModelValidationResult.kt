package com.brayan.erpagentlocal.model

import java.io.File

data class ModelValidationResult(
    val valid: Boolean,
    val file: File?,
    val message: String
)
