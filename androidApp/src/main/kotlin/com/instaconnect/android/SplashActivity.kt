package com.instaconnect.android

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity

class SplashActivity : ComponentActivity() {

    private val splashDelay: Long = 3100 // 3.1 segundos para apreciar la salida del nombre y el latido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Pantalla completa sin barras
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )

        val logoCard = findViewById<View>(R.id.logoCard)
        val txtTitle = findViewById<View>(R.id.txtTitle)
        val txtSubtitle = findViewById<View>(R.id.txtSubtitle)
        val bottomSection = findViewById<View>(R.id.bottomSection)

        // Estado inicial de los elementos (ocultos y preparados para emerger)
        logoCard.alpha = 0f
        logoCard.scaleX = 0.35f
        logoCard.scaleY = 0.35f

        // El nombre de la app comienza oculto y desplazado hacia abajo para emerger
        txtTitle.alpha = 0f
        txtTitle.translationY = 90f
        txtTitle.scaleX = 0.85f
        txtTitle.scaleY = 0.85f

        txtSubtitle.alpha = 0f
        txtSubtitle.translationY = 30f

        bottomSection.alpha = 0f

        // FASE 1: Entrada del logo con rebote (0 - 550ms)
        logoCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(550)
            .setInterpolator(OvershootInterpolator(1.4f))
            .start()

        // FASE 2: El nombre de la app emerge dinámicamente desde abajo (a los 450ms)
        Handler(Looper.getMainLooper()).postDelayed({
            txtTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(550)
                .setInterpolator(OvershootInterpolator(1.4f))
                .withEndAction {
                    // FASE 3: Animación de latido cardíaco (Heartbeat / Pulso de GramPulse)
                    startHeartbeatAnimation(txtTitle, logoCard)
                }
                .start()

            // Subtítulo se desvanece suavemente
            txtSubtitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(200)
                .setInterpolator(DecelerateInterpolator())
                .start()

            // Marca inferior "from META"
            bottomSection.animate()
                .alpha(1f)
                .setDuration(700)
                .setStartDelay(350)
                .start()
        }, 450)

        // FASE 4: Transición suave hacia MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            checkInstagramSession()
        }, splashDelay)
    }

    /**
     * Efecto de latido cardíaco ("lub-dub" doble pulso) aplicado al nombre
     * de la app con expansión de pulso y eco.
     */
    private fun startHeartbeatAnimation(title: View, logo: View) {
        // Latido 1 (Primer golpe rápido y vigoroso)
        val beat1TitleX = ObjectAnimator.ofFloat(title, View.SCALE_X, 1.0f, 1.25f).setDuration(120)
        val beat1TitleY = ObjectAnimator.ofFloat(title, View.SCALE_Y, 1.0f, 1.25f).setDuration(120)
        val beat1LogoX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 1.0f, 1.15f).setDuration(120)
        val beat1LogoY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 1.0f, 1.15f).setDuration(120)

        // Recoil intermedio
        val recoilTitleX = ObjectAnimator.ofFloat(title, View.SCALE_X, 1.25f, 0.95f).setDuration(90)
        val recoilTitleY = ObjectAnimator.ofFloat(title, View.SCALE_Y, 1.25f, 0.95f).setDuration(90)
        val recoilLogoX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 1.15f, 0.96f).setDuration(90)
        val recoilLogoY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 1.15f, 0.96f).setDuration(90)

        // Latido 2 (Segundo golpe / eco cardíaco)
        val beat2TitleX = ObjectAnimator.ofFloat(title, View.SCALE_X, 0.95f, 1.15f).setDuration(110)
        val beat2TitleY = ObjectAnimator.ofFloat(title, View.SCALE_Y, 0.95f, 1.15f).setDuration(110)
        val beat2LogoX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.96f, 1.08f).setDuration(110)
        val beat2LogoY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.96f, 1.08f).setDuration(110)

        // Retorno al reposo normal
        val restTitleX = ObjectAnimator.ofFloat(title, View.SCALE_X, 1.15f, 1.0f).setDuration(160)
        val restTitleY = ObjectAnimator.ofFloat(title, View.SCALE_Y, 1.15f, 1.0f).setDuration(160)
        val restLogoX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 1.08f, 1.0f).setDuration(160)
        val restLogoY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 1.08f, 1.0f).setDuration(160)

        val firstHeartbeat = AnimatorSet().apply {
            play(beat1TitleX).with(beat1TitleY).with(beat1LogoX).with(beat1LogoY)
            play(recoilTitleX).with(recoilTitleY).with(recoilLogoX).with(recoilLogoY).after(beat1TitleX)
            play(beat2TitleX).with(beat2TitleY).with(beat2LogoX).with(beat2LogoY).after(recoilTitleX)
            play(restTitleX).with(restTitleY).with(restLogoX).with(restLogoY).after(beat2TitleX)
        }

        firstHeartbeat.start()

        // Segundo ciclo de latido tras 350ms de pausa (ritmo cardíaco natural)
        Handler(Looper.getMainLooper()).postDelayed({
            val secondHeartbeat = AnimatorSet().apply {
                val b1X = ObjectAnimator.ofFloat(title, View.SCALE_X, 1.0f, 1.20f).setDuration(120)
                val b1Y = ObjectAnimator.ofFloat(title, View.SCALE_Y, 1.0f, 1.20f).setDuration(120)
                val l1X = ObjectAnimator.ofFloat(logo, View.SCALE_X, 1.0f, 1.12f).setDuration(120)
                val l1Y = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 1.0f, 1.12f).setDuration(120)

                val rcX = ObjectAnimator.ofFloat(title, View.SCALE_X, 1.20f, 0.96f).setDuration(90)
                val rcY = ObjectAnimator.ofFloat(title, View.SCALE_Y, 1.20f, 0.96f).setDuration(90)

                val b2X = ObjectAnimator.ofFloat(title, View.SCALE_X, 0.96f, 1.10f).setDuration(110)
                val b2Y = ObjectAnimator.ofFloat(title, View.SCALE_Y, 0.96f, 1.10f).setDuration(110)

                val rsX = ObjectAnimator.ofFloat(title, View.SCALE_X, 1.10f, 1.0f).setDuration(150)
                val rsY = ObjectAnimator.ofFloat(title, View.SCALE_Y, 1.10f, 1.0f).setDuration(150)
                val rslX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 1.12f, 1.0f).setDuration(240)
                val rslY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 1.12f, 1.0f).setDuration(240)

                play(b1X).with(b1Y).with(l1X).with(l1Y)
                play(rcX).with(rcY).after(b1X)
                play(b2X).with(b2Y).after(rcX)
                play(rsX).with(rsY).with(rslX).with(rslY).after(b2X)
            }
            secondHeartbeat.start()
        }, 650)
    }

    private fun checkInstagramSession() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
