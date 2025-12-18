package com.restaurantclient.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import androidx.recyclerview.widget.RecyclerView
import com.eightbitlab.com.blurview.BlurView
import com.eightbitlab.com.blurview.RenderScriptBlur
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.MainActivity
import com.restaurantclient.data.dto.CategoryDTO
import com.restaurantclient.R
import com.restaurantclient.data.CartManager
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.databinding.ActivityProductListBinding
import com.restaurantclient.databinding.ActivityProductListAdminBinding
import com.restaurantclient.databinding.DialogAdminProductBinding
import com.restaurantclient.ui.admin.AdminDashboardActivity
import com.restaurantclient.ui.admin.OrderManagementActivity
import com.restaurantclient.ui.admin.UserManagementActivity
import com.restaurantclient.ui.auth.AuthViewModel
import com.restaurantclient.ui.cart.ShoppingCartActivity
import com.restaurantclient.ui.common.setupGlassEffect
import com.restaurantclient.ui.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductListActivity : AppCompatActivity() {

    private var customerBinding: ActivityProductListBinding? = null
    private var adminBinding: ActivityProductListAdminBinding? = null
    private val productViewModel: ProductViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter
    
    @Inject
    lateinit var cartManager: CartManager
    
    private var cartBadgeMenuItem: MenuItem? = null
    private var isAdminUser: Boolean = false
    private var isFetchLoading: Boolean = false
    private var isMutationLoading: Boolean = false
    private var allProducts: List<ProductResponse> = emptyList()
    private var selectedCategoryId: Int? = null // null means "All"
    private var selectedCategoryName: String? = null // used when API omits ID

    companion object {
        private const val BLUR_RADIUS = 20f
        private const val BLUR_OVERLAY_ALPHA = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        authViewModel.loadStoredUserInfo()
        isAdminUser = authViewModel.isAdmin()
        
        // Inflate the appropriate layout based on user role
        if (isAdminUser) {
            adminBinding = ActivityProductListAdminBinding.inflate(layoutInflater)
            setContentView(adminBinding!!.root)
        } else {
            customerBinding = ActivityProductListBinding.inflate(layoutInflater)
            setContentView(customerBinding!!.root)
        }

        setupModernUi()
        setupAdminUi()
        setupRecyclerView()
        setupObservers()
        observeCartChanges()
        observeCategories()
        refreshProducts()
    }
    
    // Helper methods to access views from either binding (with explicit non-null assertion)
    private fun getSearchInput() = (if (isAdminUser) adminBinding!!.searchInput else customerBinding!!.searchInput)!!
    private fun getFilterButton() = (if (isAdminUser) adminBinding!!.filterButton else customerBinding!!.filterButton)!!
    private fun getProfileImage() = (if (isAdminUser) adminBinding!!.profileImage else customerBinding!!.profileImage)!!
    private fun getProductsRecyclerView() = (if (isAdminUser) adminBinding!!.productsRecyclerView else customerBinding!!.productsRecyclerView)!!
    private fun getProgressBar() = (if (isAdminUser) adminBinding!!.progressBar else customerBinding!!.progressBar)!!
    private fun getCategoryChipGroup() = (if (isAdminUser) adminBinding!!.categoryChipGroup else customerBinding!!.categoryChipGroup)!!
    
    private fun setupModernUi() {
        // Setup search functionality
        getSearchInput().setOnEditorActionListener { _, _, _ ->
            val query = getSearchInput().text.toString()
            if (query.isNotEmpty()) {
                filterProducts(query)
            }
            true
        }

        // Setup filter button
        getFilterButton().setOnClickListener {
            Toast.makeText(this, "Filter clicked", Toast.LENGTH_SHORT).show()
        }

        // Setup profile image click
        getProfileImage().setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        // Setup cart FAB for customer only
        if (!isAdminUser) {
            setupGlassFAB()
        }
    }
    
    private fun setupGlassFAB() {
        val fabBlur = customerBinding?.fabBlurContainer
        val fabClickable = customerBinding?.root?.findViewById<View>(R.id.fab_cart)
        
        // Setup blur effect for FAB glass background with stronger blur
        fabBlur?.setupGlassEffect(25f)  // Increased from 20f to 25f for more dominant effect
        fabBlur?.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND)
        fabBlur?.clipToOutline = true
        
        // FAB click action
        fabClickable?.setOnClickListener {
            startActivity(Intent(this, ShoppingCartActivity::class.java))
        }
        
        // Animate FAB on scroll
        setupFABScrollAnimation(fabBlur)
    }
    
    private fun setupFABScrollAnimation(fabContainer: View?) {
        fabContainer ?: return
        
        val fabCartContainer = customerBinding?.root?.findViewById<View>(R.id.fab_cart_container)
        fabCartContainer ?: return
        
        getProductsRecyclerView().addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    // Scrolling down - hide FAB with animation
                    fabCartContainer.animate()
                        .translationY(fabCartContainer.height.toFloat() + 100f)
                        .alpha(0f)
                        .setDuration(200)
                        .start()
                } else if (dy < 0) {
                    // Scrolling up - show FAB with animation
                    fabCartContainer.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(200)
                        .start()
                }
            }
        })
    }

    private fun setupAdminUi() {
        if (isAdminUser) {
            // Setup Glass FAB for admin
            adminBinding!!.adminAddProductFabBlur.setupGlassEffect(25f)
            
            adminBinding!!.adminAddProductFab.setOnClickListener {
                showProductEditor()
            }
            
            // Setup sort button
            adminBinding!!.adminSortButton?.setOnClickListener {
                Toast.makeText(this, "Sort options coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupBottomNavGlass() {
        // Only for customer UI if needed in future
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
        getProductsRecyclerView().layoutManager = GridLayoutManager(this, 2)
        getProductsRecyclerView().adapter = productListAdapter
    }

    private fun refreshProducts() {
        isFetchLoading = true
        updateLoadingState()
        productViewModel.fetchProducts()
    }

    private fun observeCartChanges() {
        if (!isAdminUser) {
            lifecycleScope.launch {
                cartManager.cartItems.collectLatest { items ->
                    val totalItems = cartManager.totalItems
                    updateCartBadge(totalItems)
                    updateFABBadge(totalItems)
                }
            }
        }
    }
    
    private fun updateFABBadge(itemCount: Int) {
        if (!isAdminUser) {
            val badge = customerBinding?.cartBadge
            if (itemCount > 0) {
                badge?.text = itemCount.toString()
                badge?.visibility = View.VISIBLE
            } else {
                badge?.visibility = View.GONE
            }
        }
    }

    private fun observeCategories() {
        lifecycleScope.launch {
            categoryViewModel.categories.collectLatest { categories ->
                setupDynamicCategoryChips(categories)
            }
        }
    }

    private fun setupDynamicCategoryChips(categories: List<com.restaurantclient.data.dto.CategoryDTO>) {
        getCategoryChipGroup().removeAllViews()
        
        // Add "All" chip first
        val allChip = Chip(this).apply {
            id = View.generateViewId()
            text = "All"
            tag = null // null tag means "All" category
            isCheckable = true
            isChecked = true
            setChipBackgroundColorResource(R.color.food_primary_red)
            setTextColor(getColor(R.color.white))
            chipStrokeWidth = 0f
        }
        getCategoryChipGroup().addView(allChip)
        
        // Add category chips dynamically
        categories.forEach { category ->
            val chip = Chip(this).apply {
                id = View.generateViewId()
                text = category.name
                tag = category // Keep reference for later lookup
                isCheckable = true
                setChipBackgroundColorResource(R.color.food_light_gray)
                setTextColor(getColor(R.color.food_dark_text))
                chipStrokeWidth = 2f
                chipStrokeColor = getColorStateList(R.color.food_primary_red)
            }
            getCategoryChipGroup().addView(chip)
        }
        
        // Update chip colors when selection changes
        getCategoryChipGroup().setOnCheckedStateChangeListener { group, checkedIds ->
            // Reset all chips to default style
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as? Chip
                chip?.apply {
                    if (!isChecked) {
                        setChipBackgroundColorResource(R.color.food_light_gray)
                        setTextColor(getColor(R.color.food_dark_text))
                        chipStrokeWidth = 2f
                        chipStrokeColor = getColorStateList(R.color.food_primary_red)
                    }
                }
            }
            
            // Style the selected chip
            if (checkedIds.isNotEmpty()) {
                val selectedChip = group.findViewById<Chip>(checkedIds[0])
                selectedChip?.apply {
                    setChipBackgroundColorResource(R.color.food_primary_red)
                    setTextColor(getColor(R.color.white))
                    chipStrokeWidth = 0f
                    
                    val selectedCategory = tag as? CategoryDTO
                    val categoryId = selectedCategory?.resolvedId
                    selectedCategoryId = categoryId
                    selectedCategoryName = when {
                        selectedCategory == null -> null
                        categoryId == null -> selectedCategory.name
                        else -> null
                    }
                    val fallbackName = selectedCategoryName ?: selectedCategory?.name
                    filterByCategory(categoryId, fallbackName)
                }
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
                    filterByCategory(selectedCategoryId, selectedCategoryName)
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

    private fun filterByCategory(categoryId: Int?, categoryNameFallback: String? = null) {
        if (categoryId == null && categoryNameFallback.isNullOrBlank()) {
            productListAdapter.submitList(allProducts)
            return
        }

        val filteredProducts = allProducts.filter { product ->
            val productCategories = product.categories ?: return@filter false
            productCategories.any { category ->
                val matchesId = categoryId != null && category.resolvedId == categoryId
                val matchesName = !categoryNameFallback.isNullOrBlank() &&
                    category.name.equals(categoryNameFallback, ignoreCase = true)
                matchesId || matchesName
            }
        }

        if (filteredProducts.isNotEmpty() || categoryId == null) {
            productListAdapter.submitList(filteredProducts)
            return
        }

        lifecycleScope.launch {
            getProgressBar().isVisible = true
            when (val result = productViewModel.getProductsByCategory(categoryId)) {
                is Result.Success -> {
                    productListAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@ProductListActivity,
                        "Failed to load category products: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    productListAdapter.submitList(allProducts)
                }
            }
            getProgressBar().isVisible = false
        }
    }

    private fun filterProducts(query: String) {
        val filteredProducts = allProducts.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
            product.description?.contains(query, ignoreCase = true) == true
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
                getProductsRecyclerView().smoothScrollToPosition(0)
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
        getProgressBar().isVisible = isFetchLoading || isMutationLoading
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
