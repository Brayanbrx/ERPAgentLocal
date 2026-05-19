package com.brayan.erpagentlocal.model

data class CustomerRequest(
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val email: String? = null
)

data class CustomerDto(
    val customerId: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val isActive: Boolean
)