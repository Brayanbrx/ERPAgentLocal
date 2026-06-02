package com.brayan.erpagentlocal.model

import android.content.Context
import com.brayan.erpagentlocal.util.ModelFileManager
import java.io.File

class ModelProvisioningManager(
    private val context: Context
) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getPreferredModelFile(): File {
        val lastFunctional = getLastFunctionalModelFile()
        if (lastFunctional != null) return lastFunctional
        return getDefaultModelFile()
    }

    fun getDefaultModelFile(): File {
        return ModelFileManager.getDefaultModelFile(context)
    }

    fun validateModelFile(file: File): ModelValidationResult {
        return when {
            !file.exists() -> ModelValidationResult(false, file, "No existe el modelo: ${file.absolutePath}")
            !file.isFile -> ModelValidationResult(false, file, "La ruta no es un archivo: ${file.absolutePath}")
            file.length() <= 0L -> ModelValidationResult(false, file, "El modelo esta vacio: ${file.absolutePath}")
            !file.name.endsWith(".litertlm", ignoreCase = true) ->
                ModelValidationResult(false, file, "El archivo no tiene extension .litertlm")
            else -> ModelValidationResult(true, file, "Modelo valido")
        }
    }

    fun markModelFunctional(file: File) {
        preferences.edit()
            .putString(KEY_LAST_FUNCTIONAL_MODEL_PATH, file.absolutePath)
            .apply()
    }

    fun getLastFunctionalModelFile(): File? {
        val path = preferences.getString(KEY_LAST_FUNCTIONAL_MODEL_PATH, null) ?: return null
        val file = File(path)
        return file.takeIf { validateModelFile(it).valid }
    }

    companion object {
        private const val PREFERENCES_NAME = "model_provisioning"
        private const val KEY_LAST_FUNCTIONAL_MODEL_PATH = "last_functional_model_path"
    }
}
