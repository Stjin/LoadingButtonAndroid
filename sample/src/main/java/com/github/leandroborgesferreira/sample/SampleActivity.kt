package com.github.leandroborgesferreira.sample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        
        val button = findViewById<CircularProgressButton>(R.id.buttonTest)

        button.setOnClickListener {
            // Start the loading animation
            button.startAnimation()

            // Simulate a network request and revert after 3 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                button.revertAnimation()
            }, 3000)
        }
    }
}
