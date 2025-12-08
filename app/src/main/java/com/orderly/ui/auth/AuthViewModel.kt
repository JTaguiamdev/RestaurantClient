package com.orderly.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderly.data.Result
import com.orderly.data.TokenManager
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.dto.NewUserDTO
import com.orderly.data.dto.RoleDTO
import com.orderly.data.dto.UserDTO
import com.orderly.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.CoroutineScope // Added import

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
    private val authViewModelScope: CoroutineScope // Injected CoroutineScope
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _registrationResult = MutableLiveData<Result<LoginResponse>>()
    val registrationResult: LiveData<Result<LoginResponse>> = _registrationResult

    private val _refreshResult = MutableLiveData<Result<LoginResponse>>()
    val refreshResult: LiveData<Result<LoginResponse>> = _refreshResult

    private var currentUser: UserDTO? = null

    fun login(loginDto: LoginDTO) {
        authViewModelScope.launch {
            android.util.Log.d("AuthViewModel", "Attempting login for username: ${loginDto.username}")
            val result = userRepository.login(loginDto)
            if (result is Result.Success) {
                android.util.Log.d("AuthViewModel", "Login successful!")
                android.util.Log.d("AuthViewModel", "Response token: ${result.data.token}")
                android.util.Log.d("AuthViewModel", "Response message: ${result.data.message}")
                android.util.Log.d("AuthViewModel", "Response user: ${result.data.user}")
                
                tokenManager.saveToken(result.data.token)
                
                // Check if backend returned user information
                if (result.data.user != null) {
                    val user = result.data.user
                    // Backend returned full user info with role
                    android.util.Log.d("AuthViewModel", "✅ Backend returned user info!")
                    android.util.Log.d("AuthViewModel", "Raw user object: $user")
                    android.util.Log.d("AuthViewModel", "User ID: ${user.userId}")
                    android.util.Log.d("AuthViewModel", "Username: ${user.username}")
                    android.util.Log.d("AuthViewModel", "Role Details (raw): ${user.roleDetails}")
                    android.util.Log.d("AuthViewModel", "Role Details name: ${user.roleDetails?.name}")
                    android.util.Log.d("AuthViewModel", "Computed Role: ${user.role}")
                    android.util.Log.d("AuthViewModel", "Created At: ${user.createdAt}")
                    
                    currentUser = user
                    tokenManager.saveUsername(user.username)
                    
                    // Handle role with better logging
                    if (user.role != null) {
                        tokenManager.saveUserRole(user.role)
                        android.util.Log.d("AuthViewModel", "✅ SAVED ROLE: ${user.role} for user: ${user.username}")
                        android.util.Log.d("AuthViewModel", "   - From backend role name: '${user.roleDetails?.name}'")
                        android.util.Log.d("AuthViewModel", "   - Converted to: ${user.role}")
                    } else {
                        android.util.Log.w("AuthViewModel", "⚠️ Backend returned user but role conversion failed!")
                        android.util.Log.w("AuthViewModel", "   - Raw role details: ${user.roleDetails}")
                        android.util.Log.w("AuthViewModel", "   - Role name: '${user.roleDetails?.name}'")
                    }
                } else {
                    // Backend only returned token, save username and determine role later
                    android.util.Log.w("AuthViewModel", "❌ Backend didn't return user info, will determine role manually")
                    tokenManager.saveUsername(loginDto.username)
                    
                    // Create minimal user object
                    currentUser = UserDTO(
                        userId = null,
                        username = loginDto.username,
                        roleDetails = null, // Will be determined later
                        createdAt = null,
                        updatedAt = null
                    )
                    
                    // Try to determine role by checking admin endpoints only if no role was derived from the JWT
                    if (tokenManager.getUserRole() == null) {
                        android.util.Log.d("AuthViewModel", "Attempting to determine role by testing admin access (no role from JWT)...")
                        determineUserRole(loginDto.username)
                    } else {
                        android.util.Log.d("AuthViewModel", "Skipping manual role determination (role already derived from JWT): ${tokenManager.getUserRole()}")
                    }
                }
            } else {
                android.util.Log.e("AuthViewModel", "Login failed: $result")
            }
            _loginResult.postValue(result)
        }
    }

    fun register(newUserDto: NewUserDTO) {
        authViewModelScope.launch {
            android.util.Log.d("AuthViewModel", "Attempting registration for username: ${newUserDto.username}")
            val result = userRepository.register(newUserDto)
            if (result is Result.Success) {
                android.util.Log.d("AuthViewModel", "Registration successful, saving token")
                tokenManager.saveToken(result.data.token)
                
                // Check if backend returned user information  
                if (result.data.user != null) {
                    val user = result.data.user
                    // Backend returned full user info with role
                    android.util.Log.d("AuthViewModel", "✅ Registration: Backend returned user info!")
                    android.util.Log.d("AuthViewModel", "User: ${user.username}, Role Details: ${user.roleDetails}")
                    android.util.Log.d("AuthViewModel", "Computed Role: ${user.role}")
                    
                    currentUser = user
                    tokenManager.saveUsername(user.username)
                    
                    if (user.role != null) {
                        tokenManager.saveUserRole(user.role)
                        android.util.Log.d("AuthViewModel", "✅ SAVED REGISTRATION ROLE: ${user.role}")
                    } else {
                        // For registration, assume admin if no role
                        val adminRole = RoleDTO.Admin
                        tokenManager.saveUserRole(adminRole)
                        android.util.Log.d("AuthViewModel", "⚠️ No role from backend, assumed admin for registration")
                    }
                } else {
                    // Backend only returned token, save username and assume first user is admin
                    android.util.Log.d("AuthViewModel", "Backend didn't return user info for registration")
                    tokenManager.saveUsername(newUserDto.username)
                    
                    // For registration, first user is typically admin
                    val adminRole = RoleDTO.Admin
                    tokenManager.saveUserRole(adminRole)
                    
                    currentUser = UserDTO(
                        userId = null,
                        username = newUserDto.username,
                        roleDetails = null, // Create simple admin role details
                        createdAt = null,
                        updatedAt = null
                    )
                    android.util.Log.d("AuthViewModel", "Assumed admin role for first registration")
                }
            } else {
                android.util.Log.e("AuthViewModel", "Registration failed: $result")
            }
            _registrationResult.postValue(result)
        }
    }

    fun refreshToken() {
        authViewModelScope.launch {
            val result = userRepository.refreshToken()
            if (result is Result.Success) {
                tokenManager.saveToken(result.data.token)
                
                // Try to update user info if token refresh includes user data
                result.data.user?.let { user ->
                    currentUser = user
                    tokenManager.saveUserRole(user.role)
                    android.util.Log.d("AuthViewModel", "Token refresh included user info")
                } ?: run {
                    android.util.Log.w("AuthViewModel", "Token refresh didn't include user info")
                }
            } else {
                // Refresh failed, clear token and require re-login
                tokenManager.deleteToken()
            }
            _refreshResult.postValue(result)
        }
    }

    fun isLoggedIn(): Boolean {
        val hasToken = tokenManager.getToken() != null
        val isTokenValid = tokenManager.isTokenValid()
        val hasUsername = tokenManager.getUsername() != null
        
        // Both token AND username are required for complete authentication
        val isCompletelyLoggedIn = isTokenValid && hasUsername
        
        // android.util.Log.d("AuthViewModel", "Token exists: $hasToken")
        // android.util.Log.d("AuthViewModel", "Token valid: $isTokenValid") 
        // android.util.Log.d("AuthViewModel", "Username exists: $hasUsername")
        // android.util.Log.d("AuthViewModel", "Overall logged in: $isCompletelyLoggedIn (requires both token AND username)")
        
        if (isTokenValid && !hasUsername) {
            // android.util.Log.w("AuthViewModel", "Token is valid but username is missing - clearing token to force re-login")
            tokenManager.deleteToken()
            return false
        }
        
        return isCompletelyLoggedIn
    }

    fun shouldRefreshToken(): Boolean {
        return tokenManager.shouldRefreshToken()
    }

    fun logout() {
        currentUser = null
        tokenManager.deleteToken()
    }

    // Role-based methods
    fun getUserRole(): RoleDTO? {
        return currentUser?.role ?: tokenManager.getUserRole()
    }

    fun isAdmin(): Boolean {
        return getUserRole() == RoleDTO.Admin
    }

    fun isCustomer(): Boolean {
        return getUserRole() == RoleDTO.Customer
    }

    fun getCurrentUser(): UserDTO? {
        return currentUser
    }

    // Load user info from stored data on app start
    fun loadStoredUserInfo() {
        val role = tokenManager.getUserRole()
        val username = tokenManager.getUsername()
        
        if (role != null && username != null) {
            // Create a basic user object from stored data
            currentUser = UserDTO(
                userId = null,
                username = username,
                roleDetails = null, // Role will be computed from stored role
                createdAt = null,
                updatedAt = null
            )
            android.util.Log.d("AuthViewModel", "Loaded stored user: $username, role: $role")
        } else if (username != null && role == null) {
            // Create user without role for now
            currentUser = UserDTO(
                userId = null,
                username = username,
                roleDetails = null,
                createdAt = null,
                updatedAt = null
            )
            android.util.Log.w("AuthViewModel", "No role stored for user: $username")
        }
    }
    
    // Method to set user role (for external role determination)
    fun setUserRole(role: RoleDTO) {
        android.util.Log.d("AuthViewModel", "Setting user role to: $role")
        tokenManager.saveUserRole(role)
        currentUser?.let {
            currentUser = it.copy(roleDetails = null) // We'll store the role via TokenManager
        }
    }

    // Fallback method to determine user role by testing admin endpoints
    private fun determineUserRole(username: String) {
        authViewModelScope.launch {
            android.util.Log.d("AuthViewModel", "🔍 Testing admin access for user: $username")
            
            try {
                // Try to access admin endpoint to determine if user is admin
                android.util.Log.d("AuthViewModel", "Making request to admin/users endpoint...")
                val result = userRepository.getAllUsers()
                
                android.util.Log.d("AuthViewModel", "Admin endpoint result: $result")
                
                val isAdmin = result is Result.Success
                val determinedRole = if (isAdmin) RoleDTO.Admin else RoleDTO.Customer
                
                android.util.Log.d("AuthViewModel", "🎯 DETERMINED ROLE for $username: $determinedRole (admin access: $isAdmin)")
                
                // Store determined role
                tokenManager.saveUserRole(determinedRole)
                
                // Update current user object
                currentUser = currentUser?.copy(roleDetails = null) ?: UserDTO(
                    userId = null,
                    username = username,
                    roleDetails = null, // Role stored via TokenManager
                    createdAt = null,
                    updatedAt = null
                )
                
                android.util.Log.d("AuthViewModel", "✅ Role determination complete. Final role: $determinedRole")
                
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "❌ Failed to determine user role for $username, defaulting to Customer", e)
                android.util.Log.e("AuthViewModel", "Error details: ${e.message}")
                
                // Default to customer if can't determine
                val defaultRole = RoleDTO.Customer
                tokenManager.saveUserRole(defaultRole)
                currentUser = currentUser?.copy(roleDetails = null) ?: UserDTO(
                    userId = null,
                    username = username,
                    roleDetails = null, // Role stored via TokenManager
                    createdAt = null,
                    updatedAt = null
                )
            }
        }
    }
}