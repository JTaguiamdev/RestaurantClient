package com.orderly.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.orderly.MainActivity
import com.orderly.R
import com.orderly.data.CartManager
import com.orderly.data.Result
import com.orderly.databinding.ActivityProductListBinding
import com.orderly.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter
    
    @Inject
    lateinit var cartManager: CartManager
    
    private var cartBadgeMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        observeCartChanges()
        productViewModel.fetchProducts()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { product ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.product_id)
            }
            startActivity(intent)
        }
        binding.productsRecyclerView.adapter = productListAdapter
    }

    private fun setupClickListeners() {
        binding.myOrdersButton.setOnClickListener {
            startActivity(Intent(this, com.orderly.ui.order.MyOrdersActivity::class.java))
        }
    }

    private fun observeCartChanges() {
        lifecycleScope.launch {
            cartManager.cartItems.collectLatest { items ->
                updateCartBadge(cartManager.totalItems)
            }
        }
    }

    private fun setupObservers() {
        productViewModel.products.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    productListAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to fetch products: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.customer_main_menu, menu)
        cartBadgeMenuItem = menu.findItem(R.id.action_cart)
        updateCartBadge(cartManager.totalItems)
        return true
    }
    
    private fun updateCartBadge(itemCount: Int) {
        cartBadgeMenuItem?.let { menuItem ->
            if (itemCount > 0) {
                menuItem.title = "Cart ($itemCount)"
            } else {
                menuItem.title = "Cart"
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(this, com.orderly.ui.cart.ShoppingCartActivity::class.java))
                true
            }
            R.id.action_my_orders -> {
                startActivity(Intent(this, com.orderly.ui.order.MyOrdersActivity::class.java))
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, com.orderly.ui.user.UserProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        authViewModel.logout()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}