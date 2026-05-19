package com.brayan.erpagentlocal.model

data class ProductRequest(
    val name: String,
    val description: String? = null,
    val unit: String = "unit",
    val salePrice: Double,
    val purchasePrice: Double
)

data class ProductDto(
    val productId: String,
    val name: String,
    val description: String?,
    val unit: String,
    val salePrice: Double,
    val purchasePrice: Double,
    val isActive: Boolean
)