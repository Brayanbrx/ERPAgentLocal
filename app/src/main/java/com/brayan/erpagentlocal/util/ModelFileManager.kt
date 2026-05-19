package com.brayan.erpagentlocal.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ModelFileManager {

    private const val MODELS_DIR_NAME = "models"
    private const val DEFAULT_MODEL_NAME = "gemma-4-E2B-it.litertlm"

    suspend fun copyModelToInternalStorage(
        context: Context,
        uri: Uri
    ): File {
        return withContext(Dispatchers.IO) {
            val modelsDir = File(context.filesDir, MODELS_DIR_NAME)

            if (!modelsDir.exists()) {
                modelsDir.mkdirs()
            }

            val originalName = getFileNameFromUri(context, uri)
                ?: DEFAULT_MODEL_NAME

            val safeName = if (originalName.endsWith(".litertlm", ignoreCase = true)) {
                originalName
            } else {
                DEFAULT_MODEL_NAME
            }

            val destinationFile = File(modelsDir, safeName)

            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) {
                    "No se pudo abrir el archivo seleccionado."
                }

                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            destinationFile
        }
    }

    fun getModelsDir(context: Context): File {
        return File(context.filesDir, MODELS_DIR_NAME)
    }

    fun getDefaultModelFile(context: Context): File {
        val modelsDir = getModelsDir(context)

        if (!modelsDir.exists()) {
            modelsDir.mkdirs()
        }

        val existingModels = modelsDir
            .listFiles()
            ?.filter { it.isFile && it.name.endsWith(".litertlm", ignoreCase = true) }
            .orEmpty()

        return existingModels.firstOrNull()
            ?: File(modelsDir, DEFAULT_MODEL_NAME)
    }

    fun modelExists(context: Context): Boolean {
        val file = getDefaultModelFile(context)
        return file.exists() && file.length() > 0
    }

    fun getModelInfo(context: Context): String {
        val file = getDefaultModelFile(context)

        if (!file.exists()) {
            return "No hay modelo copiado todavía."
        }

        val sizeMb = file.length().toDouble() / (1024.0 * 1024.0)

        return buildString {
            appendLine("Modelo encontrado:")
            appendLine(file.absolutePath)
            appendLine("Tamaño aproximado: ${"%.2f".format(sizeMb)} MB")
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null

        return try {
            cursor = context.contentResolver.query(uri, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (nameIndex >= 0) {
                    cursor.getString(nameIndex)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }
}