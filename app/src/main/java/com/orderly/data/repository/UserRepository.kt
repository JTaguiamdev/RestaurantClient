package com.orderly.data.repository

import com.orderly.data.Result
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.dto.NewUserDTO
import com.orderly.data.dto.UserDTO
import com.orderly.data.network.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(loginDto: LoginDTO): Result<LoginResponse> {
        return try {
            val response = apiService.login(loginDto)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun register(newUserDto: NewUserDTO): Result<LoginResponse> {
        return try {
            val response = apiService.register(newUserDto)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to register: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserById(userId: Int): Result<UserDTO> {
        return try {
            val response = apiService.getUserById(userId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get user by ID: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}