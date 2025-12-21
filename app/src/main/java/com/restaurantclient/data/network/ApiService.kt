package com.restaurantclient.data.network

import com.restaurantclient.data.dto.AddPermissionRequest
import com.restaurantclient.data.dto.AddRolesRequest
import com.restaurantclient.data.dto.AssignMultipleRolesRequest
import com.restaurantclient.data.dto.AssignRoleRequest
import com.restaurantclient.data.dto.CategoryDTO
import com.restaurantclient.data.dto.CategoryProductRequest
import com.restaurantclient.data.dto.CategoryRequest
import com.restaurantclient.data.dto.CreateOrderRequest
import com.restaurantclient.data.dto.CreateUserRequest
import com.restaurantclient.data.dto.DashboardSummaryDTO
import com.restaurantclient.data.dto.LoginDTO
import com.restaurantclient.data.dto.LoginResponse
import com.restaurantclient.data.dto.NewUserDTO
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.dto.PermissionRequest
import com.restaurantclient.data.dto.ProductRequest
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.data.dto.RoleDetailsDTO
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.RoleRequest
import com.restaurantclient.data.dto.UpdateOrderRequest
import com.restaurantclient.data.dto.UpdateUserRolesRequest
import com.restaurantclient.data.dto.UpdateUserRequest // Added missing import
import com.restaurantclient.data.dto.UserDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Authentication
    @POST("api/v1/auth/register")
    suspend fun register(@Body newUser: NewUserDTO): Response<LoginResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body loginDto: LoginDTO): Response<LoginResponse>

    @GET("api/v1/auth/refresh")
    suspend fun refreshToken(): Response<LoginResponse>

    // Products
    @POST("api/v1/products")
    suspend fun createProduct(@Body productRequest: ProductRequest): Response<ProductResponse>

    @GET("api/v1/products")
    suspend fun getAllProducts(): Response<List<ProductResponse>>

    @GET("api/v1/products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<ProductResponse>

    @PUT("api/v1/products/{id}")
    suspend fun updateProduct(@Path("id") productId: Int, @Body productRequest: ProductRequest): Response<ProductResponse>

    @DELETE("api/v1/products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Int): Response<Unit>

    // Orders
    @POST("api/v1/orders")
    suspend fun createOrder(@Body createOrderRequest: CreateOrderRequest): Response<ResponseBody>

    @GET("api/v1/orders/user/{username}")
    suspend fun getUserOrders(@Path("username") username: String): Response<List<OrderResponse>>

    @GET("api/v1/orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: Int): Response<OrderResponse>

    @GET("api/v1/orders")
    suspend fun getAllOrders(): Response<List<OrderResponse>>

    @POST("api/v1/orders/{id}")
    suspend fun updateOrder(@Path("id") orderId: Int, @Body updateOrderRequest: UpdateOrderRequest): Response<ResponseBody>

    // Admin User Management
    @GET("api/v1/users")
    suspend fun getAllUsers(): Response<List<UserDTO>>

    @GET("api/v1/users/{id}")
    suspend fun getUserById(@Path("id") userId: Int): Response<UserDTO>

    @GET("api/v1/users/search")
    suspend fun searchUserByName(@Query("username") username: String): Response<List<UserDTO>>

    @POST("api/v1/users/create")
    suspend fun createUser(@Body createUserRequest: CreateUserRequest): Response<okhttp3.ResponseBody>

    @POST("api/v1/users/{id}")
    suspend fun updateUser(@Path("id") userId: Int, @Body userDTO: UserDTO): Response<UserDTO>

    @PUT("api/v1/users/{id}/profile")
    suspend fun updateUserProfile(@Path("id") userId: Int, @Body updateUserRequest: UpdateUserRequest): Response<Unit>

    @DELETE("api/v1/users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>

    // Get current user info
    @GET("api/v1/user/me")
    suspend fun getCurrentUser(): Response<UserDTO>

    // Roles
    @POST("api/v1/roles/create")
    suspend fun createRole(@Body roleRequest: RoleRequest): Response<RoleDTO>

    @GET("api/v1/roles")
    suspend fun getAllRoles(): Response<List<RoleDTO>>
    
    @GET("api/v1/roles")
    suspend fun getAllRolesWithDetails(): Response<List<RoleDetailsDTO>>

    @POST("api/v1/roles/update/{id}")
    suspend fun updateRole(@Path("id") roleId: Int, @Body roleRequest: RoleRequest): Response<RoleDTO>

    @DELETE("api/v1/roles/{id}")
    suspend fun deleteRole(@Path("id") roleId: Int): Response<Unit>

    @POST("api/v1/roles/{id}/set_permission")
    suspend fun setPermission(@Path("id") roleId: Int, @Body permissionRequest: PermissionRequest): Response<Unit>

    @PATCH("api/v1/roles/{id}/delete_permission")
    suspend fun removePermission(@Path("id") roleId: Int): Response<Unit>

    @POST("api/v1/roles/assign")
    suspend fun assignRole(@Body assignRoleRequest: AssignRoleRequest): Response<Unit>
    
    @POST("api/v1/roles/add_permission")
    suspend fun addPermission(@Body addPermissionRequest: AddPermissionRequest): Response<Unit>
    
    @POST("api/v1/roles/assign/multiple")
    suspend fun assignMultipleRoles(@Body assignMultipleRolesRequest: AssignMultipleRolesRequest): Response<Unit>
    
    @PUT("api/v1/users/{id}/roles")
    suspend fun updateUserRoles(@Path("id") userId: Int, @Body updateUserRolesRequest: UpdateUserRolesRequest): Response<UserDTO>
    
    @POST("api/v1/users/{username}/roles/add")
    suspend fun addRolesToUser(@Path("username") username: String, @Body addRolesRequest: AddRolesRequest): Response<UserDTO>
    
    @DELETE("api/v1/users/{username}/roles/{roleName}")
    suspend fun removeRoleFromUser(@Path("username") username: String, @Path("roleName") roleName: String): Response<UserDTO>
    
    @GET("api/v1/users/{username}/roles")
    suspend fun getUserRoles(@Path("username") username: String): Response<List<RoleDetailsDTO>>

    // Categories
    @GET("api/v1/categories")
    suspend fun getAllCategories(): Response<List<CategoryDTO>>

    @POST("api/v1/categories")
    suspend fun createCategory(@Body categoryRequest: CategoryRequest): Response<CategoryDTO>

    @PUT("api/v1/categories/{id}")
    suspend fun updateCategory(@Path("id") categoryId: Int, @Body categoryRequest: CategoryRequest): Response<CategoryDTO>

    @DELETE("api/v1/categories/{id}")
    suspend fun deleteCategory(@Path("id") categoryId: Int): Response<Unit>

    @POST("api/v1/categories/product")
    suspend fun addProductToCategory(@Body request: CategoryProductRequest): Response<Unit>

    @POST("api/v1/categories/product/remove")
    suspend fun removeProductFromCategory(@Body request: CategoryProductRequest): Response<Unit>

    @GET("api/v1/categories/{id}/products")
    suspend fun getProductsByCategory(@Path("id") categoryId: Int): Response<List<ProductResponse>>
    
    @GET("api/v1/categories/{category_name}/products")
    suspend fun getProductsByCategoryName(@Path("category_name") categoryName: String): Response<List<ProductResponse>>

    // Additional Order Endpoints
    @GET("api/v1/orders/role/{role_name}")
    suspend fun getOrdersByRole(@Path("role_name") roleName: String): Response<List<OrderResponse>>

    // Health Check
    @GET("api")
    suspend fun healthCheck(): Response<String>
    
    // Dashboard Summary
    @GET("api/v1/dashboard/summary")
    suspend fun getDashboardSummary(): Response<DashboardSummaryDTO>
}
