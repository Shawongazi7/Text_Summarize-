package com.example.Text_Summarizer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.modules.OnboardingScreen01
import com.example.Text_Summarizer.modules.OnStartScreenActivity
import com.example.Text_Summarizer.modules.HomeScreenActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_color)
        FirebaseApp.initializeApp(this)

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        handler.postDelayed({
            if (isFirstRun) {
                // Navigate to OnboardingScreen01
                val intent = Intent(this, OnboardingScreen01::class.java)
                startActivity(intent)
            } else {
                if (isLoggedIn) {
                    // Navigate to HomeScreenActivity
                    val intent = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intent)
                } else {
                    // Navigate to OnStartScreenActivity
                    val intent = Intent(this, OnStartScreenActivity::class.java)
                    startActivity(intent)
                }
            }
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, 2000L)
    }
}