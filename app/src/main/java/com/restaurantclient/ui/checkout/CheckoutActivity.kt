package com.restaurantclient.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.restaurantclient.R
import com.restaurantclient.data.CartManager
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.CreateOrderRequest
import com.restaurantclient.databinding.ActivityCheckoutBinding
import com.restaurantclient.ui.cart.CheckoutCartAdapter
import com.restaurantclient.ui.common.setupGlassEffect
import com.restaurantclient.ui.order.MyOrdersActivity
import com.restaurantclient.ui.order.OrderConfirmationActivity
import com.restaurantclient.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var cartAdapter: CheckoutCartAdapter
    private var isOrderBeingPlaced = false
    
    @Inject
    lateinit var cartManager: CartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGlassUI()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        observeCartItems()
    }

    private fun setupToolbar() {
        binding.root.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).apply {
            title = "Checkout"
            navigationIcon = getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
            setNavigationOnClickListener {
                finish()
            }
        }
    }
    
    private fun setupGlassUI() {
        // Setup glass effect for checkout summary
        binding.checkoutSummaryBlur?.let { blurView ->
            val whiteOverlay = androidx.core.content.ContextCompat.getColor(this, R.color.white_glass_overlay)
            blurView.setOverlayColor(whiteOverlay)
            blurView.setupGlassEffect(20f)
        }
        
        // Setup glass effect for total card
        binding.checkoutTotalBlur?.let { blurView ->
            val whiteOverlay = androidx.core.content.ContextCompat.getColor(this, R.color.white_glass_overlay)
            blurView.setOverlayColor(whiteOverlay)
            blurView.setupGlassEffect(20f)
        }
    }

    private fun setupRecyclerView() {
        // Read-only cart adapter for checkout
        cartAdapter = CheckoutCartAdapter()
        binding.orderSummaryRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.orderSummaryRecyclerView.adapter = cartAdapter
    }

    private fun setupClickListeners() {
        binding.placeOrderButton.setOnClickListener {
            if (isOrderBeingPlaced) {
                return@setOnClickListener
            }
            
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
            isOrderBeingPlaced = false
            binding.placeOrderButton.isEnabled = true
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
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
        val total = cartManager.totalAmount
        binding.totalPrice.text = String.format("$%.2f", total)
    }

    private fun placeOrder() {
        isOrderBeingPlaced = true
        binding.placeOrderButton.isEnabled = false
        
        val orderRequest = CreateOrderRequest(
            products = cartManager.toOrderRequest()
        )
        
        orderViewModel.createOrder(orderRequest)
    }
}
