package com.example.Text_Summarizer.modules

import android.content.Intent
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

        val onboardingbtn03 : ImageButton = findViewById(R.id.onboardingbtn03)
        val skipbtn03 : TextView = findViewById(R.id.skipText03)

        onboardingbtn03.setOnClickListener{
            val intent1 = Intent(this, OnboardingScreen04::class.java)
            startActivity(intent1)

        }

        skipbtn03.setOnClickListener{
            val intent2 = Intent( this, OnStartScreenActivity::class.java)
            startActivity(intent2)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}