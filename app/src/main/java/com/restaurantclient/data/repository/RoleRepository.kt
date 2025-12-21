package com.restaurantclient.data.repository

import android.util.Log
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.PermissionRequest
import com.restaurantclient.data.dto.RoleDetailsDTO
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.RoleRequest
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRepository @Inject constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "RoleRepository"
    }

    private var cachedRoles: List<RoleDetailsDTO>? = null

    fun clearCache() {
        cachedRoles = null
    }

    suspend fun getAllRoles(forceRefresh: Boolean = false): Result<List<RoleDetailsDTO>> {
        return try {
            if (!forceRefresh && cachedRoles != null) {
                Log.d(TAG, "Returning cached roles (${cachedRoles!!.size} roles)")
                return Result.Success(cachedRoles!!)
            }

            Log.d(TAG, "Fetching roles from API (forceRefresh: $forceRefresh)")
            val response = apiService.getAllRolesWithDetails()
            
            if (response.isSuccessful && response.body() != null) {
                val roles = response.body()!!
                Log.d(TAG, "API Response code: ${response.code()}")
                
                if (cachedRoles != null && cachedRoles == roles) {
                    Log.d(TAG, "Roles data unchanged, keeping existing cache")
                } else {
                    Log.d(TAG, "Updating role cache with ${roles.size} roles")
                    cachedRoles = roles
                }
                
                Result.Success(roles)
            } else {
                val error = Exception("Failed to fetch roles: ${response.code()} - ${response.message()}")
                Log.e(TAG, "API error: ${error.message}")
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching roles", e)
            Result.Error(e)
        }
    }

    suspend fun createRole(name: String, description: String): Result<RoleDTO> {
        return try {
            Log.d(TAG, "Creating role: $name")
            val roleRequest = RoleRequest(name = name, description = description)
            val response = apiService.createRole(roleRequest)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Role created successfully: $name")
                clearCache()
                Result.Success(response.body()!!)
            } else {
                val error = Exception("Failed to create role: ${response.code()} - ${response.message()}")
                Log.e(TAG, "Create role error: ${error.message}")
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception creating role", e)
            Result.Error(e)
        }
    }

    suspend fun updateRole(roleId: Int, name: String, description: String): Result<RoleDTO> {
        return try {
            Log.d(TAG, "Updating role ID: $roleId")
            val roleRequest = RoleRequest(name = name, description = description)
            val response = apiService.updateRole(roleId, roleRequest)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Role updated successfully: $name")
                clearCache()
                Result.Success(response.body()!!)
            } else {
                val error = Exception("Failed to update role: ${response.code()} - ${response.message()}")
                Log.e(TAG, "Update role error: ${error.message}")
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception updating role", e)
            Result.Error(e)
        }
    }

    suspend fun deleteRole(roleId: Int): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting role ID: $roleId")
            val response = apiService.deleteRole(roleId)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Role deleted successfully")
                clearCache()
                Result.Success(Unit)
            } else {
                val error = Exception("Failed to delete role: ${response.code()} - ${response.message()}")
                Log.e(TAG, "Delete role error: ${error.message}")
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception deleting role", e)
            Result.Error(e)
        }
    }

    suspend fun addPermissionToRole(roleName: String, permission: String): Result<Unit> {
        return try {
            Log.d(TAG, "Adding permission '$permission' to role: $roleName")
            val request = com.restaurantclient.data.dto.AddPermissionRequest(roleName = roleName, permission = permission)
            val response = apiService.addPermission(request)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Permission added successfully")
                clearCache()
                Result.Success(Unit)
            } else {
                val error = Exception("Failed to add permission: ${response.code()} - ${response.message()}")
                Log.e(TAG, "Add permission error: ${error.message}")
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception adding permission", e)
            Result.Error(e)
        }
    }

    suspend fun removePermissionFromRole(roleId: Int): Result<Unit> {
        return try {
            Log.d(TAG, "Removing all permissions from role ID: $roleId")
            val response = apiService.removePermission(roleId)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Permissions removed successfully")
                clearCache()
                Result.Success(Unit)
            } else {
                val error = Exception("Failed to remove permissions: ${response.code()} - ${response.message()}")
                Log.e(TAG, "Remove permissions error: ${error.message}")
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception removing permissions", e)
            Result.Error(e)
        }
    }
}
