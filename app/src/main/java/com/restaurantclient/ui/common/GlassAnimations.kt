package com.restaurantclient.ui.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eightbitlab.com.blurview.BlurView

/**
 * Enhanced animation utilities for glassmorphic UI elements
 * Provides smooth, performant animations with blur effects
 */
object GlassAnimations {
    
    private const val DEFAULT_DURATION = 300L
    private const val FAST_DURATION = 200L
    private const val SLOW_DURATION = 500L
    
    /**
     * Fade in animation with glass effect
     */
    fun View.fadeInGlass(duration: Long = DEFAULT_DURATION, onComplete: (() -> Unit)? = null) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            alpha = 1f
            visibility = View.VISIBLE
            onComplete?.invoke()
            return
        }
        
        alpha = 0f
        visibility = View.VISIBLE
        
        animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onComplete?.invoke()
                }
            })
            .start()
    }
    
    /**
     * Fade out animation with glass effect
     */
    fun View.fadeOutGlass(duration: Long = DEFAULT_DURATION, onComplete: (() -> Unit)? = null) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            alpha = 0f
            visibility = View.GONE
            onComplete?.invoke()
            return
        }
        
        animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                    onComplete?.invoke()
                }
            })
            .start()
    }
    
    /**
     * Scale up animation with glass effect (entrance)
     */
    fun View.scaleInGlass(duration: Long = DEFAULT_DURATION, onComplete: (() -> Unit)? = null) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
            visibility = View.VISIBLE
            onComplete?.invoke()
            return
        }
        
        scaleX = 0.8f
        scaleY = 0.8f
        alpha = 0f
        visibility = View.VISIBLE
        
        animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator(1.2f))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onComplete?.invoke()
                }
            })
            .start()
    }
    
    /**
     * Scale down animation with glass effect (exit)
     */
    fun View.scaleOutGlass(duration: Long = FAST_DURATION, onComplete: (() -> Unit)? = null) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
            visibility = View.GONE
            onComplete?.invoke()
            return
        }
        
        animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                    onComplete?.invoke()
                }
            })
            .start()
    }
    
    /**
     * Slide up animation with glass effect
     */
    fun View.slideUpGlass(duration: Long = DEFAULT_DURATION, onComplete: (() -> Unit)? = null) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            translationY = 0f
            alpha = 1f
            visibility = View.VISIBLE
            onComplete?.invoke()
            return
        }
        
        translationY = height.toFloat()
        alpha = 0f
        visibility = View.VISIBLE
        
        animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onComplete?.invoke()
                }
            })
            .start()
    }
    
    /**
     * Slide down animation with glass effect
     */
    fun View.slideDownGlass(duration: Long = DEFAULT_DURATION, onComplete: (() -> Unit)? = null) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            translationY = height.toFloat()
            alpha = 0f
            visibility = View.GONE
            onComplete?.invoke()
            return
        }
        
        animate()
            .translationY(height.toFloat())
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                    onComplete?.invoke()
                }
            })
            .start()
    }
    
    /**
     * Animate blur radius dynamically
     */
    fun BlurView.animateBlurRadius(
        targetRadius: Float,
        duration: Long = DEFAULT_DURATION,
        onComplete: (() -> Unit)? = null
    ) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            setBlurRadius(targetRadius)
            onComplete?.invoke()
            return
        }
        
        val optimizedTarget = BlurPerformanceManager.getOptimalBlurRadius(targetRadius)
        
        val animator = ValueAnimator.ofFloat(0f, optimizedTarget)
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            setBlurRadius(value)
        }
        
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete?.invoke()
            }
        })
        
        animator.start()
    }
    
    /**
     * Pulse animation for attention (e.g., new notification)
     */
    fun View.pulseGlass(
        scaleFrom: Float = 1f,
        scaleTo: Float = 1.1f,
        duration: Long = FAST_DURATION,
        repeatCount: Int = 2
    ) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            return
        }
        
        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", scaleFrom, scaleTo, scaleFrom)
        val scaleY = ObjectAnimator.ofFloat(this, "scaleY", scaleFrom, scaleTo, scaleFrom)
        
        scaleX.duration = duration
        scaleY.duration = duration
        scaleX.repeatCount = repeatCount
        scaleY.repeatCount = repeatCount
        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()
        
        scaleX.start()
        scaleY.start()
    }
    
    /**
     * Shimmer effect for loading states
     */
    fun View.shimmerGlass(duration: Long = SLOW_DURATION) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            return
        }
        
        val animator = ObjectAnimator.ofFloat(this, "alpha", 0.5f, 1f, 0.5f)
        animator.duration = duration
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
        
        // Store animator to cancel later if needed
        setTag(com.restaurantclient.R.id.glass_animator_tag, animator)
    }
    
    /**
     * Stop shimmer effect
     */
    fun View.stopShimmer() {
        val animator = getTag(com.restaurantclient.R.id.glass_animator_tag) as? ValueAnimator
        animator?.cancel()
        alpha = 1f
    }
    
    /**
     * Stagger animation for list items
     */
    fun List<View>.staggeredFadeIn(
        delayBetween: Long = 50L,
        duration: Long = DEFAULT_DURATION
    ) {
        if (!BlurPerformanceManager.shouldEnableAnimations()) {
            forEach { it.visibility = View.VISIBLE }
            return
        }
        
        forEachIndexed { index, view ->
            view.alpha = 0f
            view.visibility = View.VISIBLE
            
            view.animate()
                .alpha(1f)
                .setStartDelay(index * delayBetween)
                .setDuration(duration)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }
}

