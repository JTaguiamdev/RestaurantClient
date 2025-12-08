package com.orderly.data.dto

enum class RoleDTO {
    Admin,
    Customer;
    
    companion object {
        fun fromString(roleString: String?): RoleDTO? {
            return when (roleString?.lowercase()?.trim()) {
                "admin" -> Admin
                "customer" -> Customer
                "user" -> Customer // Sometimes backends use "user" instead of "customer"
                else -> null
            }
        }
    }
}