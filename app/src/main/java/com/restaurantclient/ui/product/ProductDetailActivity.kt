package com.restaurantclient.ui.product

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.R
import com.restaurantclient.data.CartManager
import com.restaurantclient.data.Result
import com.restaurantclient.databinding.ActivityProductDetailBinding
import com.restaurantclient.databinding.DialogSuccessBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val productViewModel: ProductViewModel by viewModels()
    
    @Inject
    lateinit var cartManager: CartManager

    private var quantity: Int = 1
    private var spicyLevel: Int = 50

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

        setupSpicySlider()
        setupQuantityControls()
        setupObservers()
        setupClickListeners()
        productViewModel.fetchProductDetails(productId)
    }

    private fun setupSpicySlider() {
        binding.spicySlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                spicyLevel = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupQuantityControls() {
        binding.quantityText.text = quantity.toString()

        binding.decrementButton.setOnClickListener {
            if (quantity > MIN_QUANTITY) {
                quantity--
                binding.quantityText.text = quantity.toString()
            }
        }

        binding.incrementButton.setOnClickListener {
            if (quantity < MAX_QUANTITY) {
                quantity++
                binding.quantityText.text = quantity.toString()
            }
        }
    }

    private fun setupClickListeners() {
        binding.addToCartButton.setOnClickListener {
            // Add current product to cart
            productViewModel.selectedProduct.value?.let { result ->
                if (result is Result.Success) {
                    cartManager.addToCart(result.data, quantity)
                    showSuccessDialog()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val dialogBinding = DialogSuccessBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.goBackButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
        
        // Make dialog background transparent for rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupObservers() {
        productViewModel.selectedProduct.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val product = result.data
                    binding.productName.text = product.name
                    binding.productDescription.text = product.description
                    binding.productPrice.text = "$${product.price}"

                    // Generate random rating and time for demo
                    val rating = String.format("%.1f", Random.nextDouble(MIN_RATING, MAX_RATING))
                    val time = Random.nextInt(MIN_ESTIMATED_TIME, MAX_ESTIMATED_TIME)
                    binding.ratingText.text = rating
                    binding.timeText.text = getString(R.string.estimated_time, time)
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to fetch product details: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        private const val MIN_ESTIMATED_TIME = 15
        private const val MAX_ESTIMATED_TIME = 40
        private const val MIN_QUANTITY = 1
        private const val MAX_QUANTITY = 99
        private const val MIN_RATING = 3.5
        private const val MAX_RATING = 5.0
    }
}
    }
}
