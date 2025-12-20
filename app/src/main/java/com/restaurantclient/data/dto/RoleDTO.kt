package com.restaurantclient.data.dto

enum class RoleDTO {
    Admin,
    Customer,
    Casher;

    companion object {
        fun fromString(roleString: String?): RoleDTO? {
            return when (roleString?.lowercase()?.trim()) {
                "admin" -> Admin
                "customer" -> Customer
                "user" -> Customer // Sometimes backends use "user" instead of "customer"
                "casher" -> Casher
                else -> null
            }
        }
    }
}
