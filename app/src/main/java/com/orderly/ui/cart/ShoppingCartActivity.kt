package com.orderly.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.orderly.data.CartManager
import com.orderly.databinding.ActivityShoppingCartBinding
import com.orderly.ui.checkout.CheckoutActivity
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
        setupRecyclerView()
        setupClickListeners()
        observeCartItems()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
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
