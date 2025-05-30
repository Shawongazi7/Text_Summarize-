package com.example.Text_Summarizer.modules

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.Text_Summarizer.R
import com.google.firebase.auth.FirebaseAuth
//import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation
import androidx.core.content.ContextCompat

import com.airbnb.lottie.LottieAnimationView

class OnStartScreenActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.on_start)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_color)


        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            // User is logged in, redirect to HomeScreenActivity
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is not logged in, show the OnStartScreenActivity
            val signup_btn: Button = findViewById(R.id.signup_btn)

            val login_btn: Button = findViewById(R.id.login_btn)
            val skipbtn02: TextView = findViewById(R.id.skipText02)

            // Initializing LottieAnimationView
            val lottieAnimationView = findViewById<LottieAnimationView>(R.id.hello_bot)

            // Optionally set the animation resource
            lottieAnimationView.setAnimation(R.raw.hello_bot_ani)

            // Play the animation
            lottieAnimationView.playAnimation()

            signup_btn.setOnClickListener {
                val intent1 = Intent(this, RegisterActivity::class.java)
                startActivity(intent1)
            }

            login_btn.setOnClickListener {
                val intent2 = Intent(this, LoginActivity::class.java)
                startActivity(intent2)
            }

            skipbtn02.setOnClickListener {
                val intent2 = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent2)
            }
        }
    }
}