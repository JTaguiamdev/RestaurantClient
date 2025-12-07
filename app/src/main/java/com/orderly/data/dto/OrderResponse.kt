package com.orderly.data.dto

data class OrderResponse(
    val orderId: Int,
    val userId: Int,
    val items: List<OrderItemResponse>,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)