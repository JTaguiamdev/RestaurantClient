package com.restaurantclient.data.dto

data class ProductResponse(
    val product_id: Int,
    val name: String,
    val description: String?,
    val price: String,
    val product_image_uri: String?,
    val categories: List<CategoryDTO>? = null
)
