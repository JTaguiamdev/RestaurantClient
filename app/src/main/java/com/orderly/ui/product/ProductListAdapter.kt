package com.orderly.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orderly.data.dto.ProductResponse
import com.orderly.databinding.ItemProductBinding

class ProductListAdapter(private val onClick: (ProductResponse) -> Unit) :
    ListAdapter<ProductResponse, ProductListAdapter.ProductViewHolder>(ProductDiffCallback) {

    class ProductViewHolder(private val binding: ItemProductBinding, val onClick: (ProductResponse) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentProduct: ProductResponse? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let {
                    onClick(it)
                }
            }
        }

        fun bind(product: ProductResponse) {
            currentProduct = product
            binding.productName.text = product.name
            binding.productDescription.text = product.description
            binding.productPrice.text = "$${product.price}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }
}

object ProductDiffCallback : DiffUtil.ItemCallback<ProductResponse>() {
    override fun areItemsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
        return oldItem.product_id == newItem.product_id
    }

    override fun areContentsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
        return oldItem == newItem
    }
}