package com.brayan.erpagentlocal.model

data class SaleItemRequest(
    val productId: String,
    val quantity: Int
)

data class SaleRequest(
    val customerId: String,
    val items: List<SaleItemRequest>
)