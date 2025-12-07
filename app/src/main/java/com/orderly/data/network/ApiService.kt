package com.orderly.data.network

import com.orderly.data.dto.CreateOrderRequest
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.dto.NewUserDTO
import com.orderly.data.dto.OrderResponse
import com.orderly.data.dto.ProductResponse
import com.orderly.data.dto.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // User Authentication & Profile
    @POST("api/auth/register")
    suspend fun register(@Body newUser: NewUserDTO): Response<LoginResponse>

    @POST("api/auth/login")
    suspend fun login(@Body loginDto: LoginDTO): Response<LoginResponse>

    @GET("api/users/{userId}") // For fetching customer's own profile
    suspend fun getUserById(@Path("userId") userId: Int): Response<UserDTO>

    // Product Routes (Customer view)
    @GET("api/products")
    suspend fun getAllProducts(): Response<List<ProductResponse>>

    @GET("api/products/{productId}")
    suspend fun getProductById(@Path("productId") productId: Int): Response<ProductResponse>

    // Order Routes (Customer view)
    @POST("api/orders")
    suspend fun createOrder(@Body createOrderRequest: CreateOrderRequest): Response<String> // Returns "Orders created"

    @GET("api/orders/user/{username}") // To fetch orders for the logged-in user
    suspend fun getUserOrders(@Path("username") username: String): Response<List<OrderResponse>>

    @GET("api/orders/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): Response<OrderResponse>
}