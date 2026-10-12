package com.instaconnect.android

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity

class SplashActivity : ComponentActivity() {

    private val splashDelay: Long = 2800 // 2.8 seconds display time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Fullscreen layout
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )

        val centerContainer = findViewById<View>(R.id.centerContainer)
        val imgLogo = findViewById<View>(R.id.imgLogo)

        // 1. Initial Fade-in & Scale Animation
        centerContainer.alpha = 0f
        centerContainer.scaleX = 0.8f
        centerContainer.scaleY = 0.8f

        centerContainer.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // 2. Continuous Pulse Animation on the Logo (GramPulse effect)
                val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.12f, 1.0f)
                val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.12f, 1.0f)

                val pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(imgLogo, scaleX, scaleY).apply {
                    duration = 1000
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
                pulseAnimator.start()
            }
            .start()

        // 3. Instagram Session Check & Navigation Trigger
        Handler(Looper.getMainLooper()).postDelayed({
            checkInstagramSession()
        }, splashDelay)
    }

    private fun checkInstagramSession() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
