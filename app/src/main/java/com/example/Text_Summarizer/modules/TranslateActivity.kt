package com.example.Text_Summarizer.modules

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class TranslateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translate_activity)
        val ans_back_btn: ImageButton = findViewById(R.id.ans_back_btn)
        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val translatedText = intent.getStringExtra("translatedText") ?: "No translation available"
        val originalText = intent.getStringExtra("originalText") ?: "No text available"

        val translatedTextView: TextView = findViewById(R.id.result_view)
        val originalTextView: TextView = findViewById(R.id.result_view_2)


        originalTextView.setText(originalText)
        translatedTextView.setText(translatedText)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        ans_back_btn.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        bottom_navigation.selectedItemId = R.id.page_2

        bottom_navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

                R.id.page_2 -> true
                R.id.page_3 -> {
                    startActivity(Intent(this, SavedScreenActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

                R.id.page_4 -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

                else -> false
            }
        }
    }
}