package com.restaurantclient.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.data.dto.ProductRequest
import com.restaurantclient.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<Result<List<ProductResponse>>>()
    val products: LiveData<Result<List<ProductResponse>>> = _products

    private val _selectedProduct = MutableLiveData<Result<ProductResponse>>()
    val selectedProduct: LiveData<Result<ProductResponse>> = _selectedProduct

    private val _productMutation = MutableLiveData<Result<Unit>>()
    val productMutation: LiveData<Result<Unit>> = _productMutation

    private val _mutationLoading = MutableLiveData<Boolean>()
    val mutationLoading: LiveData<Boolean> = _mutationLoading

    fun fetchProducts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val result = productRepository.getAllProducts(forceRefresh)
            _products.postValue(result)
        }
    }

    fun fetchProductDetails(productId: Int) {
        viewModelScope.launch {
            val result = productRepository.getProductById(productId)
            _selectedProduct.postValue(result)
        }
    }

    fun createProduct(name: String, description: String, price: String, imageUrl: String) {
        mutateProduct { productRepository.createProduct(ProductRequest(name, description, price, imageUrl)) }
    }

    fun updateProduct(productId: Int, name: String, description: String, price: String, imageUrl: String) {
        mutateProduct { productRepository.updateProduct(productId, ProductRequest(name, description, price, imageUrl)) }
    }

    fun deleteProduct(productId: Int) {
        mutateProduct { productRepository.deleteProduct(productId) }
    }

    suspend fun getProductsByCategory(categoryId: Int): Result<List<ProductResponse>> {
        return productRepository.getProductsByCategory(categoryId)
    }

    private fun mutateProduct(block: suspend () -> Result<*>) {
        viewModelScope.launch {
            _mutationLoading.value = true
            val result = try {
                block()
            } catch (e: Exception) {
                Result.Error(e)
            }

            when (result) {
                is Result.Success<*> -> {
                    _productMutation.value = Result.Success(Unit)
                    productRepository.clearCache() // Invalidate cache
                    fetchProducts(true) // Refresh products from API
                }
                is Result.Error -> _productMutation.value = Result.Error(result.exception)
            }

            _mutationLoading.value = false
        }
    }
}
