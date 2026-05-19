package com.brayan.erpagentlocal.data

object ApiConfig {

    const val BASE_URL = "https://qnphwupj51.execute-api.us-east-1.amazonaws.com/Prod"

    const val CUSTOMERS = "/customers"
    const val PRODUCTS = "/products"
    const val PURCHASES = "/purchases"
    const val SALES = "/sales"
    const val INVENTORY = "/inventory"
    const val HEALTH = "/health"

    const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 20L
    const val DEFAULT_READ_TIMEOUT_SECONDS = 30L
    const val DEFAULT_WRITE_TIMEOUT_SECONDS = 30L

    fun buildUrl(path: String): String {
        val cleanBaseUrl = BASE_URL.trimEnd('/')
        val cleanPath = path.trimStart('/')

        return "$cleanBaseUrl/$cleanPath"
    }

    fun getProductionBaseUrl(): String {
        return BASE_URL
    }
}