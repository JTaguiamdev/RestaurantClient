package com.restaurantclient.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.CategoryDTO
import com.restaurantclient.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryDTO>>(emptyList())
    val categories: StateFlow<List<CategoryDTO>> = _categories

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            when (val result = categoryRepository.getAllCategories()) {
                is Result.Success -> {
                    _categories.value = result.data
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
            }
            
            _loading.value = false
        }
    }
}
