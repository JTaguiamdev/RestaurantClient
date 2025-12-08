package com.orderly.data

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.orderly.data.dto.RoleDTO
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(private val prefs: SharedPreferences) {

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
        // Try to extract and save username from token
        try {
            val payload = decodeJWTPayload(token)
            Log.d("TokenManager", "JWT payload: $payload")
            
            // Try different possible username fields in JWT
            var username: String? = null
            
            if (payload.has("username")) {
                username = payload.getString("username")
                Log.d("TokenManager", "Found username in JWT: $username")
            } else if (payload.has("sub")) {
                username = payload.getString("sub")
                Log.d("TokenManager", "Found sub in JWT: $username")
            } else if (payload.has("user")) {
                username = payload.getString("user")
                Log.d("TokenManager", "Found user in JWT: $username")
            } else {
                Log.w("TokenManager", "No username field found in JWT")
            }
            
            if (!username.isNullOrEmpty()) {
                prefs.edit().putString(USERNAME_KEY, username).apply()
                Log.d("TokenManager", "Saved username: $username")
            } else {
                Log.w("TokenManager", "Username is null or empty, not saving")
            }

            // Try to extract and save role from token
            try {
                if (payload.has("role")) {
                    val role = payload.getString("role")
                    Log.d("TokenManager", "Found role in JWT: $role")
                    saveUserRole(role)
                } else if (payload.has("roles")) {
                    if (payload.isNull("roles")) { // Check if the "roles" key exists but its value is null
                        Log.d("TokenManager", "Roles field is null in JWT, defaulting to Customer")
                        saveUserRole("Customer")
                    } else {
                        // Your backend uses "roles" array with role IDs
                        val rolesArray = payload.getJSONArray("roles")
                        Log.d("TokenManager", "Found roles array in JWT: $rolesArray")
                        
                        if (rolesArray.length() > 0) {
                            val roleId = rolesArray.getInt(0) // Get first role ID
                            Log.d("TokenManager", "First role ID: $roleId")
                            
                            // Convert role ID to role name based on your system
                            val roleName = when (roleId) {
                                2 -> "Admin"     // Your admin role ID is 2
                                1 -> "Customer"  // Assuming customer role ID is 1
                                else -> "Customer" // Default to customer
                            }
                            Log.d("TokenManager", "Converted role ID $roleId to: $roleName")
                            saveUserRole(roleName)
                        } else {
                            Log.d("TokenManager", "Roles array is empty, defaulting to Customer")
                            saveUserRole("Customer")
                        }
                    }
                } else { // If neither 'role' nor 'roles' field exist
                    Log.d("TokenManager", "No role or roles field found in JWT, defaulting to Customer")
                    saveUserRole("Customer")
                }
            } catch (e: Exception) {
                Log.w("TokenManager", "Could not extract role from JWT", e)
            }
        } catch (e: Exception) {
            Log.e("TokenManager", "Could not extract username from token", e)
        }
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun deleteToken() {
        prefs.edit().remove(TOKEN_KEY).remove(USERNAME_KEY).remove(ROLE_KEY).apply()
    }

    fun getUsername(): String? {
        val username = prefs.getString(USERNAME_KEY, null)
        Log.d("TokenManager", "Retrieved username: $username")
        return username
    }

    fun saveUsername(username: String) {
        Log.d("TokenManager", "Saving username: $username")
        prefs.edit().putString(USERNAME_KEY, username).apply()
        Log.d("TokenManager", "Username saved successfully")
    }

    fun clearAll() {
        Log.d("TokenManager", "Clearing all authentication data")
        prefs.edit().clear().apply()
    }

    fun saveUserRole(role: String) {
        Log.d("TokenManager", "Saving user role: $role")
        // Store role in a normalized format for comparison
        val normalizedRole = when (role.lowercase()) {
            "admin" -> "Admin"
            "customer" -> "Customer"
            else -> role // Keep original if not recognized
        }
        Log.d("TokenManager", "Normalized role: $normalizedRole")
        prefs.edit().putString(ROLE_KEY, normalizedRole).apply()
    }

    fun saveUserRole(role: RoleDTO?) {
        role?.let {
            val roleString = when (it) {
                RoleDTO.Admin -> "Admin"
                RoleDTO.Customer -> "Customer"
            }
            saveUserRole(roleString)
        }
    }

    fun getUserRole(): RoleDTO? {
        val roleString = prefs.getString(ROLE_KEY, null)
        Log.d("TokenManager", "Retrieved role: $roleString")
        return when (roleString?.lowercase()) {
            "admin" -> RoleDTO.Admin
            "customer" -> RoleDTO.Customer
            else -> {
                Log.w("TokenManager", "Unknown role: $roleString")
                null
            }
        }
    }

    fun isAdmin(): Boolean {
        return getUserRole() == RoleDTO.Admin
    }

    fun isCustomer(): Boolean {
        return getUserRole() == RoleDTO.Customer
    }

    fun isTokenValid(): Boolean {
        val token = getToken()
        Log.d("TokenManager", "Checking token validity...")
        Log.d("TokenManager", "Token exists: ${token != null}")
        
        if (token == null) {
            Log.d("TokenManager", "No token found - user not logged in")
            return false
        }
        
        return try {
            val payload = decodeJWTPayload(token)
            val exp = payload.getLong("exp")
            val currentTime = System.currentTimeMillis() / 1000
            val isValid = exp > currentTime
            
            Log.d("TokenManager", "Token expiration: $exp")
            Log.d("TokenManager", "Current time: $currentTime")
            Log.d("TokenManager", "Token valid: $isValid")
            
            isValid
        } catch (e: Exception) {
            Log.e("TokenManager", "Error validating token", e)
            false
        }
    }

    fun shouldRefreshToken(): Boolean {
        val token = getToken() ?: return false
        return try {
            val payload = decodeJWTPayload(token)
            val exp = payload.getLong("exp")
            val currentTime = System.currentTimeMillis() / 1000
            val timeUntilExpiry = exp - currentTime
            timeUntilExpiry < 10 * 60 // Refresh if less than 10 minutes remaining
        } catch (e: Exception) {
            true
        }
    }

    private fun decodeJWTPayload(token: String): JSONObject {
        val parts = token.split(".")
        if (parts.size != 3) throw IllegalArgumentException("Invalid JWT token")
        
        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING)
        return JSONObject(String(decodedBytes))
    }

    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val USERNAME_KEY = "username"
        private const val ROLE_KEY = "user_role"
    }
}