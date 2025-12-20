package com.restaurantclient.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderManagementViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableLiveData<Result<List<OrderResponse>>>()
    val orders: LiveData<Result<List<OrderResponse>>> = _orders

    private val _updateResult = MutableLiveData<Result<OrderResponse>>()
    val updateResult: LiveData<Result<OrderResponse>> = _updateResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadOrders(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = orderRepository.getAllOrders(forceRefresh)
                _orders.value = result
            } catch (e: Exception) {
                _orders.value = Result.Error(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                val result = orderRepository.updateOrderStatus(orderId, newStatus)
                _updateResult.value = result
                orderRepository.clearCache() // Invalidate cache after update
                loadOrders(true) // Refresh orders
            } catch (e: Exception) {
                _updateResult.value = Result.Error(e)
            }
        }
    }
}
