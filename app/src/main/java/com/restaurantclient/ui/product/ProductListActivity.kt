package com.restaurantclient.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.ColorUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.eightbitlab.com.blurview.BlurView
import com.eightbitlab.com.blurview.RenderScriptBlur
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.CartManager
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.databinding.ActivityProductListBinding
import com.restaurantclient.databinding.DialogAdminProductBinding
import com.restaurantclient.ui.admin.AdminDashboardActivity
import com.restaurantclient.ui.admin.OrderManagementActivity
import com.restaurantclient.ui.admin.UserManagementActivity
import com.restaurantclient.ui.auth.AuthViewModel
import com.restaurantclient.ui.cart.ShoppingCartActivity
import com.restaurantclient.ui.user.UserProfileActivity
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
    private var isAdminUser: Boolean = false
    private var isFetchLoading: Boolean = false
    private var isMutationLoading: Boolean = false
    private var allProducts: List<ProductResponse> = emptyList()
    private var selectedCategory: String = "All"

    companion object {
        private const val BLUR_RADIUS = 20f
        private const val BLUR_OVERLAY_ALPHA = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.loadStoredUserInfo()
        isAdminUser = authViewModel.isAdmin()

        setupModernUi()
        setupAdminUi()
        setupRecyclerView()
        setupObservers()
        observeCartChanges()
        refreshProducts()
    }
    
    private fun setupModernUi() {
        // Setup search functionality
        binding.searchInput.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                filterProducts(query)
            }
            true
        }

        // Setup category chips
        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                selectedCategory = chip?.text.toString()
                filterByCategory(selectedCategory)
            }
        }

        // Setup filter button
        binding.filterButton.setOnClickListener {
            Toast.makeText(this, "Filter clicked", Toast.LENGTH_SHORT).show()
        }

        // Setup profile image click
        binding.profileImage.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        // Setup bottom navigation
        binding.navHome.setOnClickListener {
            // Already on home
        }

        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, ShoppingCartActivity::class.java))
        }

        binding.navOrders.setOnClickListener {
            if (isAdminUser) {
                startActivity(Intent(this, OrderManagementActivity::class.java))
            } else {
                startActivity(Intent(this, com.restaurantclient.ui.order.MyOrdersActivity::class.java))
            }
        }

        binding.navFavorites.setOnClickListener {
            Toast.makeText(this, "Favorites coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAdminUi() {
        binding.adminModeBanner.isVisible = isAdminUser
        binding.adminAddProductFab.isVisible = isAdminUser

        if (isAdminUser) {
            binding.adminQuickUsersButton.setOnClickListener {
                startActivity(Intent(this, UserManagementActivity::class.java))
            }
            binding.adminQuickOrdersButton.setOnClickListener {
                startActivity(Intent(this, OrderManagementActivity::class.java))
            }
            binding.adminQuickDashboardButton.setOnClickListener {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
            }
            binding.adminAddProductFab.setOnClickListener {
                showProductEditor()
            }
            // Configure blur for admin banner
            val decorView = window.decorView
            val rootView = decorView.findViewById<android.view.ViewGroup>(android.R.id.content)
            val windowBackground = decorView.background
            binding.adminBannerBlurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(this))
                .setBlurRadius(BLUR_RADIUS)
                .setHasFixedTransformationMatrix(true)
            val overlay = ColorUtils.setAlphaComponent(
                ContextCompat.getColor(this, R.color.admin_glass_overlay), BLUR_OVERLAY_ALPHA
            )
            binding.adminBannerBlurView.setOverlayColor(overlay)
        }
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter(
            onClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.product_id)
                }
                startActivity(intent)
            },
            onAdminAction = { anchor, product ->
                showProductMenu(anchor, product)
            },
            isAdminMode = isAdminUser
        )
        // Use GridLayoutManager for modern 2-column grid
        binding.productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.productsRecyclerView.adapter = productListAdapter
    }

    private fun refreshProducts() {
        isFetchLoading = true
        updateLoadingState()
        productViewModel.fetchProducts()
    }

    private fun observeCartChanges() {
        lifecycleScope.launch {
            cartManager.cartItems.collectLatest { items ->
                updateCartBadge(cartManager.totalItems)
            }
        }
    }

    private fun showProductMenu(anchor: View, product: ProductResponse) {
        if (!isAdminUser) return
        PopupMenu(this, anchor).apply {
            menuInflater.inflate(R.menu.admin_product_actions, menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit_product -> {
                        showProductEditor(product)
                        true
                    }
                    R.id.action_delete_product -> {
                        confirmDeleteProduct(product)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }

    private fun showProductEditor(product: ProductResponse? = null) {
        val dialogBinding = DialogAdminProductBinding.inflate(LayoutInflater.from(this))
        dialogBinding.nameInput.setText(product?.name.orEmpty())
        dialogBinding.descriptionInput.setText(product?.description.orEmpty())
        dialogBinding.priceInput.setText(product?.price.orEmpty())
        dialogBinding.imageInput.setText(product?.product_image_uri.orEmpty())

        val dialogTitle = if (product == null) {
            R.string.dialog_create_product_title
        } else {
            R.string.dialog_edit_product_title
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(dialogTitle)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_save, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (validateProductForm(dialogBinding)) {
                    val name = dialogBinding.nameInput.text!!.toString().trim()
                    val description = dialogBinding.descriptionInput.text!!.toString().trim()
                    val price = dialogBinding.priceInput.text!!.toString().trim()
                    val image = dialogBinding.imageInput.text!!.toString().trim()

                    if (product == null) {
                        productViewModel.createProduct(name, description, price, image)
                    } else {
                        productViewModel.updateProduct(product.product_id, name, description, price, image)
                    }
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun validateProductForm(form: DialogAdminProductBinding): Boolean {
        var isValid = true
        val name = form.nameInput.text?.toString()?.trim().orEmpty()
        val price = form.priceInput.text?.toString()?.trim().orEmpty()

        if (name.isBlank()) {
            form.nameLayout.error = getString(R.string.error_product_name_required)
            isValid = false
        } else {
            form.nameLayout.error = null
        }

        val priceValue = price.toDoubleOrNull()
        if (price.isBlank() || priceValue == null) {
            form.priceLayout.error = getString(R.string.error_product_price_required)
            isValid = false
        } else {
            form.priceLayout.error = null
        }

        return isValid
    }

    private fun confirmDeleteProduct(product: ProductResponse) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_product_title, product.name))
            .setMessage(R.string.delete_product_message)
            .setPositiveButton(R.string.action_delete) { _, _ ->
                productViewModel.deleteProduct(product.product_id)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun setupObservers() {
        productViewModel.products.observe(this) { result ->
            isFetchLoading = false
            updateLoadingState()
            when (result) {
                is Result.Success -> {
                    allProducts = result.data
                    filterByCategory(selectedCategory)
                }
                is Result.Error -> {
                    Toast.makeText(this, getString(R.string.product_list_error, result.exception.message), Toast.LENGTH_LONG).show()
                }
            }
        }

        productViewModel.productMutation.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, getString(R.string.product_action_success), Toast.LENGTH_SHORT).show()
                    refreshProducts()
                }
                is Result.Error -> {
                    Toast.makeText(this, getString(R.string.product_action_error, result.exception.message), Toast.LENGTH_LONG).show()
                }
            }
        }

        productViewModel.mutationLoading.observe(this) { isLoading ->
            isMutationLoading = isLoading
            updateLoadingState()
        }
    }

    private fun filterByCategory(category: String) {
        val filteredProducts = if (category == "All") {
            allProducts
        } else {
            // Simple filtering - you can enhance this based on product categories
            allProducts.filter { product ->
                product.name.contains(category, ignoreCase = true) ||
                product.description.contains(category, ignoreCase = true)
            }
        }
        productListAdapter.submitList(filteredProducts)
    }

    private fun filterProducts(query: String) {
        val filteredProducts = allProducts.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
            product.description.contains(query, ignoreCase = true)
        }
        productListAdapter.submitList(filteredProducts)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isAdminUser) {
            menuInflater.inflate(R.menu.admin_main_menu, menu)
            cartBadgeMenuItem = null
        } else {
            menuInflater.inflate(R.menu.customer_main_menu, menu)
            cartBadgeMenuItem = menu.findItem(R.id.action_cart)
            updateCartBadge(cartManager.totalItems)
        }
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
                startActivity(Intent(this, com.restaurantclient.ui.cart.ShoppingCartActivity::class.java))
                true
            }
            R.id.action_my_orders -> {
                if (isAdminUser) {
                    startActivity(Intent(this, OrderManagementActivity::class.java))
                } else {
                    startActivity(Intent(this, com.restaurantclient.ui.order.MyOrdersActivity::class.java))
                }
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, com.restaurantclient.ui.user.UserProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_user_management -> {
                startActivity(Intent(this, UserManagementActivity::class.java))
                true
            }
            R.id.action_product_list -> {
                binding.productsRecyclerView.smoothScrollToPosition(0)
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

    private fun updateLoadingState() {
        binding.progressBar.isVisible = isFetchLoading || isMutationLoading
    }

    private fun setupGlassEffects() {
        configureBlur(binding.adminBannerBlurView, R.color.admin_glass_overlay, 20f)
        configureBlur(binding.customerBannerBlurView, R.color.customer_glass_overlay, 22f)
    }

    private fun configureBlur(blurView: BlurView, overlayColorRes: Int, radius: Float = 18f) {
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background
        blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
        val overlay = ColorUtils.setAlphaComponent(ContextCompat.getColor(this, overlayColorRes), 200)
        blurView.setOverlayColor(overlay)
    }
}
