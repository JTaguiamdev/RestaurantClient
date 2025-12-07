package com.orderly.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderly.data.Result
import com.orderly.data.dto.ProductResponse
import com.orderly.data.repository.ProductRepository
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

    fun fetchProducts() {
        viewModelScope.launch {
            val result = productRepository.getAllProducts()
            _products.postValue(result)
        }
    }

    fun fetchProductDetails(productId: Int) {
        viewModelScope.launch {
            val result = productRepository.getProductById(productId)
            _selectedProduct.postValue(result)
        }
    }
}