package com.restaurantclient.ui.admin

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.eightbitlab.com.blurview.BlurView
import com.eightbitlab.com.blurview.RenderScriptBlur
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.databinding.ActivityAdminDashboardBinding
import com.restaurantclient.ui.product.ProductListActivity
import com.restaurantclient.ui.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardActivity : BaseAdminActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val adminViewModel: AdminDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupToolbar()
        setupGlassEffects()
        setupClickListeners()
        setupObservers()
        
        // Load dashboard data
        adminViewModel.loadDashboardData()
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.admin_primary)
    }

    private fun setupToolbar() {
        setupAdminToolbar(binding.adminToolbar.toolbar, getString(R.string.admin_dashboard_title))
    }

    private fun setupClickListeners() {
        // User Management Card
        binding.userManagementCard.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }

        // Product Management Card
        binding.productManagementCard.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        // Order Management Card
        binding.orderManagementCard.setOnClickListener {
            startActivity(Intent(this, OrderManagementActivity::class.java))
        }

        // Quick Actions
        binding.addUserButton.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
        }

        binding.viewReportsButton.setOnClickListener {
            showReportsPlaceholderDialog()
        }
    }

    private fun setupObservers() {
        adminViewModel.dashboardStats.observe(this) { stats ->
            binding.totalUsersText.text = stats.totalUsers.toString()
            binding.totalOrdersText.text = stats.totalOrders.toString()
            binding.totalProductsText.text = stats.totalProducts.toString()
            binding.newUsersText.text = stats.newUsersToday.toString()
        }
    }

    private fun bindRoleChip(chip: Chip, role: RoleDTO?) {
        val resolvedRole = role ?: RoleDTO.Customer
        chip.text = resolvedRole.name.uppercase()
        val colorRes = if (resolvedRole == RoleDTO.Admin) R.color.admin_primary else R.color.admin_secondary
        chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))
    }

    private fun formatDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) {
            return getString(R.string.label_unknown_date)
        }
        return rawDate.substringBefore("T")
    }

    private fun showReportsPlaceholderDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.reports_dialog_title)
            .setMessage(R.string.reports_dialog_message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            R.id.action_user_management -> {
                startActivity(Intent(this, UserManagementActivity::class.java))
                true
            }
            R.id.action_product_list -> {
                startActivity(Intent(this, ProductListActivity::class.java))
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        authViewModel.logout()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Refresh dashboard data when returning to screen
        adminViewModel.loadDashboardData()
    }

    private fun setupGlassEffects() {
        val radius = 25f
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background
        
        // Admin badge blur with white overlay
        binding.adminBadgeBlurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
        val adminOverlay = ContextCompat.getColor(this, R.color.admin_glass_overlay)
        binding.adminBadgeBlurView.setOverlayColor(adminOverlay)
        
        // White glassmorphism overlay for statistics cards
        val whiteOverlay = ContextCompat.getColor(this, R.color.white_glass_overlay)
        
        binding.totalUsersBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(20f)
        binding.totalUsersBlur.setOverlayColor(whiteOverlay)
        
        binding.totalOrdersBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(20f)
        binding.totalOrdersBlur.setOverlayColor(whiteOverlay)
        
        binding.totalProductsBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(20f)
        binding.totalProductsBlur.setOverlayColor(whiteOverlay)
        
        binding.newUsersBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(20f)
        binding.newUsersBlur.setOverlayColor(whiteOverlay)
        
        // Management cards
        binding.userManagementBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(20f)
        binding.userManagementBlur.setOverlayColor(whiteOverlay)
        
        binding.productManagementBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(20f)
        binding.productManagementBlur.setOverlayColor(whiteOverlay)
        
        binding.orderManagementBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(20f)
        binding.orderManagementBlur.setOverlayColor(whiteOverlay)
    }
}
