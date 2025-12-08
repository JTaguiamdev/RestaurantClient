package com.orderly.data.repository

import com.orderly.data.Result
import com.orderly.data.dto.CreateUserRequest
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.dto.NewUserDTO
import com.orderly.data.dto.RoleDTO
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

    suspend fun refreshToken(): Result<LoginResponse> {
        return try {
            val response = apiService.refreshToken()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to refresh token: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Admin User Management Methods
    suspend fun getAllUsers(): Result<List<UserDTO>> {
        return try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to get users: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun createUser(username: String, password: String, role: RoleDTO): Result<Unit> {
        return try {
            val roleString = when (role) {
                RoleDTO.Admin -> "Admin"
                RoleDTO.Customer -> "Customer"
            }
            val createUserRequest = CreateUserRequest(username, password, roleString)
            val response = apiService.createUser(createUserRequest)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to create user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateUser(userId: Int, userDTO: UserDTO): Result<UserDTO> {
        return try {
            val response = apiService.updateUser(userId, userDTO)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteUser(userId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to delete user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}