package com.orderly.ui.order

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.data.Result
import com.orderly.databinding.ActivityMyOrdersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrdersBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var orderListAdapter: OrderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderListAdapter = OrderListAdapter { order ->
            Toast.makeText(this, "Clicked on Order #${order.orderId}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to OrderDetailActivity
        }
        binding.ordersRecyclerView.adapter = orderListAdapter

        setupObservers()
        // FIXME: Hardcoded username. This should be retrieved from a user session.
        orderViewModel.fetchUserOrders("customer")
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