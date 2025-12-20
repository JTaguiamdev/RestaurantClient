package com.restaurantclient.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.DashboardSummaryDTO
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.data.repository.DashboardRepository
import com.restaurantclient.data.repository.OrderRepository
import com.restaurantclient.data.repository.ProductRepository
import com.restaurantclient.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _dashboardStats = MutableLiveData<DashboardStats>()
    val dashboardStats: LiveData<DashboardStats> = _dashboardStats
    
    private val _dashboardSummary = MutableLiveData<DashboardSummaryDTO>()
    val dashboardSummary: LiveData<DashboardSummaryDTO> = _dashboardSummary

    private val _recentUsers = MutableLiveData<List<UserDTO>>()
    val recentUsers: LiveData<List<UserDTO>> = _recentUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private var dashboardSummaryAvailable = true // Track if endpoint is available

    fun loadDashboardData() {
        viewModelScope.launch {
            _loading.value = true
            
            try {
                // Try to load from new dashboard summary API first (if available)
                if (dashboardSummaryAvailable) {
                    val summaryResult = dashboardRepository.getDashboardSummary()
                    
                    if (summaryResult is Result.Success) {
                        val summary = summaryResult.data
                        _dashboardSummary.value = summary
                        
                        // Convert to DashboardStats for backward compatibility
                        val stats = DashboardStats(
                            totalUsers = summary.userCount,
                            totalOrders = summary.orderCount,
                            totalProducts = summary.productCount,
                            newUsersToday = 0, // Not provided by summary API
                            pendingOrders = summary.pendingOrders,
                            completedOrders = summary.completedOrders,
                            cancelledOrders = summary.cancelledOrders
                        )
                        _dashboardStats.value = stats
                        
                        // Load recent users separately
                        loadRecentUsers()
                    } else {
                        // If it's a 404, disable future attempts
                        if (summaryResult is Result.Error && 
                            summaryResult.exception.message?.contains("404") == true) {
                            android.util.Log.w("AdminDashboardVM", "Dashboard summary endpoint not available, using fallback")
                            dashboardSummaryAvailable = false
                        }
                        // Fallback to loading data from individual endpoints
                        loadDashboardDataFallback()
                    }
                } else {
                    // Directly use fallback if we know the endpoint isn't available
                    loadDashboardDataFallback()
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
                // Fallback to loading data from individual endpoints
                loadDashboardDataFallback()
            } finally {
                _loading.value = false
            }
        }
    }
    
    private suspend fun loadRecentUsers() {
        val usersResult = userRepository.getAllUsers(forceRefresh = true)
        if (usersResult is Result.Success) {
            _recentUsers.value = usersResult.data
                .sortedBy { it.createdAt }
                .takeLast(5)
                .reversed()
        }
    }
    
    private suspend fun loadDashboardDataFallback() {
        val usersResult = userRepository.getAllUsers(forceRefresh = true)
        val ordersResult = orderRepository.getAllOrders(forceRefresh = true)
        val productsResult = productRepository.getAllProducts(forceRefresh = true)

        val errors = mutableListOf<String>()

        val users = when (usersResult) {
            is Result.Success -> usersResult.data
            is Result.Error -> {
                errors += "Users: ${usersResult.exception.message}"
                emptyList()
            }
        }

        val totalOrders = when (ordersResult) {
            is Result.Success -> ordersResult.data.size
            is Result.Error -> {
                errors += "Orders: ${ordersResult.exception.message}"
                0
            }
        }

        val totalProducts = when (productsResult) {
            is Result.Success -> productsResult.data.size
            is Result.Error -> {
                errors += "Products: ${productsResult.exception.message}"
                0
            }
        }

        val stats = DashboardStats(
            totalUsers = users.size,
            totalOrders = totalOrders,
            totalProducts = totalProducts,
            newUsersToday = calculateNewUsersToday(users),
            pendingOrders = 0,
            completedOrders = 0,
            cancelledOrders = 0
        )

        _dashboardStats.value = stats
        _recentUsers.value = users
            .sortedBy { it.createdAt }
            .takeLast(5)
            .reversed()

        _error.value = if (errors.isNotEmpty()) errors.joinToString("\n") else null
    }

    private fun calculateNewUsersToday(users: List<UserDTO>): Int {
        val today = LocalDate.now()
        return users.count { user ->
            user.createdAt?.let { createdAt ->
                runCatching { LocalDate.parse(createdAt.substring(0, 10)) }.getOrNull() == today
            } ?: false
        }
    }

    fun refreshData() {
        userRepository.clearCache()
        orderRepository.clearCache()
        productRepository.clearCache()
        loadDashboardData()
    }
}

data class DashboardStats(
    val totalUsers: Int,
    val totalOrders: Int,
    val totalProducts: Int,
    val newUsersToday: Int,
    val pendingOrders: Int = 0,
    val completedOrders: Int = 0,
    val cancelledOrders: Int = 0
)
