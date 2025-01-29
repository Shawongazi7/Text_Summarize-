package com.example.Text_Summarizer.modules

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ResultScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        // Get the summary from the intent
        val summary = intent.getStringExtra("summary") ?: "No summary available"
        val originalText = intent.getStringExtra("originalText") ?: ""

        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val ans_back_btn: ImageButton = findViewById(R.id.ans_back_btn)
        val copy_btn: Button = findViewById(R.id.copy_btn)
        val result_view: EditText = findViewById(R.id.result_view)
        val saveButton: Button = findViewById(R.id.save_btn)
        val share_btn: Button = findViewById(R.id.share_btn)

        // Set the summary text
        result_view.setText(summary)

        share_btn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, summary)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share Summary")
            startActivity(shareIntent)
        }

        saveButton.setOnClickListener {
            val saveSummaryActivity = SaveSummaryActivity(summary, originalText)
            saveSummaryActivity.show(
                supportFragmentManager,
                SaveSummaryActivity::class.java.simpleName
            )
        }

        ans_back_btn.setOnClickListener {
            finish() // Better than starting a new activity
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        copy_btn.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("summary", summary)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        bottom_navigation.selectedItemId = R.id.page_1

        // Using setOnItemSelectedListener instead of deprecated setOnNavigationItemSelectedListener
        bottom_navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> true
//                R.id.page_2 -> {
//                    startActivity(Intent(this, RecentScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }

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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}