package com.restaurantclient.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.databinding.ActivityOrderManagementBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderManagementActivity : BaseAdminActivity() {

    private lateinit var binding: ActivityOrderManagementBinding
    private val viewModel: OrderManagementViewModel by viewModels()
    private lateinit var adapter: OrderManagementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        setupObservers()
        setupRefresh()

        viewModel.loadOrders()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startPollingOrders()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPollingOrders()
    }

    private fun setupToolbar() {
        setupAdminToolbar(
            binding.adminToolbar.toolbar,
            getString(R.string.order_management_title),
            showBackButton = true
        )
    }

    private fun setupRecyclerView() {
        adapter = OrderManagementAdapter { order, status ->
            viewModel.updateOrderStatus(order.order_id, status.uppercase())
        }
        binding.ordersRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.ordersRecyclerView.adapter = adapter
    }

    private fun setupFilters() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val filter = when (selectedId) {
                R.id.filter_pending -> "Pending"
                R.id.filter_accepted -> "Accepted"
                R.id.filter_ready -> "Ready"
                R.id.filter_completed -> "Completed"
                R.id.filter_cancelled -> "Cancelled"
                else -> "ALL"
            }
            viewModel.setFilter(filter)
        }
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadOrders()
        }
    }

    private fun setupObservers() {
        viewModel.orders.observe(this) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data)
                    updateEmptyState(result.data.isEmpty())
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    updateEmptyState(true)
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    Toast.makeText(this, getString(R.string.order_list_error, message), Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.updateResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, getString(R.string.order_update_success), Toast.LENGTH_SHORT).show()
                    viewModel.loadOrders()
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    Toast.makeText(this, getString(R.string.order_update_error, message), Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.isVisible = isEmpty
        binding.ordersRecyclerView.isVisible = !isEmpty
    }
}
