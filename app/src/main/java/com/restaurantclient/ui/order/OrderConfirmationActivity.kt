package com.restaurantclient.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.restaurantclient.databinding.ActivityOrderConfirmationBinding
import com.restaurantclient.ui.product.ProductListActivity

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        val orderId = intent.getIntExtra(EXTRA_ORDER_ID, -1)
        val orderTotal = intent.getStringExtra(EXTRA_ORDER_TOTAL) ?: "$0.00"

        setupUI(orderId, orderTotal)
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customerToolbar.toolbar)
        supportActionBar?.apply {
            title = "Confirmation"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.customerToolbar.toolbar.setNavigationOnClickListener {
            navigateToHome()
        }
    }

    private fun setupUI(orderId: Int, orderTotal: String) {
        if (orderId > 0) {
            binding.orderIdText.text = "Order #$orderId"
        } else {
            binding.orderIdText.text = "Order Placed Successfully"
        }
        binding.orderTotalText.text = orderTotal
    }

    private fun setupClickListeners() {
        binding.viewOrdersButton.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
            finish()
        }

        binding.continueshoppingButton.setOnClickListener {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, ProductListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_ORDER_ID = "extra_order_id"
        const val EXTRA_ORDER_TOTAL = "extra_order_total"
    }
}
