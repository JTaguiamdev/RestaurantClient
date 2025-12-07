package com.orderly.data.repository

import com.orderly.data.Result
import com.orderly.data.dto.CreateOrderRequest
import com.orderly.data.dto.OrderResponse
import com.orderly.data.network.ApiService
import javax.inject.Inject

class OrderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun createOrder(createOrderRequest: CreateOrderRequest): Result<String> {
        return try {
            val response = apiService.createOrder(createOrderRequest)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to create order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserOrders(username: String): Result<List<OrderResponse>> {
        return try {
            val response = apiService.getUserOrders(username)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get user orders: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getOrderById(orderId: Int): Result<OrderResponse> {
        return try {
            val response = apiService.getOrderById(orderId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get order by ID: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}