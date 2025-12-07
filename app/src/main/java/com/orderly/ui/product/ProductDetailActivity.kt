package com.orderly.ui.product

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.data.Result
import com.orderly.data.dto.CreateOrderRequest
import com.orderly.data.dto.OrderItemRequest
import com.orderly.databinding.ActivityProductDetailBinding
import com.orderly.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()

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
        productViewModel.fetchProductDetails(productId)

        binding.addToCartButton.setOnClickListener {
            // FIXME: Hardcoded userId and quantity
            val createOrderRequest = CreateOrderRequest(
                userId = 1,
                items = listOf(OrderItemRequest(productId = productId, quantity = 1))
            )
            orderViewModel.createOrder(createOrderRequest)
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

        orderViewModel.createOrderResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "Order created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to create order: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}