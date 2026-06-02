package com.brayan.erpagentlocal.data

import com.brayan.erpagentlocal.BuildConfig

object ApiConfig {

    val BASE_URL: String = BuildConfig.SERVERLESS_BASE_URL

    const val HEALTH = "/health"

    const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 20L
    const val DEFAULT_READ_TIMEOUT_SECONDS = 30L
    const val DEFAULT_WRITE_TIMEOUT_SECONDS = 30L

    fun buildUrl(path: String): String {
        val cleanBaseUrl = BASE_URL.trimEnd('/')
        val cleanPath = path.trimStart('/')

        return "$cleanBaseUrl/$cleanPath"
    }
}
