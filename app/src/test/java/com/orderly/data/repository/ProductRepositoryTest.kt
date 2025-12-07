package com.orderly.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.orderly.data.Result
import com.orderly.data.dto.ProductResponse
import com.orderly.data.network.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class ProductRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var productRepository: ProductRepository
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        productRepository = ProductRepository(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllProducts success returns success result`() = runTest {
        // Given
        val products = listOf(ProductResponse(1, "p1", "d1", 1.0, "", ""))
        val mockResponse = MockResponse()
            .setBody(Gson().toJson(products))
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = productRepository.getAllProducts()

        // Then
        assert(result is Result.Success)
        assert((result as Result.Success).data.size == 1)
    }

    @Test
    fun `getAllProducts failure returns error result`() = runTest {
        // Given
        val mockResponse = MockResponse().setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = productRepository.getAllProducts()

        // Then
        assert(result is Result.Error)
    }
}