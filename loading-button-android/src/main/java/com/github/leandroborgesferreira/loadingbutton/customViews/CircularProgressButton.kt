package com.github.leandroborgesferreira.loadingbutton.customViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Handler
import android.os.Looper
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

    private var morphAnimator: AnimatorSet? = null
    private var spinner: IndeterminateDrawable<*>? = null

    init {
        iconGravity = ICON_GRAVITY_TEXT_START
        iconPadding = 0
    }

    fun startAnimation() {
        if (isProgressing) return
        
        val currentWidth = if (isMorphing) {
            morphAnimator?.cancel()
            layoutParams.width
        } else {
            initialWidth = width
            initialLayoutParamsWidth = layoutParams.width
            initialText = text
            initialPaddingLeft = paddingLeft
            initialPaddingRight = paddingRight
            width
        }

        isMorphing = true
        isClickable = false
        isPressed = false // Clear the press state visually

        val finalWidth = height // Make it a circle

        val widthAnimator = ValueAnimator.ofInt(currentWidth, finalWidth).apply {
            addUpdateListener {
                layoutParams = layoutParams.apply { width = it.animatedValue as Int }
            }
        }

        val paddingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            val startPaddingLeft = paddingLeft
            val startPaddingRight = paddingRight
            addUpdateListener {
                val fraction = it.animatedValue as Float
                val currentLeft = startPaddingLeft + ((0 - startPaddingLeft) * fraction).toInt()
                val currentRight = startPaddingRight + ((0 - startPaddingRight) * fraction).toInt()
                setPadding(currentLeft, paddingTop, currentRight, paddingBottom)
            }
        }

        morphAnimator = AnimatorSet().apply {
            playTogether(widthAnimator, paddingAnimator)
            duration = 300
            addListener(object : AnimatorListenerAdapter() {
                private var isCanceled = false

                override fun onAnimationCancel(animation: Animator) {
                    isCanceled = true
                }

                override fun onAnimationStart(animation: Animator) {
                    text = "" // Hide text as it morphs
                    spinner?.start()
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (isCanceled) return
                    isMorphing = false
                    isProgressing = true

                    // Display and start Material 3 Wave animation
                    val spec = CircularProgressIndicatorSpec(
                        context,
                        null,
                        0,
                        com.google.android.material.R.style.Widget_Material3Expressive_CircularProgressIndicator_Wavy
                    ).apply {
                        indicatorColors = intArrayOf(textColors.defaultColor)
                        indicatorSize = (this@CircularProgressButton.height * 0.6).toInt()
                        trackThickness = (this@CircularProgressButton.height * 0.1).toInt()
                    }
                    spinner = IndeterminateDrawable.createCircularDrawable(context, spec)
                    icon = spinner

                }
            })
            start()
        }
    }

    fun revertAnimation() {
        if (!isMorphing && !isProgressing) return

        val currentWidth = if (isMorphing) {
            morphAnimator?.cancel()
            layoutParams.width
        } else {
            height
        }

        isMorphing = true
        isProgressing = false

        // Stop and hide the spinner
        spinner?.stop()
        spinner = null
        icon = null

        val widthAnimator = ValueAnimator.ofInt(currentWidth, initialWidth).apply {
            addUpdateListener {
                layoutParams = layoutParams.apply { width = it.animatedValue as Int }
            }
        }

        val paddingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            val startPaddingLeft = paddingLeft
            val startPaddingRight = paddingRight
            addUpdateListener {
                val fraction = it.animatedValue as Float
                val currentLeft = startPaddingLeft + ((initialPaddingLeft - startPaddingLeft) * fraction).toInt()
                val currentRight = startPaddingRight + ((initialPaddingRight - startPaddingRight) * fraction).toInt()
                setPadding(currentLeft, paddingTop, currentRight, paddingBottom)
            }
        }

        morphAnimator = AnimatorSet().apply {
            playTogether(widthAnimator, paddingAnimator)
            duration = 300
            addListener(object : AnimatorListenerAdapter() {
                private var isCanceled = false

                override fun onAnimationCancel(animation: Animator) {
                    isCanceled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (isCanceled) return
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
