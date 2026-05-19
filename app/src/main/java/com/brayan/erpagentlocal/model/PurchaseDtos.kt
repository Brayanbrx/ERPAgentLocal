package com.brayan.erpagentlocal.model

data class PurchaseRequest(
    val productId: String,
    val quantity: Int,
    val unitCost: Double
)