package com.restaurantclient.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.databinding.ActivityUserProfileBinding
import com.restaurantclient.ui.admin.AdminDashboardActivity
import com.restaurantclient.ui.admin.OrderManagementActivity
import com.restaurantclient.ui.admin.UserManagementActivity
import com.restaurantclient.ui.auth.AuthViewModel
import com.restaurantclient.ui.common.setupGlassEffect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    private var currentUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGlassUI()
        setupUserInfo()
        setupClickListeners()
        setupObservers()

        currentUserId = authViewModel.getUserId()
        currentUserId?.let { userId ->
            userProfileViewModel.loadUserProfile(userId)
        } ?: run {
            Toast.makeText(this, "User ID not found, cannot load profile.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customerToolbar.toolbar)
        supportActionBar?.apply {
            title = "My Profile"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.customerToolbar.toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun setupGlassUI() {
        // Setup glass effect for profile card
        binding.profileGlassCard.setupGlassEffect(25f)
        binding.profileGlassCard.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND)
        binding.profileGlassCard.clipToOutline = true
    }

    private fun setupUserInfo() {
        authViewModel.loadStoredUserInfo()
        
        val currentUser = authViewModel.getCurrentUser()
        val userRole = authViewModel.getUserRole()

        binding.usernameText.text = currentUser?.username ?: getString(R.string.profile_unknown_username)
        binding.roleChip.text = when (userRole) {
            RoleDTO.Admin -> getString(R.string.role_admin_label)
            RoleDTO.Customer -> getString(R.string.role_customer_label)
            RoleDTO.Casher -> "Cashier"
            else -> getString(R.string.profile_unknown_role)
        }
        binding.roleChip.isVisible = userRole != null

        when {
            authViewModel.isAdmin() -> setupAdminShortcuts()
            authViewModel.isCasher() -> setupCasherShortcuts()
            else -> setupCustomerShortcuts()
        }
    }

    private fun setupCasherShortcuts() {
        binding.adminActionsCard.isVisible = false
        binding.customerActionsCard.isVisible = false
        binding.casherActionsCard.isVisible = true

        // Setup blur for casher actions card
        binding.casherActionsBlur.let { blurView ->
            val whiteOverlay = ContextCompat.getColor(this, R.color.white_glass_overlay)
            blurView.setOverlayColor(whiteOverlay)
            blurView.setupGlassEffect(20f)
        }

        binding.casherDashboardButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.casher.CasherDashboardActivity::class.java))
        }
        binding.casherOrdersButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.casher.CasherOrderActivity::class.java))
        }
    }

    private fun setupAdminShortcuts() {
        binding.adminActionsCard.isVisible = true
        binding.customerActionsCard.isVisible = false
        
        // Setup blur for admin actions card
        binding.adminActionsBlur.let { blurView ->
            val whiteOverlay = ContextCompat.getColor(this, R.color.white_glass_overlay)
            blurView.setOverlayColor(whiteOverlay)
            blurView.setupGlassEffect(20f)
        }

        binding.adminDashboardButton.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        }
        binding.adminUsersButton.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }
        binding.adminOrdersButton.setOnClickListener {
            startActivity(Intent(this, OrderManagementActivity::class.java))
        }
    }

    private fun setupCustomerShortcuts() {
        binding.adminActionsCard.isVisible = false
        binding.customerActionsCard.isVisible = true
        
        // Setup blur for customer actions card
        binding.customerActionsBlur.let { blurView ->
            val whiteOverlay = ContextCompat.getColor(this, R.color.white_glass_overlay)
            blurView.setOverlayColor(whiteOverlay)
            blurView.setupGlassEffect(20f)
        }

        binding.customerOrdersButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.order.MyOrdersActivity::class.java))
        }
        binding.customerCartButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.cart.ShoppingCartActivity::class.java))
        }
        binding.customerCheckoutButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.checkout.CheckoutActivity::class.java))
        }
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        userProfileViewModel.userProfile.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    binding.usernameText.text = user.username // Update displayed username
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    Toast.makeText(this, "Failed to load user profile: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
