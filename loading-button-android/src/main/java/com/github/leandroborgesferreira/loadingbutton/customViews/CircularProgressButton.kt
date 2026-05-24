package com.github.leandroborgesferreira.loadingbutton.customViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable

class CircularProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    private var isMorphing = false
    private var isProgressing = false

    private var initialWidth: Int = 0
    private var initialLayoutParamsWidth: Int = 0
    private var initialText: CharSequence = ""
    private var initialPaddingLeft: Int = 0
    private var initialPaddingRight: Int = 0

    private val progressSpec by lazy {
        CircularProgressIndicatorSpec(
            context,
            null,
            0,
            com.google.android.material.R.style.Widget_Material3Expressive_CircularProgressIndicator_Wavy
        ).apply {
            indicatorColors = intArrayOf(textColors.defaultColor)
            indicatorSize = (this@CircularProgressButton.height * 0.6).toInt()
            trackThickness = (this@CircularProgressButton.height * 0.1).toInt()
        }
    }

    private val indeterminateDrawable by lazy {
        IndeterminateDrawable.createCircularDrawable(context, progressSpec)
    }

    init {
        iconGravity = ICON_GRAVITY_TEXT_START
        iconPadding = 0
    }

    fun startAnimation() {
        if (isMorphing || isProgressing) return
        isMorphing = true
        isClickable = false
        isPressed = false // Clear the press state visually
        
        initialWidth = width
        initialLayoutParamsWidth = layoutParams.width
        initialText = text
        initialPaddingLeft = paddingLeft
        initialPaddingRight = paddingRight

        val finalWidth = height // Make it a circle

        val widthAnimator = ValueAnimator.ofInt(initialWidth, finalWidth).apply {
            addUpdateListener {
                layoutParams = layoutParams.apply { width = it.animatedValue as Int }
            }
        }

        val paddingAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                val fraction = it.animatedValue as Float
                setPadding((initialPaddingLeft * fraction).toInt(), paddingTop, (initialPaddingRight * fraction).toInt(), paddingBottom)
            }
        }

        AnimatorSet().apply {
            playTogether(widthAnimator, paddingAnimator)
            duration = 300
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    text = "" // Hide text as it morphs
                }

                override fun onAnimationEnd(animation: Animator) {
                    isMorphing = false
                    isProgressing = true
                    
                    // Display and start Material 3 Wave animation
                    icon = indeterminateDrawable
                    (indeterminateDrawable as Animatable).start()
                }
            })
            start()
        }
    }

    fun revertAnimation() {
        if (isMorphing || !isProgressing) return
        isMorphing = true
        isProgressing = false

        // Stop and hide the spinner
        (indeterminateDrawable as Animatable).stop()
        icon = null

        val finalWidth = height

        val widthAnimator = ValueAnimator.ofInt(finalWidth, initialWidth).apply {
            addUpdateListener {
                layoutParams = layoutParams.apply { width = it.animatedValue as Int }
            }
        }

        val paddingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val fraction = it.animatedValue as Float
                setPadding((initialPaddingLeft * fraction).toInt(), paddingTop, (initialPaddingRight * fraction).toInt(), paddingBottom)
            }
        }

        AnimatorSet().apply {
            playTogether(widthAnimator, paddingAnimator)
            duration = 300
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    text = initialText
                    isMorphing = false
                    isClickable = true
                    layoutParams = layoutParams.apply { width = initialLayoutParamsWidth }
                }
            })
            start()
        }
    }
}
