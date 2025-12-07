package com.orderly.data.dto

data class CreateOrderRequest(
    val userId: Int,
    val items: List<OrderItemRequest>
)