package com.restaurantclient.utils

import com.restaurantclient.R

/**
 * Maps backend image URIs to local drawable resources
 * Since backend doesn't serve images, we store them locally
 * 
 * TO ADD IMAGES:
 * 1. Add image files to app/src/main/res/drawable/ with these names:
 *    - pork_gyoza.jpg, edamame.jpg, shrimp_tempura.jpg, etc.
 * 2. Uncomment the mapping entries below
 * 3. Rebuild the app
 */
object ImageMapper {
    
    private val imageMap = mapOf<String, Int>(
        // TODO: Add images to drawable folder and uncomment these:
        // "/images/pork-gyoza.jpg" to R.drawable.pork_gyoza,
        // "/images/edamame.jpg" to R.drawable.edamame,
        // "/images/shrimp-tempura.jpg" to R.drawable.shrimp_tempura,
        // "/images/maguro-nigiri.jpg" to R.drawable.maguro_nigiri,
        // "/images/cali-roll.jpg" to R.drawable.cali_roll,
        // "/images/salmon-sashimi.jpg" to R.drawable.salmon_sashimi,
        // "/images/tonkotsu-ramen.jpg" to R.drawable.tonkotsu_ramen,
        // "/images/tempura-udon.jpg" to R.drawable.tempura_udon,
        // "/images/yakisoba.jpg" to R.drawable.yakisoba,
        // "/images/gyudon.jpg" to R.drawable.gyudon,
        // "/images/katsudon.jpg" to R.drawable.katsudon,
        // "/images/unagi-don.jpg" to R.drawable.unagi_don,
        // "/images/matcha-mochi.jpg" to R.drawable.matcha_mochi,
        // "/images/taiyaki.jpg" to R.drawable.taiyaki,
        // "/images/sesame-ice-cream.jpg" to R.drawable.sesame_ice_cream
    )
    
    /**
     * Get drawable resource ID from backend image URI
     * @param imageUri The URI from backend like "/images/pork-gyoza.jpg"
     * @return Drawable resource ID or null if not found
     */
    fun getDrawableResource(imageUri: String?): Int? {
        if (imageUri.isNullOrEmpty()) return null
        return imageMap[imageUri]
    }
    
    /**
     * Get drawable resource ID with fallback to placeholder
     * @param imageUri The URI from backend
     * @return Drawable resource ID (fallback to placeholder if not found)
     */
    fun getDrawableResourceOrPlaceholder(imageUri: String?): Int {
        return getDrawableResource(imageUri) ?: R.drawable.ic_product_placeholder
    }
}
