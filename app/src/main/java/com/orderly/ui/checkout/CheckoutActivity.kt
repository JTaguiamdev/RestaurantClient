package com.orderly.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.orderly.data.CartManager
import com.orderly.data.Result
import com.orderly.data.dto.CreateOrderRequest
import com.orderly.databinding.ActivityCheckoutBinding
import com.orderly.ui.cart.CheckoutCartAdapter
import com.orderly.ui.order.MyOrdersActivity
import com.orderly.ui.order.OrderConfirmationActivity
import com.orderly.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var cartAdapter: CheckoutCartAdapter
    
    @Inject
    lateinit var cartManager: CartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        observeCartItems()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Read-only cart adapter for checkout
        cartAdapter = CheckoutCartAdapter()
        binding.orderSummaryRecyclerView.adapter = cartAdapter
    }

    private fun setupClickListeners() {
        binding.placeOrderButton.setOnClickListener {
            if (cartManager.totalItems > 0) {
                placeOrder()
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupObservers() {
        orderViewModel.createOrderResult.observe(this) { result ->
            binding.placeOrderButton.isEnabled = true
            when (result) {
                is Result.Success -> {
                    val orderTotal = "$${String.format("%.2f", cartManager.totalAmount)}"
                    cartManager.clearCart() // Clear cart after successful order
                    
                    // Navigate to order confirmation
                    val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                        putExtra(OrderConfirmationActivity.EXTRA_ORDER_ID, result.data.order_id)
                        putExtra(OrderConfirmationActivity.EXTRA_ORDER_TOTAL, orderTotal)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to place order: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeCartItems() {
        lifecycleScope.launch {
            cartManager.cartItems.collectLatest { items ->
                cartAdapter.submitList(items)
                updateTotal()
                
                // If cart becomes empty while in checkout, go back
                if (items.isEmpty()) {
                    Toast.makeText(this@CheckoutActivity, "Cart is empty", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun updateTotal() {
        binding.totalPrice.text = "$${String.format("%.2f", cartManager.totalAmount)}"
    }

    private fun placeOrder() {
        binding.placeOrderButton.isEnabled = false
        
        val orderRequest = CreateOrderRequest(
            products = cartManager.toOrderRequest()
        )
        
        orderViewModel.createOrder(orderRequest)
    }
}