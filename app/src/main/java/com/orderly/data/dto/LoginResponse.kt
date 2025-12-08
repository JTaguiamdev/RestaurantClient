package com.orderly.data.dto

data class LoginResponse(
    val token: String,
    val message: String,
    val user: UserDTO? = null  // Make user optional in case backend doesn't return it
)