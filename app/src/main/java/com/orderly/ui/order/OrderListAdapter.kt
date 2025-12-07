package com.orderly.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orderly.data.dto.OrderResponse
import com.orderly.databinding.ItemOrderBinding

class OrderListAdapter(private val onClick: (OrderResponse) -> Unit) :
    ListAdapter<OrderResponse, OrderListAdapter.OrderViewHolder>(OrderDiffCallback) {

    class OrderViewHolder(private val binding: ItemOrderBinding, val onClick: (OrderResponse) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentOrder: OrderResponse? = null

        init {
            itemView.setOnClickListener {
                currentOrder?.let {
                    onClick(it)
                }
            }
        }

        fun bind(order: OrderResponse) {
            currentOrder = order
            binding.orderId.text = "Order #${order.orderId}"
            binding.orderStatus.text = order.status
            binding.orderDate.text = order.createdAt.substringBefore("T")
            binding.orderItemCount.text = "${order.items.size} items"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }
}

object OrderDiffCallback : DiffUtil.ItemCallback<OrderResponse>() {
    override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
        return oldItem == newItem
    }
}