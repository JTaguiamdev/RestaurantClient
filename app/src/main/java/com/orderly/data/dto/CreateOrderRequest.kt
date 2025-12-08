package com.orderly.data.dto

data class CreateOrderRequest(
    val products: List<OrderItemRequest>
)