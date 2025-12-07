package com.orderly.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderly.data.Result
import com.orderly.data.dto.CreateOrderRequest
import com.orderly.data.dto.OrderResponse
import com.orderly.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _createOrderResult = MutableLiveData<Result<String>>()
    val createOrderResult: LiveData<Result<String>> = _createOrderResult

    private val _userOrders = MutableLiveData<Result<List<OrderResponse>>>()
    val userOrders: LiveData<Result<List<OrderResponse>>> = _userOrders

    fun createOrder(createOrderRequest: CreateOrderRequest) {
        viewModelScope.launch {
            val result = orderRepository.createOrder(createOrderRequest)
            _createOrderResult.postValue(result)
        }
    }

    fun fetchUserOrders(username: String) {
        viewModelScope.launch {
            val result = orderRepository.getUserOrders(username)
            _userOrders.postValue(result)
        }
    }
}