package com.orderly

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.data.dto.RoleDTO
import com.orderly.ui.admin.AdminDashboardActivity
import com.orderly.ui.auth.AuthViewModel
import com.orderly.ui.auth.LoginActivity
import com.orderly.ui.product.ProductListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("MainActivity", "Login successful, determining role and navigating...")
            // After successful login, the AuthViewModel has already processed the login
            // and determined the role. We can now navigate based on role.
            determineUserRoleAndNavigate()
        } else {
            // User cancelled login, so finish the app
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "Checking authentication status...")
        val isLoggedIn = authViewModel.isLoggedIn()
        Log.d("MainActivity", "User logged in: $isLoggedIn")
        
        if (isLoggedIn) {
            Log.d("MainActivity", "User is logged in, determining user role...")
            determineUserRoleAndNavigate()
        } else {
            Log.d("MainActivity", "User not logged in, launching LoginActivity")
            loginLauncher.launch(Intent(this, LoginActivity::class.java))
        }
    }
    
    private fun determineUserRoleAndNavigate() {
        authViewModel.loadStoredUserInfo()
        
        val storedRole = authViewModel.getUserRole()
        Log.d("MainActivity", "🔍 Checking stored role: $storedRole")
        
        if (storedRole != null) {
            // We have a stored role, use it immediately
            Log.d("MainActivity", "✅ Found stored role: $storedRole")
            navigateBasedOnRole()
        } else {
            // No stored role - this means either:
            // 1. Backend didn't return user info and we need to determine role
            // 2. Role determination is in progress (async)
            Log.d("MainActivity", "❌ No stored role found")
            
            // Wait a moment for async role determination to complete
            waitForRoleOrFallback()
        }
    }
    
    private fun waitForRoleOrFallback() {
        Log.d("MainActivity", "⏳ Waiting for role determination...")
        
        // Simple single check with short delay to allow async role determination
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val role = authViewModel.getUserRole()
            Log.d("MainActivity", "🔍 Role check result: $role")
            
            if (role != null) {
                Log.d("MainActivity", "🎯 Role found: $role")
                navigateBasedOnRole()
            } else {
                Log.w("MainActivity", "⚠️ No role found, using fallback detection")
                determineRoleByAdminAccess()
            }
        }, 1500) // Wait 1.5 seconds for async role determination
    }
    
    private fun determineRoleByAdminAccess() {
        val username = authViewModel.getCurrentUser()?.username ?: ""
        Log.d("MainActivity", "🔍 Determining role for username: '$username'")
        
        // TODO: FOR DEBUGGING - Force admin role if you know you should be admin
        // Uncomment the next 4 lines to force admin role for testing:
        // Log.d("MainActivity", "🧪 DEBUG: Forcing admin role for testing")
        // authViewModel.setUserRole(RoleDTO.Admin)
        // goToAdminDashboard()
        // return
        
        // Smart admin detection based on username patterns
        val isLikelyAdmin = username.contains("admin", ignoreCase = true) || 
                           username == "admin" || 
                           username.lowercase().startsWith("admin") ||
                           isLikelyFirstUser(username)
        
        Log.d("MainActivity", "🤖 Admin detection for '$username':")
        Log.d("MainActivity", "   - Contains 'admin': ${username.contains("admin", ignoreCase = true)}")
        Log.d("MainActivity", "   - Equals 'admin': ${username == "admin"}")
        Log.d("MainActivity", "   - Starts with 'admin': ${username.lowercase().startsWith("admin")}")
        Log.d("MainActivity", "   - Likely first user: ${isLikelyFirstUser(username)}")
        Log.d("MainActivity", "   - 🎯 Final decision: isAdmin = $isLikelyAdmin")
        
        if (isLikelyAdmin) {
            Log.d("MainActivity", "✅ Username suggests admin role, navigating to admin dashboard")
            authViewModel.setUserRole(RoleDTO.Admin)
            goToAdminDashboard()
        } else {
            Log.d("MainActivity", "👤 Assuming customer role, navigating to product list")
            authViewModel.setUserRole(RoleDTO.Customer)
            goToProductList()
        }
    }
    
    private fun isLikelyFirstUser(username: String): Boolean {
        // Simple heuristics for first user detection
        return username.length < 10 && // Short usernames are often admin
               !username.contains("user", ignoreCase = true) && // Not explicitly a user
               !username.contains("customer", ignoreCase = true) // Not explicitly a customer
    }

    private fun navigateBasedOnRole() {
        val userRole = authViewModel.getUserRole()
        Log.d("MainActivity", "User role: $userRole")
        
        when {
            authViewModel.isAdmin() -> {
                Log.d("MainActivity", "User is admin, navigating to AdminDashboard")
                goToAdminDashboard()
            }
            authViewModel.isCustomer() -> {
                Log.d("MainActivity", "User is customer, navigating to ProductList")
                goToProductList()
            }
            else -> {
                Log.w("MainActivity", "Unknown user role: $userRole, defaulting to ProductList")
                goToProductList()
            }
        }
    }

    private fun goToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToProductList() {
        val intent = Intent(this, ProductListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}