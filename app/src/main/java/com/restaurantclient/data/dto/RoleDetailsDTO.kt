package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class RoleDetailsDTO(
    @SerializedName("role_id")
    val roleId: Int,
    
    @SerializedName("name")
    val name: String, // "ADMIN" or "CUSTOMER"
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("permissions")
    val permissions: List<String>?,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
) {
    // Convert backend role to our app's RoleDTO
    fun toRoleDTO(): RoleDTO? {
        return when (name.uppercase()) {
            "ADMIN" -> RoleDTO.Admin
            "CUSTOMER" -> RoleDTO.Customer
            "USER" -> RoleDTO.Customer // Fallback
            "CASHER" -> RoleDTO.Casher
            else -> null
        }
    }
}
