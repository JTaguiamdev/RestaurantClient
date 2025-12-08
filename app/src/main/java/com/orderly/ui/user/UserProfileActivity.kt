package com.orderly.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.MainActivity
import com.orderly.databinding.ActivityUserProfileBinding
import com.orderly.ui.admin.AdminDashboardActivity
import com.orderly.ui.admin.UserManagementActivity
import com.orderly.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUserInfo()
        setupClickListeners()
    }

    private fun setupUserInfo() {
        // Load stored user info
        authViewModel.loadStoredUserInfo()
        
        val currentUser = authViewModel.getCurrentUser()
        val userRole = authViewModel.getUserRole()

        // Display user information
        binding.usernameText.text = currentUser?.username ?: "Unknown"
        binding.roleText.text = when (userRole?.name) {
            "Admin" -> "Administrator"
            "Customer" -> "Customer"
            else -> "Unknown Role"
        }
        binding.createdAtText.text = currentUser?.createdAt ?: "Unknown"

        // Show/hide admin features based on role
        if (authViewModel.isAdmin()) {
            // Show admin-specific UI elements (implement in layout if needed)
            setupAdminFeatures()
        } else {
            // Hide admin features for customers
            hideAdminFeatures()
        }
    }

    private fun setupAdminFeatures() {
        // Add admin-specific buttons/features here if needed in future
        // For now, admin features are accessible through dashboard
    }

    private fun hideAdminFeatures() {
        // Hide any admin-specific UI elements
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Add admin dashboard button if user is admin
        if (authViewModel.isAdmin()) {
            // You can add a button to go back to dashboard here if needed
        }
    }
}