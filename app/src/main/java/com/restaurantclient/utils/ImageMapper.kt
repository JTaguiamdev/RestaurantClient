package com.restaurantclient.utils
import com.restaurantclient.R
object ImageMapper {
    
    private val imageMap = mapOf(
        "/images/pork-gyoza.jpg" to R.drawable.pork_gyoza,
        "/images/edamame.jpg" to R.drawable.edamame,
        "/images/shrimp-tempura.jpg" to R.drawable.shrimp_tempura,
        "/images/maguro-nigiri.jpg" to R.drawable.maguro_nigiri,
        "/images/cali-roll.jpg" to R.drawable.cali_roll,
        "/images/salmon-sashimi.jpg" to R.drawable.salmon_sashimi,
        "/images/tonkotsu-ramen.jpg" to R.drawable.tonkotsu_ramen,
        "/images/tempura-udon.jpg" to R.drawable.tempura_udon,
        "/images/yakisoba.jpg" to R.drawable.yakisoba,
        "/images/gyudon.jpg" to R.drawable.gyudon,
        "/images/katsudon.jpg" to R.drawable.katsudon,
        "/images/unagi-don.jpg" to R.drawable.unagi_don,
        "/images/matcha-mochi.jpg" to R.drawable.matcha_mochi,
        "/images/taiyaki.jpg" to R.drawable.taiyaki,
        "/images/sesame-ice-cream.jpg" to R.drawable.sesame_ice_cream
    )
    fun getDrawableResource(imageUri: String?): Int? {
        if (imageUri.isNullOrEmpty()) return null
        return imageMap[imageUri]
    }

    fun getDrawableResourceOrPlaceholder(imageUri: String?): Int {
        return getDrawableResource(imageUri) ?: R.drawable.ic_product_placeholder
    }
}
