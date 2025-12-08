package com.orderly.data.network

import com.orderly.data.dto.CreateOrderRequest
import com.orderly.data.dto.CreateUserRequest
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.dto.NewUserDTO
import com.orderly.data.dto.OrderResponse
import com.orderly.data.dto.ProductResponse
import com.orderly.data.dto.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    // Authentication
    @POST("api/v1/auth/register")
    suspend fun register(@Body newUser: NewUserDTO): Response<LoginResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body loginDto: LoginDTO): Response<LoginResponse>

    @GET("api/v1/auth/refresh")
    suspend fun refreshToken(): Response<LoginResponse>

    // Products
    @GET("api/v1/products")
    suspend fun getAllProducts(): Response<List<ProductResponse>>

    @GET("api/v1/products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<ProductResponse>

    // Orders
    @POST("api/v1/orders")
    suspend fun createOrder(@Body createOrderRequest: CreateOrderRequest): Response<OrderResponse>

    @GET("api/v1/orders/user/{username}")
    suspend fun getUserOrders(@Path("username") username: String): Response<List<OrderResponse>>

    @GET("api/v1/orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: Int): Response<OrderResponse>

    @GET("api/v1/orders")
    suspend fun getAllOrders(): Response<List<OrderResponse>>

    // Admin User Management
    @GET("api/v1/users")
    suspend fun getAllUsers(): Response<List<UserDTO>>

    @POST("api/v1/users/create")
    suspend fun createUser(@Body createUserRequest: CreateUserRequest): Response<UserDTO>

    @PUT("api/v1/users/{id}")
    suspend fun updateUser(@Path("id") userId: Int, @Body userDTO: UserDTO): Response<UserDTO>

    @DELETE("api/v1/users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>

    // Get current user info
    @GET("api/v1/user/me")
    suspend fun getCurrentUser(): Response<UserDTO>
}