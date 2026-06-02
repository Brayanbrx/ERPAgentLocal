package com.brayan.erpagentlocal.di

import android.content.Context
import com.brayan.erpagentlocal.ai.AgentService
import com.brayan.erpagentlocal.ai.LocalModelService
import com.brayan.erpagentlocal.metrics.MetricsStore
import com.brayan.erpagentlocal.model.ModelProvisioningManager
import com.brayan.erpagentlocal.network.BackendHealthMonitor
import com.brayan.erpagentlocal.speech.VoskSpeechService
import com.brayan.erpagentlocal.trace.TraceStore

class AppServiceContainer private constructor(context: Context) {

    private val appContext = context.applicationContext

    val agentService: AgentService = AgentService()
    val localModelService: LocalModelService = LocalModelService()
    val voskSpeechService: VoskSpeechService = VoskSpeechService() // Unica instancia del servicio de vosk
    val backendHealthMonitor: BackendHealthMonitor = BackendHealthMonitor(appContext)
    val modelProvisioningManager: ModelProvisioningManager = ModelProvisioningManager(appContext)
    val metricsStore: MetricsStore = MetricsStore(appContext)
    val traceStore: TraceStore = TraceStore(appContext)

    companion object {
        @Volatile
        private var instance: AppServiceContainer? = null

        fun get(context: Context): AppServiceContainer {
            return instance ?: synchronized(this) {
                instance ?: AppServiceContainer(context).also { instance = it }
            }
        }
    }
}
