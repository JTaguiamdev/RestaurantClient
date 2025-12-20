package com.restaurantclient.data.repository

import android.util.Log
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductRequest
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class ProductRepository @Inject constructor(private val apiService: ApiService) {

    private var cachedProducts: List<ProductResponse>? = null

    fun clearCache() {
        cachedProducts = null
    }

    suspend fun getAllProducts(forceRefresh: Boolean = false): Result<List<ProductResponse>> {
        if (cachedProducts != null && !forceRefresh) {
            return Result.Success(cachedProducts!!)
        }

        return try {
            Log.d("ProductRepository", "Fetching all products")
            val response = apiService.getAllProducts()
            Log.d("ProductRepository", "API Response code: ${response.code()}")
            if (response.isSuccessful) {
                val newProducts = response.body()
                if (newProducts != null) {
                    if (newProducts != cachedProducts) { // Simple equality check for list
                        cachedProducts = newProducts
                        Log.d("ProductRepository", "Successfully fetched and updated ${newProducts.size} products in cache")
                    } else {
                        Log.d("ProductRepository", "Successfully fetched products, but data is same as cache. Not updating cache.")
                    }
                    Result.Success(cachedProducts!!)
                } else {
                    val errorMsg = "Failed to fetch products: Response body is null"
                    Log.e("ProductRepository", errorMsg)
                    Result.Error(Exception(errorMsg))
                }
            } else {
                val errorMsg = "Failed to fetch products: HTTP ${response.code()}"
                Log.e("ProductRepository", errorMsg)
                Result.Error(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception fetching products", e)
            Result.Error(e)
        }
    }

    suspend fun getProductById(productId: Int): Result<ProductResponse> {
        return try {
            val response = apiService.getProductById(productId)
            if (response.isSuccessful) {
                val product = response.body()
                if (product != null) {
                    Result.Success(product)
                } else {
                    Result.Error(Exception("Failed to fetch product by ID: Response body is null"))
                }
            } else {
                Result.Error(Exception("Failed to fetch product by ID: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun createProduct(request: ProductRequest): Result<ProductResponse> {
        return try {
            val response = apiService.createProduct(request)
            if (response.isSuccessful) {
                val product = response.body()
                if (product != null) {
                    Result.Success(product)
                } else {
                    Result.Error(Exception("Failed to create product: Response body is null"))
                }
            } else {
                Result.Error(Exception("Failed to create product: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateProduct(productId: Int, request: ProductRequest): Result<ProductResponse> {
        return try {
            val response = apiService.updateProduct(productId, request)
            if (response.isSuccessful) {
                val product = response.body()
                if (product != null) {
                    Result.Success(product)
                } else {
                    Result.Error(Exception("Failed to update product: Response body is null"))
                }
            } else {
                Result.Error(Exception("Failed to update product: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteProduct(productId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteProduct(productId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to delete product: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getProductsByCategory(categoryId: Int): Result<List<ProductResponse>> {
        return try {
            Log.d("ProductRepository", "Fetching products for category: $categoryId")
            val response = apiService.getProductsByCategory(categoryId)
            Log.d("ProductRepository", "API Response code: ${response.code()}")
            if (response.isSuccessful) {
                val products = response.body()
                if (products != null) {
                    Log.d("ProductRepository", "Successfully fetched ${products.size} products for category $categoryId")
                    Result.Success(products)
                } else {
                    val errorMsg = "Failed to fetch products by category: Response body is null"
                    Log.e("ProductRepository", errorMsg)
                    Result.Error(Exception(errorMsg))
                }
            } else {
                val errorMsg = "Failed to fetch products by category: HTTP ${response.code()}"
                Log.e("ProductRepository", errorMsg)
                Result.Error(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception fetching products by category", e)
            Result.Error(e)
        }
    }
}

