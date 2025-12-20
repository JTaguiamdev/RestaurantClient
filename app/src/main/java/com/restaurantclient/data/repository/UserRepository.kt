package com.restaurantclient.data.repository

import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.AddRolesRequest
import com.restaurantclient.data.dto.AssignMultipleRolesRequest
import com.restaurantclient.data.dto.AssignRoleRequest
import com.restaurantclient.data.dto.CreateUserRequest
import com.restaurantclient.data.dto.LoginDTO
import com.restaurantclient.data.dto.LoginResponse
import com.restaurantclient.data.dto.NewUserDTO
import com.restaurantclient.data.dto.RoleDetailsDTO
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.UpdateUserRolesRequest
import com.restaurantclient.data.dto.UpdateUserRequest // Added missing import
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {

    private var cachedUsers: List<UserDTO>? = null

    fun clearCache() {
        cachedUsers = null
    }

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
    suspend fun getAllUsers(forceRefresh: Boolean = false): Result<List<UserDTO>> {
        if (cachedUsers != null && !forceRefresh) {
            android.util.Log.d("UserRepository", "Returning cached users (${cachedUsers!!.size} users)")
            return Result.Success(cachedUsers!!)
        }
        
        android.util.Log.d("UserRepository", "Fetching users from API (forceRefresh: $forceRefresh)")
        return try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                val newUsers = response.body() ?: emptyList()
                if (newUsers != cachedUsers) {
                    android.util.Log.d("UserRepository", "Updating user cache with ${newUsers.size} users")
                    cachedUsers = newUsers
                } else {
                    android.util.Log.d("UserRepository", "Users data unchanged, keeping existing cache")
                }
                Result.Success(cachedUsers!!)
            } else {
                Result.Error(Exception("Failed to get users: ${response.code()}"))
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

    suspend fun createUser(username: String, password: String, role: RoleDTO): Result<Unit> {
        return try {
            val roleString = when (role) {
                RoleDTO.Admin -> "Admin"
                RoleDTO.Customer -> "Customer"
                RoleDTO.Casher -> "Casher"
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

    suspend fun updateUserProfile(userId: Int, updateUserRequest: UpdateUserRequest): Result<Unit> {
        return try {
            val response = apiService.updateUserProfile(userId, updateUserRequest)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to update user profile: ${response.code()}"))
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

    suspend fun assignRole(username: String, role: RoleDTO): Result<Unit> {
        return try {
            val roleName = when (role) {
                RoleDTO.Admin -> "ADMIN"
                RoleDTO.Customer -> "customer"
                RoleDTO.Casher -> "Casher"
            }
            val response = apiService.assignRole(AssignRoleRequest(username, roleName))
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to assign role: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Multi-Role Management Methods
    suspend fun assignMultipleRoles(username: String, roleNames: List<String>): Result<Unit> {
        return try {
            val request = AssignMultipleRolesRequest(username, roleNames)
            val response = apiService.assignMultipleRoles(request)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to assign multiple roles: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateUserRoles(userId: Int, roleNames: List<String>): Result<UserDTO> {
        return try {
            val request = UpdateUserRolesRequest(userId, roleNames)
            val response = apiService.updateUserRoles(userId, request)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update user roles: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun addRolesToUser(username: String, roleNames: List<String>): Result<UserDTO> {
        return try {
            val request = AddRolesRequest(roleNames)
            val response = apiService.addRolesToUser(username, request)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to add roles: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun removeRoleFromUser(username: String, roleName: String): Result<UserDTO> {
        return try {
            val response = apiService.removeRoleFromUser(username, roleName)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to remove role: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserRoles(username: String): Result<List<RoleDetailsDTO>> {
        return try {
            val response = apiService.getUserRoles(username)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to get user roles: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
