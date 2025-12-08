package com.orderly.data.dto

data class ProductResponse(
    val product_id: Int,
    val name: String,
    val description: String?,
    val price: String, // BigDecimal serialized as String
    val product_image_uri: String?
)