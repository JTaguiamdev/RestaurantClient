package com.orderly.data.dto

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("user_id")
    val userId: Int?,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("role")
    val roleDetails: RoleDetailsDTO?, // Complex role object from backend
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
) {
    // Computed property to get simple role from complex role object
    val role: RoleDTO?
        get() = roleDetails?.toRoleDTO()
        
    // Helper method to check if user is admin
    fun isAdmin(): Boolean = role == RoleDTO.Admin
    
    // Helper method to check if user is customer  
    fun isCustomer(): Boolean = role == RoleDTO.Customer
}