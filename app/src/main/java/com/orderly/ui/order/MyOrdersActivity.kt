package com.orderly.ui.order

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.data.Result
import com.orderly.data.TokenManager
import com.orderly.databinding.ActivityMyOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrdersBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var orderListAdapter: OrderListAdapter
    
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderListAdapter = OrderListAdapter { order ->
            Toast.makeText(this, "Clicked on Order #${order.order_id}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to OrderDetailActivity
        }
        binding.ordersRecyclerView.adapter = orderListAdapter

        setupObservers()
        
        // Get username from token manager
        val username = tokenManager.getUsername()
        Log.d("MyOrdersActivity", "Current username: $username")
        
        if (username != null) {
            Log.d("MyOrdersActivity", "Fetching orders for username: $username")
            orderViewModel.fetchUserOrders(username)
        } else {
            Log.e("MyOrdersActivity", "No username found, user not logged in properly")
            Log.d("MyOrdersActivity", "Redirecting to login...")
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            
            // Redirect to MainActivity which will handle login
            val intent = android.content.Intent(this, com.orderly.MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupObservers() {
        orderViewModel.userOrders.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    orderListAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to fetch orders: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}