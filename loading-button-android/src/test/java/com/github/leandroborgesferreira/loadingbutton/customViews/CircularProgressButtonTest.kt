package com.github.leandroborgesferreira.loadingbutton.customViews

import android.content.Context
import android.os.Looper
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CircularProgressButtonTest {

    @Test
    fun `test second animation triggers`() {
        val context = androidx.appcompat.view.ContextThemeWrapper(
            ApplicationProvider.getApplicationContext<Context>(), 
            com.google.android.material.R.style.Theme_Material3_Light_NoActionBar
        )
        val button = CircularProgressButton(context)
        button.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 100)
        button.measure(1000, 100)
        button.layout(0, 0, 1000, 100)
        
        button.startAnimation()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("Width should be animated to height", 100, button.layoutParams.width)
        
        button.revertAnimation()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("Width should be animated back to original", 1000, button.layoutParams.width)
        
        // Let's see if the second start works
        button.startAnimation()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("Width should be animated to height again", 100, button.layoutParams.width)
    }
}
