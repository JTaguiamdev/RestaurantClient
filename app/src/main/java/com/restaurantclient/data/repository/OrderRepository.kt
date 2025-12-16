package com.restaurantclient.data.repository

import android.util.Log
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.CreateOrderRequest
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.dto.UpdateOrderRequest
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class OrderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun createOrder(createOrderRequest: CreateOrderRequest): Result<OrderResponse> {
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
            Log.d("OrderRepository", "Fetching orders for username: $username")
            val response = apiService.getUserOrders(username)
            Log.d("OrderRepository", "API Response code: ${response.code()}")
            if (response.isSuccessful) {
                val orders = response.body()!!
                Log.d("OrderRepository", "Successfully fetched ${orders.size} orders")
                Result.Success(orders)
            } else {
                val errorMsg = "Failed to get user orders: HTTP ${response.code()}"
                Log.e("OrderRepository", errorMsg)
                Result.Error(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception fetching user orders", e)
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

    suspend fun getAllOrders(): Result<List<OrderResponse>> {
        return try {
            val response = apiService.getAllOrders()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to fetch orders: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String): Result<OrderResponse> {
        return try {
            val response = apiService.updateOrder(orderId, UpdateOrderRequest(status))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Empty response body"))
                }
            } else {
                Result.Error(Exception("Failed to update order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
