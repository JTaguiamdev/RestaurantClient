package com.restaurantclient.data.repository

import android.util.Log
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductRequest
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class ProductRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllProducts(): Result<List<ProductResponse>> {
        return try {
            Log.d("ProductRepository", "Fetching all products")
            val response = apiService.getAllProducts()
            Log.d("ProductRepository", "API Response code: ${response.code()}")
            if (response.isSuccessful) {
                val products = response.body()!!
                Log.d("ProductRepository", "Successfully fetched ${products.size} products")
                Result.Success(products)
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
                Result.Success(response.body()!!)
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
                Result.Success(response.body()!!)
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
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Empty response body"))
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
}

