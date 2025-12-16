package com.restaurantclient.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.UserDTO
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
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _dashboardStats = MutableLiveData<DashboardStats>()
    val dashboardStats: LiveData<DashboardStats> = _dashboardStats

    private val _recentUsers = MutableLiveData<List<UserDTO>>()
    val recentUsers: LiveData<List<UserDTO>> = _recentUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadDashboardData() {
        viewModelScope.launch {
            _loading.value = true
            
            try {
                val usersResult = userRepository.getAllUsers()
                val ordersResult = orderRepository.getAllOrders()
                val productsResult = productRepository.getAllProducts()

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
                    newUsersToday = calculateNewUsersToday(users)
                )

                _dashboardStats.value = stats
                _recentUsers.value = users
                    .sortedBy { it.createdAt }
                    .takeLast(5)
                    .reversed()

                _error.value = if (errors.isNotEmpty()) errors.joinToString("\n") else null
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun calculateNewUsersToday(users: List<UserDTO>): Int {
        val today = LocalDate.now()
        return users.count { user ->
            user.createdAt?.let { createdAt ->
                runCatching { LocalDate.parse(createdAt.take(10)) }.getOrNull() == today
            } ?: false
        }
    }

    fun refreshData() {
        loadDashboardData()
    }
}

data class DashboardStats(
    val totalUsers: Int,
    val totalOrders: Int,
    val totalProducts: Int,
    val newUsersToday: Int
)
