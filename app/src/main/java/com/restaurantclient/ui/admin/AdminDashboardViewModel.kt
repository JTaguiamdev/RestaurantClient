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
import com.restaurantclient.data.repository.RoleRepository
import com.restaurantclient.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val roleRepository: RoleRepository,
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
    private var pollingJob: Job? = null

    fun loadDashboardData(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _loading.value = true
            
            try {
                // Fetch basic stats from repositories directly for most accurate live data
                val usersResult = userRepository.getAllUsers(forceRefresh = true)
                val ordersResult = orderRepository.getAllOrders(forceRefresh = true)
                val productsResult = productRepository.getAllProducts(forceRefresh = true)
                val rolesResult = roleRepository.getAllRoles(forceRefresh = true)

                if (usersResult is Result.Success && ordersResult is Result.Success && productsResult is Result.Success) {
                    val users = usersResult.data
                    val rolesCount = if (rolesResult is Result.Success) rolesResult.data.size else 0
                    
                    val stats = DashboardStats(
                        totalUsers = users.size,
                        totalOrders = ordersResult.data.size,
                        totalProducts = productsResult.data.size,
                        totalRoles = rolesCount,
                        newUsersToday = calculateNewUsersToday(users),
                        pendingOrders = ordersResult.data.count { it.status?.equals("Pending", true) == true },
                        completedOrders = ordersResult.data.count { it.status?.equals("Completed", true) == true },
                        cancelledOrders = ordersResult.data.count { it.status?.equals("Cancelled", true) == true }
                    )
                    _dashboardStats.value = stats
                    _recentUsers.value = users.takeLast(5).reversed()
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminDashboardVM", "Error loading data", e)
            } finally {
                if (showLoading) _loading.value = false
            }
        }
    }

    fun startPollingStats() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (true) {
                loadDashboardData(showLoading = false)
                delay(5000)
            }
        }
    }

    fun stopPollingStats() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingStats()
    }

    private fun calculateNewUsersToday(users: List<UserDTO>): Int {
        val today = LocalDate.now()
        // Format from backend is often "dd/MM/yyyy" or "yyyy-MM-dd"
        return users.count { user ->
            user.createdAt?.let { createdAt ->
                try {
                    // Try dd/MM/yyyy first
                    val date = if (createdAt.contains("/")) {
                        LocalDate.parse(createdAt, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } else {
                        // Fallback to yyyy-MM-dd
                        LocalDate.parse(createdAt.substring(0, 10))
                    }
                    date == today
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }
    }

    fun refreshData() {
        userRepository.clearCache()
        orderRepository.clearCache()
        productRepository.clearCache()
        roleRepository.clearCache()
        loadDashboardData()
    }
}

data class DashboardStats(
    val totalUsers: Int,
    val totalOrders: Int,
    val totalProducts: Int,
    val totalRoles: Int = 0,
    val newUsersToday: Int,
    val pendingOrders: Int = 0,
    val completedOrders: Int = 0,
    val cancelledOrders: Int = 0
)
