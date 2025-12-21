package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("user_id")
    val userId: Int?,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("role")
    val roleDetails: RoleDetailsDTO?, // Complex role object from backend (for backward compatibility)
    
    @SerializedName("roles")
    val roles: List<RoleDetailsDTO>?, // Multiple roles support
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
) {
    // Computed property to get simple role from complex role object (backward compatibility)
    val role: RoleDTO?
        get() = roleDetails?.toRoleDTO() ?: roles?.firstOrNull()?.toRoleDTO()
        
    // Get all role names
    val roleNames: List<String>
        get() = roles?.map { it.name } ?: roleDetails?.let { listOf(it.name) } ?: emptyList()
        
    // Helper method to check if user is admin
    fun isAdmin(): Boolean = role == RoleDTO.Admin || roles?.any { it.toRoleDTO() == RoleDTO.Admin } == true
    
    // Helper method to check if user is casher
    fun isCasher(): Boolean = role == RoleDTO.Casher || roles?.any { it.toRoleDTO() == RoleDTO.Casher } == true
    
    // Helper method to check if user is customer  
    fun isCustomer(): Boolean = role == RoleDTO.Customer || roles?.any { it.toRoleDTO() == RoleDTO.Customer } == true
    
    // Check if user has specific role
    fun hasRole(roleName: String): Boolean = 
        roleNames.any { it.equals(roleName, ignoreCase = true) }
        
    // Check if user has any of the specified roles
    fun hasAnyRole(vararg roleNames: String): Boolean =
        roleNames.any { hasRole(it) }
}
