package com.restaurantclient.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.R
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.databinding.ItemProductBinding
import kotlin.random.Random

class ProductListAdapter(
    private val onClick: (ProductResponse) -> Unit,
    private val onAdminAction: ((View, ProductResponse) -> Unit)? = null,
    private val isAdminMode: Boolean = false
) : ListAdapter<ProductResponse, ProductListAdapter.ProductViewHolder>(ProductDiffCallback) {

    private val favoriteStates = mutableMapOf<String, Boolean>()

    companion object {
        private const val MIN_RATING = 3.5
        private const val MAX_RATING = 5.0
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onClick: (ProductResponse) -> Unit,
        private val onAdminAction: ((View, ProductResponse) -> Unit)?,
        private val isAdminMode: Boolean,
        private val favoriteStates: MutableMap<String, Boolean>
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentProduct: ProductResponse? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let { onClick(it) }
            }
        }

        fun bind(product: ProductResponse) {
            currentProduct = product
            binding.productName.text = product.name
            binding.productDescription.text = product.description
            binding.productPrice.text = "$${product.price}"

            // Show/hide elements based on mode
            binding.adminBadgeChip.isVisible = isAdminMode
            binding.adminManageButton.isVisible = isAdminMode
            binding.favoriteIcon.isVisible = !isAdminMode
            binding.ratingSection.isVisible = !isAdminMode

            // Generate random rating for demo
            if (!isAdminMode) {
                val rating = String.format("%.1f", Random.nextDouble(MIN_RATING, MAX_RATING))
                binding.ratingText.text = rating

                // Handle favorite state
                val isFavorite = favoriteStates[product.product_id] ?: false
                updateFavoriteIcon(isFavorite)

                binding.favoriteIcon.setOnClickListener {
                    val newState = !(favoriteStates[product.product_id] ?: false)
                    favoriteStates[product.product_id] = newState
                    updateFavoriteIcon(newState)
                }
            }

            if (isAdminMode) {
                binding.adminManageButton.setOnClickListener { view ->
                    currentProduct?.let { onAdminAction?.invoke(view, it) }
                }
            } else {
                binding.adminManageButton.setOnClickListener(null)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                binding.favoriteIcon.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.favoriteIcon.setImageResource(R.drawable.ic_favorite)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onClick, onAdminAction, isAdminMode, favoriteStates)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
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
