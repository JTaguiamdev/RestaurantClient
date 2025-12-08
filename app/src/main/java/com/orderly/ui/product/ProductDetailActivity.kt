package com.orderly.ui.product

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.data.CartManager
import com.orderly.data.Result
import com.orderly.databinding.ActivityProductDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val productViewModel: ProductViewModel by viewModels()
    
    @Inject
    lateinit var cartManager: CartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupObservers()
        setupClickListeners()
        productViewModel.fetchProductDetails(productId)
    }

    private fun setupClickListeners() {
        binding.addToCartButton.setOnClickListener {
            // Add current product to cart instead of creating order directly
            productViewModel.selectedProduct.value?.let { result ->
                if (result is Result.Success) {
                    cartManager.addToCart(result.data, 1)
                    Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupObservers() {
        productViewModel.selectedProduct.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val product = result.data
                    binding.productName.text = product.name
                    binding.productDescription.text = product.description
                    binding.productPrice.text = "$${product.price}"
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to fetch product details: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}