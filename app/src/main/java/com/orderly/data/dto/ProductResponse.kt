package com.orderly.data.dto

data class ProductResponse(
    val productId: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val createdAt: String,
    val updatedAt: String
)