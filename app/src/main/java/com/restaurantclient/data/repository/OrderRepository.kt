package com.restaurantclient.data.repository

import android.util.Log
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.CreateOrderRequest
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.dto.UpdateOrderRequest
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class OrderRepository @Inject constructor(private val apiService: ApiService) {

    private var cachedAllOrders: List<OrderResponse>? = null
    private val cachedUserOrders: MutableMap<String, List<OrderResponse>> = mutableMapOf()

    fun clearCache() {
        cachedAllOrders = null
        cachedUserOrders.clear()
        Log.d("OrderRepository", "Order caches cleared.")
    }

    suspend fun createOrder(createOrderRequest: CreateOrderRequest): Result<OrderResponse> {
        return try {
            val response = apiService.createOrder(createOrderRequest)
            if (response.isSuccessful) {
                clearCache() // Invalidate cache after creating new order
                // Server returns plain text "Orders created", create dummy response
                val dummyResponse = OrderResponse(
                    order_id = 0,
                    user_id = 0,
                    product_id = 0,
                    quantity = 0,
                    total_amount = "0",
                    status = "pending",
                    created_at = null,
                    updated_at = null
                )
                Result.Success(dummyResponse)
            } else {
                Result.Error(Exception("Failed to create order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserOrders(username: String, forceRefresh: Boolean = false): Result<List<OrderResponse>> {
        if (cachedUserOrders.containsKey(username) && !forceRefresh) {
            Log.d("OrderRepository", "Returning cached orders for user: $username")
            return Result.Success(cachedUserOrders[username]!!)
        }

        return try {
            Log.d("OrderRepository", "Fetching orders for username: $username (forceRefresh: $forceRefresh)")
            val response = apiService.getUserOrders(username)
            Log.d("OrderRepository", "API Response code: ${response.code()}")
            if (response.isSuccessful) {
                val newOrders = response.body()!!
                if (newOrders != cachedUserOrders[username]) {
                    cachedUserOrders[username] = newOrders
                    Log.d("OrderRepository", "Successfully fetched and updated ${newOrders.size} orders for user $username in cache")
                } else {
                    Log.d("OrderRepository", "Successfully fetched orders for user $username, but data is same as cache. Not updating cache.")
                }
                Result.Success(cachedUserOrders[username]!!)
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

    suspend fun getAllOrders(forceRefresh: Boolean = false): Result<List<OrderResponse>> {
        if (cachedAllOrders != null && !forceRefresh) {
            Log.d("OrderRepository", "Returning cached all orders.")
            return Result.Success(cachedAllOrders!!)
        }

        return try {
            Log.d("OrderRepository", "Fetching all orders (forceRefresh: $forceRefresh)")
            val response = apiService.getAllOrders()
            if (response.isSuccessful) {
                val newOrders = response.body() ?: emptyList()
                if (newOrders != cachedAllOrders) {
                    cachedAllOrders = newOrders
                    Log.d("OrderRepository", "Successfully fetched and updated ${newOrders.size} all orders in cache")
                } else {
                    Log.d("OrderRepository", "Successfully fetched all orders, but data is same as cache. Not updating cache.")
                }
                Result.Success(cachedAllOrders!!)
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
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getOrdersByRole(roleName: String): Result<List<OrderResponse>> {
        return try {
            val response = apiService.getOrdersByRole(roleName)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to get orders by role: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
