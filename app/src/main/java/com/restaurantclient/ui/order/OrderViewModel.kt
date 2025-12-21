package com.restaurantclient.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.CreateOrderRequest
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _createOrderResult = MutableLiveData<Result<OrderResponse>>()
    val createOrderResult: LiveData<Result<OrderResponse>> = _createOrderResult

    private val _userOrders = MutableLiveData<Result<List<OrderResponse>>>()
    val userOrders: LiveData<Result<List<OrderResponse>>> = _userOrders
    
    private var isCreatingOrder = false
    private var pollingJob: Job? = null

    fun createOrder(createOrderRequest: CreateOrderRequest, username: String) { // Pass username to refresh orders
        if (isCreatingOrder) return
        
        isCreatingOrder = true
        viewModelScope.launch {
            val result = orderRepository.createOrder(createOrderRequest)
            _createOrderResult.postValue(result)
            isCreatingOrder = false
            
            // Force refresh user orders after creating a new order
            if (result is Result.Success) {
                fetchUserOrders(username, forceRefresh = true)
            }
        }
    }

    fun fetchUserOrders(username: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val result = orderRepository.getUserOrders(username, forceRefresh)
            _userOrders.postValue(result)
        }
    }

    fun startPollingOrders(username: String) {
        if (pollingJob?.isActive == true) return
        
        pollingJob = viewModelScope.launch {
            while (true) {
                fetchUserOrders(username, forceRefresh = true)
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    fun stopPollingOrders() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingOrders()
    }
}
