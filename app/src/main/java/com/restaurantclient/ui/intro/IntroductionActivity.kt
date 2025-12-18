package com.restaurantclient.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.restaurantclient.R
import com.restaurantclient.databinding.ActivityIntroBinding
import com.restaurantclient.ui.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var adapter: IntroProductAdapter
    private val products = listOf(
        // Zensai (Appetizers)
        IntroProduct(
            productId = 1,
            name = "Pork Gyoza",
            description = "Six pieces of pan-seared dumplings served with a soy-vinegar dipping sauce",
            price = 8.50,
            productImageUri = R.drawable.pork_gyoza,
            category = "Zensai"
        ),
        IntroProduct(
            productId = 2,
            name = "Edamame",
            description = "Steamed young soybeans tossed in flakey sea salt",
            price = 5.00,
            productImageUri = R.drawable.edamame,
            category = "Zensai"
        ),
        IntroProduct(
            productId = 3,
            name = "Shrimp Tempura",
            description = "Three pieces of lightly battered, crispy fried shrimp",
            price = 11.00,
            productImageUri = R.drawable.shrimp_tempura,
            category = "Zensai"
        ),
        // Sushi & Sashimi
        IntroProduct(
            productId = 4,
            name = "Maguro Nigiri",
            description = "Two pieces of fresh Bluefin tuna over hand-pressed vinegared rice",
            price = 12.00,
            productImageUri = R.drawable.maguro_nigiri,
            category = "Sushi & Sashimi"
        ),
        IntroProduct(
            productId = 5,
            name = "California Roll",
            description = "Classic roll with crab mix, avocado, and cucumber",
            price = 9.50,
            productImageUri = R.drawable.cali_roll,
            category = "Sushi & Sashimi"
        ),
        IntroProduct(
            productId = 6,
            name = "Salmon Sashimi",
            description = "Five thick slices of premium fresh Atlantic salmon",
            price = 14.50,
            productImageUri = R.drawable.salmon_sashimi,
            category = "Sushi & Sashimi"
        ),
        // Menrui (Noodles)
        IntroProduct(
            productId = 7,
            name = "Tonkotsu Ramen",
            description = "Rich pork bone broth, chashu pork, bamboo shoots, and a soft-boiled egg",
            price = 15.99,
            productImageUri = R.drawable.tonkotsu_ramen,
            category = "Menrui"
        ),
        IntroProduct(
            productId = 8,
            name = "Tempura Udon",
            description = "Thick wheat noodles in a clear dashi broth topped with shrimp tempura",
            price = 13.50,
            productImageUri = R.drawable.tempura_udon,
            category = "Menrui"
        ),
        IntroProduct(
            productId = 9,
            name = "Vegetable Yakisoba",
            description = "Stir-fried buckwheat noodles with cabbage, carrots, and savory sauce",
            price = 12.00,
            productImageUri = R.drawable.yakisoba,
            category = "Menrui"
        ),
        // Donburi (Rice Bowls)
        IntroProduct(
            productId = 10,
            name = "Gyu-Don",
            description = "Thinly sliced beef and onions simmered in a sweet soy dashi over rice",
            price = 12.50,
            productImageUri = R.drawable.gyudon,
            category = "Donburi"
        ),
        IntroProduct(
            productId = 11,
            name = "Katsu-Don",
            description = "Crispy pork cutlet and egg simmered in savory broth over rice",
            price = 13.50,
            productImageUri = R.drawable.katsudon,
            category = "Donburi"
        ),
        IntroProduct(
            productId = 12,
            name = "Unagi-Don",
            description = "Grilled freshwater eel glazed with sweet tare sauce over steamed rice",
            price = 21.00,
            productImageUri = R.drawable.unagi_don,
            category = "Donburi"
        ),
        // Kanmi (Desserts)
        IntroProduct(
            productId = 13,
            name = "Matcha Mochi",
            description = "Sweet glutinous rice cake filled with premium green tea cream",
            price = 6.50,
            productImageUri = R.drawable.matcha_mochi,
            category = "Kanmi"
        ),
        IntroProduct(
            productId = 14,
            name = "Taiyaki",
            description = "Fish-shaped waffle cake filled with sweet red bean paste",
            price = 7.00,
            productImageUri = R.drawable.taiyaki,
            category = "Kanmi"
        ),
        IntroProduct(
            productId = 15,
            name = "Black Sesame Ice Cream",
            description = "Creamy, nutty, and slightly savory roasted black sesame frozen treat",
            price = 5.50,
            productImageUri = R.drawable.sesame_ice_cream,
            category = "Kanmi"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCarousel()
        setupClickListeners()
        startAutoSlide()
    }

    private fun setupCarousel() {
        adapter = IntroProductAdapter(products)
        binding.productViewpager.adapter = adapter
        
        // Setup page indicator dots
        setupIndicators()
        
        // Update product name when page changes
        binding.productViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.productName.text = products[position].name
                updateIndicators(position)
            }
        })
        
        // Set initial product name
        binding.productName.text = products[0].name
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<View>(products.size)
        val layoutParams = LinearLayout.LayoutParams(
            16, 16
        ).apply {
            setMargins(4, 0, 4, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = View(this).apply {
                setBackgroundResource(R.drawable.circle_accent)
                alpha = if (i == 0) 1.0f else 0.3f
                this.layoutParams = layoutParams
            }
            binding.indicatorContainer.addView(indicators[i])
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until binding.indicatorContainer.childCount) {
            binding.indicatorContainer.getChildAt(i).alpha = if (i == position) 1.0f else 0.3f
        }
    }

    private fun startAutoSlide() {
        lifecycleScope.launch {
            while (isActive) {
                delay(3000) // Auto-slide every 3 seconds
                val nextItem = (binding.productViewpager.currentItem + 1) % adapter.itemCount
                binding.productViewpager.setCurrentItem(nextItem, true)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnStartOrdering.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
