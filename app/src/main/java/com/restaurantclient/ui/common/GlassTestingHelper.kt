package com.restaurantclient.ui.common

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.eightbitlab.com.blurview.BlurView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Testing utilities for glassmorphic UI components
 * Provides helpers for performance testing and UI testing
 */
object GlassTestingHelper {
    
    private const val TAG = "GlassTestingHelper"
    private const val TARGET_FPS = 60
    private const val FRAME_TIME_MS = 1000L / TARGET_FPS
    
    /**
     * Performance metrics for blur rendering
     */
    data class PerformanceMetrics(
        val averageFrameTime: Long,
        val fps: Float,
        val droppedFrames: Int,
        val totalFrames: Int,
        val memoryUsageMB: Float,
        val blurRenderTime: Long,
        val passesTarget: Boolean
    )
    
    /**
     * Frame timing tracker
     */
    class FrameTimingTracker {
        private val frameTimes = mutableListOf<Long>()
        private var startTime = 0L
        private var lastFrameTime = 0L
        private var droppedFrames = 0
        
        fun start() {
            startTime = System.currentTimeMillis()
            lastFrameTime = startTime
            frameTimes.clear()
            droppedFrames = 0
        }
        
        fun recordFrame() {
            val now = System.currentTimeMillis()
            val frameTime = now - lastFrameTime
            frameTimes.add(frameTime)
            
            if (frameTime > FRAME_TIME_MS * 2) {
                droppedFrames++
            }
            
            lastFrameTime = now
        }
        
        fun getMetrics(context: Context): PerformanceMetrics {
            val totalFrames = frameTimes.size
            val averageFrameTime = if (totalFrames > 0) {
                frameTimes.average().toLong()
            } else 0L
            
            val fps = if (averageFrameTime > 0) {
                1000f / averageFrameTime
            } else 0f
            
            val memoryStatus = MemoryOptimizer.getMemoryStatus(context)
            val memoryUsageMB = memoryStatus.usedMemory / (1024f * 1024f)
            
            return PerformanceMetrics(
                averageFrameTime = averageFrameTime,
                fps = fps,
                droppedFrames = droppedFrames,
                totalFrames = totalFrames,
                memoryUsageMB = memoryUsageMB,
                blurRenderTime = averageFrameTime,
                passesTarget = fps >= TARGET_FPS * 0.9 // Allow 10% variance
            )
        }
    }
    
    /**
     * Test blur view performance
     */
    fun testBlurPerformance(
        blurView: BlurView,
        context: Context,
        durationMs: Long = 5000L
    ): PerformanceMetrics {
        val tracker = FrameTimingTracker()
        tracker.start()
        
        val startTime = System.currentTimeMillis()
        var frameCount = 0
        
        // Simulate blur updates
        while (System.currentTimeMillis() - startTime < durationMs) {
            val radius = 10f + (frameCount % 20)
            blurView.setBlurRadius(radius)
            
            tracker.recordFrame()
            frameCount++
            
            Thread.sleep(FRAME_TIME_MS)
        }
        
        return tracker.getMetrics(context)
    }
    
    /**
     * Count blur views in view hierarchy
     */
    fun countBlurViews(view: View): Int {
        var count = 0
        
        if (view is BlurView) {
            count++
        }
        
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                count += countBlurViews(view.getChildAt(i))
            }
        }
        
        return count
    }
    
    /**
     * Find all blur views in activity
     */
    fun findAllBlurViews(activity: Activity): List<BlurView> {
        val blurViews = mutableListOf<BlurView>()
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        collectBlurViews(rootView, blurViews)
        return blurViews
    }
    
    private fun collectBlurViews(view: View, collection: MutableList<BlurView>) {
        if (view is BlurView) {
            collection.add(view)
        }
        
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                collectBlurViews(view.getChildAt(i), collection)
            }
        }
    }
    
    /**
     * Test accessibility compliance
     */
    fun testAccessibilityCompliance(activity: Activity): List<String> {
        val issues = mutableListOf<String>()
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        
        checkAccessibilityRecursive(rootView, issues, activity)
        
        return issues
    }
    
    private fun checkAccessibilityRecursive(
        view: View,
        issues: MutableList<String>,
        activity: Activity
    ) {
        // Check touch target size
        val density = view.resources.displayMetrics.density
        val minSize = (48 * density).toInt()
        
        if (view.isClickable && (view.width < minSize || view.height < minSize)) {
            issues.add("Touch target too small: ${view.javaClass.simpleName} (${view.width}x${view.height})")
        }
        
        // Check content description for clickable items
        if (view.isClickable && view.contentDescription.isNullOrEmpty()) {
            issues.add("Missing content description: ${view.javaClass.simpleName}")
        }
        
        // Check text contrast for TextViews
        if (view is android.widget.TextView) {
            val textColor = view.currentTextColor
            val backgroundColor = view.background?.let {
                if (it is android.graphics.drawable.ColorDrawable) it.color else null
            }
            
            backgroundColor?.let { bg ->
                if (!AccessibilityHelper.meetsWCAGAA(textColor, bg)) {
                    issues.add("Insufficient contrast: ${view.javaClass.simpleName} " +
                            "(ratio: ${AccessibilityHelper.calculateContrastRatio(textColor, bg)})")
                }
            }
        }
        
        // Recurse to children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                checkAccessibilityRecursive(view.getChildAt(i), issues, activity)
            }
        }
    }
    
    /**
     * Generate performance report
     */
    fun generatePerformanceReport(
        activity: Activity,
        testDurationMs: Long = 10000L
    ): String {
        val context = activity.applicationContext
        val blurViews = findAllBlurViews(activity)
        
        val report = StringBuilder()
        report.appendLine("=== Glass UI Performance Report ===")
        report.appendLine("Device: ${Build.MODEL}")
        report.appendLine("Android: ${Build.VERSION.SDK_INT}")
        report.appendLine("BlurView count: ${blurViews.size}")
        report.appendLine()
        
        // Memory status
        val memoryStatus = MemoryOptimizer.getMemoryStatus(context)
        report.appendLine("Memory Usage:")
        report.appendLine("  - Used: ${memoryStatus.usedMemory / (1024 * 1024)} MB")
        report.appendLine("  - Available: ${memoryStatus.availableMemory / (1024 * 1024)} MB")
        report.appendLine("  - Usage: ${(memoryStatus.usagePercent * 100).toInt()}%")
        report.appendLine("  - Low Memory: ${memoryStatus.isLowMemory}")
        report.appendLine()
        
        // Performance mode
        report.appendLine("Performance Mode: ${BlurPerformanceManager.getCurrentMode()}")
        report.appendLine("Reduce Effects: ${BlurPerformanceManager.isReduceEffectsEnabled()}")
        report.appendLine()
        
        // Accessibility
        val accessibilityIssues = testAccessibilityCompliance(activity)
        report.appendLine("Accessibility Issues: ${accessibilityIssues.size}")
        if (accessibilityIssues.isNotEmpty()) {
            accessibilityIssues.take(5).forEach { issue ->
                report.appendLine("  - $issue")
            }
            if (accessibilityIssues.size > 5) {
                report.appendLine("  ... and ${accessibilityIssues.size - 5} more")
            }
        }
        
        return report.toString()
    }
    
    /**
     * Log performance metrics
     */
    fun logPerformanceMetrics(metrics: PerformanceMetrics) {
        Log.d(TAG, "=== Performance Metrics ===")
        Log.d(TAG, "Average Frame Time: ${metrics.averageFrameTime}ms")
        Log.d(TAG, "FPS: ${metrics.fps}")
        Log.d(TAG, "Dropped Frames: ${metrics.droppedFrames}/${metrics.totalFrames}")
        Log.d(TAG, "Memory Usage: ${metrics.memoryUsageMB} MB")
        Log.d(TAG, "Passes Target: ${metrics.passesTarget}")
    }
}

