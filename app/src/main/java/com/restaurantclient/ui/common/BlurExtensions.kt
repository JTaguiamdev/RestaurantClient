package com.restaurantclient.ui.common

import android.view.View
import android.view.ViewOutlineProvider
import com.eightbitlab.com.blurview.BlurView

/**
 * Extension function to setup BlurView with common configurations
 * Note: BlurView v3.2.0 uses BlurTarget in XML, setup is minimal in code
 * 
 * @param blurRadius The blur radius (recommended: 15-25)
 */
fun BlurView.setupGlassEffect(
    blurRadius: Float = 20f
) {
    val radius = blurRadius.coerceIn(1f, 25f)
    this.setBlurRadius(radius)
    
    // Enable rounded corners clipping
    this.outlineProvider = ViewOutlineProvider.BACKGROUND
    this.clipToOutline = true
}

/**
 * Extension to apply glass effect styling to any view
 */
fun View.applyGlassEffect(elevation: Float = 8f, alpha: Float = 0.95f) {
    this.alpha = alpha
    this.elevation = elevation
}

/**
 * Extension to dynamically change blur radius (useful for animations)
 */
fun BlurView.setBlurIntensity(intensity: Float) {
    val radius = (intensity * 25f).coerceIn(0f, 25f)
    this.setBlurRadius(radius)
}

