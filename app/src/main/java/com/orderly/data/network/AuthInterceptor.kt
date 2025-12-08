package com.orderly.data.network

import android.util.Log
import com.orderly.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        
        tokenManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("AuthInterceptor", "Added auth header to ${request.url}")
        } ?: run {
            Log.w("AuthInterceptor", "No token available for ${request.url}")
        }
        
        val response = chain.proceed(requestBuilder.build())
        Log.d("AuthInterceptor", "Response: ${response.code} for ${request.url}")
        
        // Handle 401 Unauthorized - token might be expired
        if (response.code == 401) {
            Log.w("AuthInterceptor", "Received 401 Unauthorized - token might be expired")
            // Clear invalid token
            tokenManager.deleteToken()
        }
        
        return response
    }
}