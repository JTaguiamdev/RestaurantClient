package com.restaurantclient.ui.common

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.eightbitlab.com.blurview.BlurView

/**
 * Manager for optimizing BlurView performance across different devices
 * Automatically adjusts blur quality based on device capabilities
 */
object BlurPerformanceManager : LifecycleObserver {
    
    private const val PREFS_NAME = "blur_performance_prefs"
    private const val KEY_REDUCE_EFFECTS = "reduce_effects"
    private const val KEY_PERFORMANCE_MODE = "performance_mode"
    
    // Performance profiles
    enum class PerformanceMode {
        HIGH_QUALITY,    // High-end devices (60fps stable)
        BALANCED,        // Mid-range devices (optimize for balance)
        POWER_SAVER      // Low-end devices or battery saver enabled
    }
    
    private var currentMode: PerformanceMode = PerformanceMode.BALANCED
    private var reduceEffects: Boolean = false
    private var prefs: SharedPreferences? = null
    
    /**
     * Initialize performance manager
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        reduceEffects = prefs?.getBoolean(KEY_REDUCE_EFFECTS, false) ?: false
        
        // Detect device performance tier
        currentMode = detectPerformanceMode(context)
    }
    
    /**
     * Get optimal blur radius for current performance mode
     */
    fun getOptimalBlurRadius(requestedRadius: Float): Float {
        if (reduceEffects) return 0f
        
        return when (currentMode) {
            PerformanceMode.HIGH_QUALITY -> requestedRadius
            PerformanceMode.BALANCED -> (requestedRadius * 0.7f).coerceAtMost(20f)
            PerformanceMode.POWER_SAVER -> (requestedRadius * 0.4f).coerceAtMost(12f)
        }
    }
    
    /**
     * Get optimal sampling factor for blur rendering
     */
    fun getOptimalSamplingFactor(): Float {
        if (reduceEffects) return 8f
        
        return when (currentMode) {
            PerformanceMode.HIGH_QUALITY -> 4f
            PerformanceMode.BALANCED -> 6f
            PerformanceMode.POWER_SAVER -> 8f
        }
    }
    
    /**
     * Check if animations should be enabled
     */
    fun shouldEnableAnimations(): Boolean {
        if (reduceEffects) return false
        return currentMode != PerformanceMode.POWER_SAVER
    }
    
    /**
     * Check if blur effect should be applied
     */
    fun shouldApplyBlur(): Boolean {
        return !reduceEffects
    }
    
    /**
     * Detect device performance capabilities
     */
    private fun detectPerformanceMode(context: Context): PerformanceMode {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        
        val totalMemoryGB = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        val isLowRamDevice = activityManager?.isLowRamDevice ?: false
        
        return when {
            isLowRamDevice || totalMemoryGB < 3 -> PerformanceMode.POWER_SAVER
            totalMemoryGB >= 6 && Build.VERSION.SDK_INT >= 34 -> // API 34+
                PerformanceMode.HIGH_QUALITY
            else -> PerformanceMode.BALANCED
        }
    }
    
    /**
     * Enable or disable reduced effects mode
     */
    fun setReduceEffects(reduce: Boolean) {
        reduceEffects = reduce
        prefs?.edit()?.putBoolean(KEY_REDUCE_EFFECTS, reduce)?.apply()
    }
    
    /**
     * Check if reduced effects mode is enabled
     */
    fun isReduceEffectsEnabled(): Boolean = reduceEffects
    
    /**
     * Get current performance mode
     */
    fun getCurrentMode(): PerformanceMode = currentMode
    
    /**
     * Manually set performance mode
     */
    fun setPerformanceMode(mode: PerformanceMode) {
        currentMode = mode
        prefs?.edit()?.putString(KEY_PERFORMANCE_MODE, mode.name)?.apply()
    }
    
    /**
     * Lifecycle-aware pause blur effects when app is in background
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseBlurEffects() {
        // Blur effects are automatically paused when views are not visible
    }
    
    /**
     * Resume blur effects when app comes to foreground
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumeBlurEffects() {
        // Blur effects are automatically resumed when views become visible
    }
}

/**
 * Extension function to setup BlurView with performance optimizations
 */
fun BlurView.setupWithPerformance(
    blurRadius: Float = 20f
) {
    if (!BlurPerformanceManager.shouldApplyBlur()) {
        // Don't apply blur if reduced effects is enabled
        this.alpha = 0.95f
        return
    }
    
    val optimizedRadius = BlurPerformanceManager.getOptimalBlurRadius(blurRadius)
    
    this.setupGlassEffect(optimizedRadius)
}

/**
 * Memory-efficient blur setup for list items
 */
fun BlurView.setupForRecyclerView(blurRadius: Float = 15f) {
    if (!BlurPerformanceManager.shouldApplyBlur()) {
        this.alpha = 0.95f
        return
    }
    
    // Use lower blur radius for list items
    val optimizedRadius = BlurPerformanceManager.getOptimalBlurRadius(blurRadius * 0.8f)
    this.setupGlassEffect(optimizedRadius)
}

