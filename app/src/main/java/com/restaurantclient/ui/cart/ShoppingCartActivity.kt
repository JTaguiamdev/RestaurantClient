package com.restaurantclient.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.restaurantclient.R
import com.restaurantclient.data.CartManager
import com.restaurantclient.databinding.ActivityShoppingCartBinding
import com.restaurantclient.ui.checkout.CheckoutActivity
import com.restaurantclient.ui.common.setupGlassEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingCartBinding
    private lateinit var cartAdapter: CartAdapter
    
    @Inject
    lateinit var cartManager: CartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGlassUI()
        setupRecyclerView()
        setupClickListeners()
        observeCartItems()
    }

    private fun setupToolbar() {
        binding.root.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).apply {
            title = "My Cart"
            navigationIcon = getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
            setNavigationOnClickListener {
                finish()
            }
        }
    }
    
    private fun setupGlassUI() {
        // Setup glass effect for floating cart summary
        binding.cartSummaryBlur?.let { blurView ->
            val whiteOverlay = androidx.core.content.ContextCompat.getColor(this, R.color.white_glass_overlay)
            blurView.setOverlayColor(whiteOverlay)
            blurView.setupGlassEffect(20f)
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { productId, quantity ->
                cartManager.updateQuantity(productId, quantity)
            },
            onRemoveItem = { productId ->
                cartManager.removeFromCart(productId)
            }
        )
        binding.cartRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun setupClickListeners() {
        binding.checkoutButton.setOnClickListener {
            if (cartManager.totalItems > 0) {
                startActivity(Intent(this, CheckoutActivity::class.java))
            }
        }
    }

    private fun observeCartItems() {
        lifecycleScope.launch {
            cartManager.cartItems.collectLatest { items ->
                cartAdapter.submitList(items)
                updateUI(items.isEmpty())
                updateTotal()
            }
        }
    }

    private fun updateUI(isEmpty: Boolean) {
        if (isEmpty) {
            binding.cartRecyclerView.visibility = View.GONE
            binding.checkoutButton.isEnabled = false
            binding.checkoutButton.text = "Cart is Empty"
            // You could add an empty state view here
        } else {
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.checkoutButton.isEnabled = true
            binding.checkoutButton.text = "Proceed to Checkout (${cartManager.totalItems} items)"
        }
    }

    private fun updateTotal() {
        binding.totalPrice.text = "$${String.format("%.2f", cartManager.totalAmount)}"
    }
}

