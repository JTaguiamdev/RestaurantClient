package com.restaurantclient.ui.intro

data class IntroProduct(
    val productId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val productImageUri: Int, // Drawable resource ID
    val category: String
)
