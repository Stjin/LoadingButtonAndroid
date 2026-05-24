package com.github.leandroborgesferreira.loadingbutton.customViews

import android.content.Context
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CircularProgressButtonTest2 {
    @Test
    fun testShapeModel() {
        val context = androidx.appcompat.view.ContextThemeWrapper(
            ApplicationProvider.getApplicationContext<Context>(), 
            com.google.android.material.R.style.Theme_Material3Expressive_Light_NoActionBar
        )
        val button = CircularProgressButton(context)
        println("Initial Corner Radius: ${button.cornerRadius}")
        println("Initial Shape: ${button.shapeAppearanceModel.topLeftCornerSize}")
    }
}
