package com.restaurantclient.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.databinding.ItemIntroProductBinding

class IntroProductAdapter(
    private val products: List<IntroProduct>
) : RecyclerView.Adapter<IntroProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(
        private val binding: ItemIntroProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: IntroProduct) {
            binding.productImage.setImageResource(product.productImageUri)
            binding.productNameOverlay.text = product.name
            binding.productPrice.text = String.format("$%.2f", product.price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemIntroProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}
