package com.brayan.erpagentlocal.model

data class InventoryDto(
    val productId: String,
    val stock: Int,
    val minStock: Int,
    val updatedAt: String?
)