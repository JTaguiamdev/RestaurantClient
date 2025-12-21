package com.restaurantclient.ui.common

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import android.view.View
import android.view.ViewTreeObserver
import com.eightbitlab.com.blurview.BlurView
import java.lang.ref.WeakReference

/**
 * Memory optimization utilities for glassmorphic UI
 * Monitors and optimizes memory usage for blur effects
 */
object MemoryOptimizer {
    
    private const val MAX_MEMORY_USAGE_PERCENT = 0.75 // 75% of available heap
    private const val MEMORY_WARNING_THRESHOLD = 0.85 // 85% usage triggers warning
    
    private val blurViewCache = mutableMapOf<String, WeakReference<BlurView>>()
    private val activityRefs = mutableSetOf<WeakReference<Activity>>()
    
    /**
     * Get current memory status
     */
    data class MemoryStatus(
        val totalMemory: Long,
        val usedMemory: Long,
        val availableMemory: Long,
        val usagePercent: Float,
        val isLowMemory: Boolean
    )
    
    /**
     * Get current memory usage information
     */
    fun getMemoryStatus(context: Context): MemoryStatus {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        
        val usedMemory = totalMemory - freeMemory
        val availableMemory = maxMemory - usedMemory
        val usagePercent = (usedMemory.toFloat() / maxMemory.toFloat())
        
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        
        return MemoryStatus(
            totalMemory = totalMemory,
            usedMemory = usedMemory,
            availableMemory = availableMemory,
            usagePercent = usagePercent,
            isLowMemory = memoryInfo.lowMemory
        )
    }
    
    /**
     * Check if memory usage is approaching limit
     */
    fun isMemoryWarning(context: Context): Boolean {
        val status = getMemoryStatus(context)
        return status.usagePercent >= MEMORY_WARNING_THRESHOLD || status.isLowMemory
    }
    
    /**
     * Register activity for memory monitoring
     */
    fun registerActivity(activity: Activity) {
        activityRefs.add(WeakReference(activity))
        cleanupStaleReferences()
    }
    
    /**
     * Unregister activity
     */
    fun unregisterActivity(activity: Activity) {
        activityRefs.removeIf { it.get() == activity || it.get() == null }
    }
    
    /**
     * Clean up stale weak references
     */
    private fun cleanupStaleReferences() {
        activityRefs.removeIf { it.get() == null }
        blurViewCache.entries.removeIf { it.value.get() == null }
    }
    
    /**
     * Cache BlurView to prevent recreation
     */
    fun cacheBlurView(key: String, blurView: BlurView) {
        blurViewCache[key] = WeakReference(blurView)
        cleanupStaleReferences()
    }
    
    /**
     * Get cached BlurView
     */
    fun getCachedBlurView(key: String): BlurView? {
        return blurViewCache[key]?.get()
    }
    
    /**
     * Clear blur view cache
     */
    fun clearBlurCache() {
        blurViewCache.clear()
    }
    
    /**
     * Optimize blur view for memory efficiency
     */
    fun optimizeBlurView(blurView: BlurView, context: Context) {
        val status = getMemoryStatus(context)
        
        when {
            status.usagePercent >= MEMORY_WARNING_THRESHOLD -> {
                // Critical: reduce blur significantly
                blurView.setBlurRadius(BlurPerformanceManager.getOptimalBlurRadius(10f))
            }
            status.usagePercent >= MAX_MEMORY_USAGE_PERCENT -> {
                // Warning: reduce blur moderately
                blurView.setBlurRadius(BlurPerformanceManager.getOptimalBlurRadius(15f))
            }
            else -> {
                // Normal: use standard blur
                blurView.setBlurRadius(BlurPerformanceManager.getOptimalBlurRadius(20f))
            }
        }
    }
    
    /**
     * Trigger memory cleanup
     */
    fun performMemoryCleanup() {
        System.gc()
        clearBlurCache()
        cleanupStaleReferences()
    }
    
    /**
     * Monitor memory and adjust blur effects automatically
     */
    fun startMemoryMonitoring(activity: Activity) {
        registerActivity(activity)
        
        val contentView = activity.window.decorView.findViewById<View>(android.R.id.content)
        
        contentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isMemoryWarning(activity)) {
                    // Reduce blur effects automatically
                    reduceBlurEffectsInActivity(activity)
                }
            }
        })
    }
    
    /**
     * Reduce blur effects in an activity
     */
    private fun reduceBlurEffectsInActivity(activity: Activity) {
        val contentView = activity.window.decorView.findViewById<View>(android.R.id.content)
        reduceBlurEffectsInView(contentView, activity)
    }
    
    /**
     * Recursively reduce blur effects in view hierarchy
     */
    private fun reduceBlurEffectsInView(view: View, context: Context) {
        if (view is BlurView) {
            optimizeBlurView(view, context)
        }
        
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                reduceBlurEffectsInView(view.getChildAt(i), context)
            }
        }
    }
    
    /**
     * Create memory-efficient blur view
     */
    fun createMemoryEfficientBlurView(
        context: Context,
        blurRadius: Float = 20f
    ): Float {
        val status = getMemoryStatus(context)
        val baseRadius = BlurPerformanceManager.getOptimalBlurRadius(blurRadius)
        
        return when {
            status.isLowMemory -> baseRadius * 0.5f
            status.usagePercent >= MEMORY_WARNING_THRESHOLD -> baseRadius * 0.7f
            status.usagePercent >= MAX_MEMORY_USAGE_PERCENT -> baseRadius * 0.85f
            else -> baseRadius
        }
    }
    
    /**
     * Check if bitmap can be safely loaded given current memory
     */
    fun canLoadBitmap(context: Context, estimatedSizeBytes: Long): Boolean {
        val status = getMemoryStatus(context)
        return status.availableMemory > (estimatedSizeBytes * 2) // 2x buffer for safety
    }
    
    /**
     * Get recommended image scale factor based on memory
     */
    fun getRecommendedImageScale(context: Context): Float {
        val status = getMemoryStatus(context)
        
        return when {
            status.usagePercent >= MEMORY_WARNING_THRESHOLD -> 0.5f
            status.usagePercent >= MAX_MEMORY_USAGE_PERCENT -> 0.7f
            else -> 1.0f
        }
    }
}

/**
 * Extension function to monitor view memory usage
 */
fun View.monitorMemoryUsage() {
    val context = this.context
    
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        private var lastCheck = System.currentTimeMillis()
        
        override fun onGlobalLayout() {
            val now = System.currentTimeMillis()
            if (now - lastCheck >= 5000) { // Check every 5 seconds
                lastCheck = now
                
                if (MemoryOptimizer.isMemoryWarning(context)) {
                    // Post warning or reduce effects
                    if (this@monitorMemoryUsage is BlurView) {
                        MemoryOptimizer.optimizeBlurView(this@monitorMemoryUsage, context)
                    }
                }
            }
        }
    })
}

/**
 * Extension to setup blur with memory optimization
 */
fun BlurView.setupWithMemoryOptimization(
    context: Context,
    requestedRadius: Float = 20f
) {
    val optimizedRadius = MemoryOptimizer.createMemoryEfficientBlurView(context, requestedRadius)
    this.setupWithPerformance(optimizedRadius)
    
    // Cache this blur view
    val key = "${context.javaClass.simpleName}_${this.id}"
    MemoryOptimizer.cacheBlurView(key, this)
}

