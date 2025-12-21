package com.restaurantclient.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.restaurantclient.R
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.databinding.ActivityOrderDetailBinding
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val order = intent.getSerializableExtra(EXTRA_ORDER, OrderResponse::class.java)
        if (order == null) {
            Toast.makeText(this, R.string.order_detail_error_missing, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        renderOrder(order)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customerToolbar.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.order_detail_title)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.customerToolbar.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun renderOrder(order: OrderResponse) {
        binding.orderNumberValue.text = "#${order.order_id}"
        binding.orderStatusChip.text = order.status ?: getString(R.string.order_detail_unknown_value)
        binding.orderQuantityValue.text = order.quantity.toString()
        binding.orderTotalValue.text = formatCurrency(order.total_amount)
        binding.orderPlacedValue.text = formatTimestamp(order.created_at)
        binding.orderUpdatedValue.text = formatTimestamp(order.updated_at)
    }

    private fun formatCurrency(value: String?): String {
        if (value.isNullOrBlank()) {
            return getString(R.string.order_detail_unknown_value)
        }
        return runCatching {
            val amount = BigDecimal(value)
            NumberFormat.getCurrencyInstance().format(amount)
        }.getOrElse {
            "\$${value}"
        }
    }

    private fun formatTimestamp(value: String?): String {
        if (value.isNullOrBlank()) {
            return getString(R.string.order_detail_unknown_value)
        }
        return runCatching {
            val date = OffsetDateTime.parse(value)
            date.format(timestampFormatter())
        }.getOrElse {
            value.replace("T", " ")
                .replace("Z", "")
                .substringBefore("+", value)
                .trim()
        }
    }

    private fun timestampFormatter(): DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a", Locale.getDefault())

    companion object {
        private const val EXTRA_ORDER = "extra_order"

        fun createIntent(context: Context, order: OrderResponse): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(EXTRA_ORDER, order)
            }
        }
    }
}
