package com.orderly.data.dto

data class UserDTO(
    val userId: Int?,
    val username: String,
    val role: RoleDTO?,
    val createdAt: String?,
    val updatedAt: String?
)