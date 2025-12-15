package com.eightbitlab.com.blurview

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Lightweight BlurView surrogate that mimics the third-party API using RenderEffect
 * so we can provide glassmorphism visuals without an external dependency.
 */
class BlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var blurRadius: Float = 0f

    fun setupWith(@Suppress("UNUSED_PARAMETER") rootView: ViewGroup): BlurView = this

    fun setFrameClearDrawable(@Suppress("UNUSED_PARAMETER") drawable: Drawable?): BlurView = this

    fun setBlurAlgorithm(@Suppress("UNUSED_PARAMETER") algorithm: RenderScriptBlur?): BlurView = this

    fun setBlurRadius(radius: Float): BlurView {
        blurRadius = radius
        applyBlur()
        return this
    }

    fun setHasFixedTransformationMatrix(@Suppress("UNUSED_PARAMETER") fixedMatrix: Boolean): BlurView = this

    fun setOverlayColor(color: Int) {
        setBackgroundColor(color)
    }

    private fun applyBlur() {
        if (blurRadius <= 0f) {
            setRenderEffect(null)
        } else {
            val effect = RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
            setRenderEffect(effect)
        }
    }
}
