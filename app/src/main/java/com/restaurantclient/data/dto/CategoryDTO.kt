package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class CategoryDTO(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("category_id")
    val categoryId: Int? = null,
    val name: String,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
) {
    val resolvedId: Int?
        get() = id ?: categoryId
}

data class CategoryRequest(
    val name: String,
    val description: String? = null
)

data class CategoryProductRequest(
    val category_id: Int,
    val product_id: Int
)
