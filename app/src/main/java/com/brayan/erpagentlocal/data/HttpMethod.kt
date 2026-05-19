package com.brayan.erpagentlocal.data

enum class HttpMethod {
    GET,
    POST,
    PATCH,
    DELETE;

    companion object {
        fun fromValue(value: String): HttpMethod {
            return entries.firstOrNull { method ->
                method.name.equals(value.trim(), ignoreCase = true)
            } ?: GET
        }
    }
}