package com.orderly.data.repository

import android.util.Log
import com.orderly.data.Result
import com.orderly.data.dto.ProductResponse
import com.orderly.data.network.ApiService
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
}