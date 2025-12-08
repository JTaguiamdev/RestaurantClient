package com.orderly.data.dto

data class OrderResponse(
    val order_id: Int,
    val user_id: Int,
    val product_id: Int,
    val quantity: Int,
    val total_amount: String, // BigDecimal serialized as String
    val status: String?,
    val created_at: String?,
    val updated_at: String?
)