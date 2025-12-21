package com.restaurantclient.ui.casher

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.databinding.ActivityCasherDashboardBinding
import com.restaurantclient.ui.common.setupGlassEffect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CasherDashboardActivity : BaseCasherActivity() {

    private lateinit var binding: ActivityCasherDashboardBinding
    private val viewModel: CasherDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCasherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBlurEffects()
        setupClickListeners()
        setupObservers()

        viewModel.loadDashboardData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startPollingStats()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPollingStats()
    }

    private fun setupToolbar() {
        setupCasherToolbar(
            binding.toolbarContainer.toolbar,
            getString(R.string.casher_dashboard_title),
            showBackButton = false
        )
    }

    private fun setupBlurEffects() {
        // Stats Cards
        binding.statPendingBlur.setupGlassEffect(15f)
        binding.statTotalOrdersBlur.setupGlassEffect(15f)
        
        // Buttons
        binding.btnOrdersBlur.setupGlassEffect(20f)
    }

    private fun setupClickListeners() {
        binding.btnManageOrders.setOnClickListener {
            // Navigate to Order Queue
            startActivity(Intent(this, CasherOrderActivity::class.java))
        }
    }

    private fun setupObservers() {
        // Initial welcome text from cached data
        val cachedUsername = authViewModel.getCurrentUser()?.username 
            ?: authViewModel.getUserRole()?.name 
            ?: "Cashier"
        binding.welcomeText.text = getString(R.string.casher_welcome, cachedUsername)

        viewModel.dashboardStats.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val stats = result.data
                    binding.statPendingCount.text = stats.pendingOrders.toString()
                    binding.statTotalOrdersCount.text = stats.orderCount.toString()
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    android.widget.Toast.makeText(this, "Failed to load stats: $message", android.widget.Toast.LENGTH_LONG).show()
                    binding.statPendingCount.text = "-"
                    binding.statTotalOrdersCount.text = "-"
                }
            }
        }

        viewModel.currentUser.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.welcomeText.text = getString(R.string.casher_welcome, result.data.username)
                }
                is Result.Error -> {
                    // Fallback to stored username
                    authViewModel.loadStoredUserInfo()
                    val username = authViewModel.getCurrentUser()?.username ?: "Cashier"
                    binding.welcomeText.text = getString(R.string.casher_welcome, username)
                    android.util.Log.e("CasherDashboard", "Failed to fetch user details: ${result.exception.message}")
                }
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_main_menu, menu) // Reuse admin menu for logout for now
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
}

