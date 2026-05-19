package com.brayan.erpagentlocal.agent

object NativeToolCallingEvaluator {

    fun evaluate(
        modelPath: String?,
        toolCatalog: ToolCatalog
    ): NativeToolCallingReadiness {
        val cleanModelPath = modelPath.orEmpty()
        val modelName = cleanModelPath
            .substringAfterLast('/')
            .substringAfterLast('\\')

        val normalizedName = modelName.lowercase()

        val reasons = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val looksLikeLiteRtModel = normalizedName.endsWith(".litertlm")
        val looksLikeFunctionGemma =
            normalizedName.contains("functiongemma") ||
                    normalizedName.contains("function-gemma") ||
                    normalizedName.contains("function_gemma")

        val hasTools = toolCatalog.count() > 0

        if (looksLikeLiteRtModel) {
            reasons.add("El archivo seleccionado parece ser un modelo .litertlm.")
        } else {
            warnings.add("El archivo no parece ser .litertlm.")
        }

        if (looksLikeFunctionGemma) {
            reasons.add("El nombre del modelo sugiere FunctionGemma, especializado en function calling.")
        } else {
            warnings.add("El nombre del modelo no sugiere FunctionGemma. Puede funcionar con JSON manual, pero no es ideal para native tools.")
        }

        if (hasTools) {
            reasons.add("El catálogo tiene ${toolCatalog.count()} tools disponibles.")
        } else {
            warnings.add("No hay tools cargadas en ToolCatalog.")
        }

        val canTryNativeTools = looksLikeLiteRtModel && looksLikeFunctionGemma && hasTools

        val recommendedMode = if (canTryNativeTools) {
            AgentToolCallingMode.NATIVE_LITERT_EXPERIMENTAL
        } else {
            AgentToolCallingMode.JSON_MANUAL
        }

        if (!canTryNativeTools) {
            reasons.add("Se recomienda mantener JSON tool_call manual porque es más estable con el modelo actual.")
        }

        warnings.add(
            "El modo nativo debe activarse solo después de confirmar la API exacta de LiteRT-LM disponible en tu versión."
        )

        return NativeToolCallingReadiness(
            canTryNativeTools = canTryNativeTools,
            recommendedMode = recommendedMode,
            modelName = modelName,
            reasons = reasons,
            warnings = warnings
        )
    }
}