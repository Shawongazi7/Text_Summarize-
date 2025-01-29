package com.example.Text_Summarizer.modules

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R

class OnboardingScreen03 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_screen_03)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_color)

        val onboardingbtn03: ImageButton = findViewById(R.id.onboardingbtn03)
        val skipbtn03: TextView = findViewById(R.id.skipText03)

        onboardingbtn03.setOnClickListener {
            completeOnboarding()
        }

        skipbtn03.setOnClickListener {
            completeOnboarding()
        }
    }

    private fun completeOnboarding() {
        // Set the flag to false
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstRun", false)
        editor.apply()

        // Navigate to OnStartScreenActivity
        val intent = Intent(this, OnStartScreenActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}