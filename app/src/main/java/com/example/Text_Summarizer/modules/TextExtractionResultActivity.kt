package com.example.Text_Summarizer.modules

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class TextExtractionResultActivity : AppCompatActivity() {

    private lateinit var translator: Translator
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.text_extraction_result)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        // Get the original text from the intent
        val originalText = intent.getStringExtra("originalText") ?: "No text available"

        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val ans_back_btn: ImageButton = findViewById(R.id.ans_back_btn)
        val copy_btn: Button = findViewById(R.id.copy_btn)
        val result_view: EditText = findViewById(R.id.result_view)
        val saveButton: Button = findViewById(R.id.save_btn)
        val share_btn: Button = findViewById(R.id.share_btn)
        val summary_btn: Button = findViewById(R.id.summary_btn)
        val translate_btn: ImageButton = findViewById(R.id.btn_to_translate)
        progressBar = findViewById(R.id.progressBar)

        // Set the original text
        result_view.setText(originalText)

        // Apply Linkify to make URLs clickable
        Linkify.addLinks(result_view, Linkify.WEB_URLS)


        summary_btn.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java).apply {
                putExtra("Extracted_text", originalText)
            }
            startActivity(intent)
        }

        share_btn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, originalText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share Text")
            startActivity(shareIntent)
        }

        saveButton.setOnClickListener {
            val saveExtractedTextActivity = TextExtractionSavedActivity(originalText)
            saveExtractedTextActivity.show(
                supportFragmentManager,
                TextExtractionSavedActivity::class.java.simpleName
            )
        }

        ans_back_btn.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        copy_btn.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("originalText", originalText)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
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

        translate_btn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            detectAndTranslateText(originalText)
        }
    }

    private fun detectAndTranslateText(originalText: String) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(originalText)
            .addOnSuccessListener { languageCode ->
                if (languageCode != "und") {
                    translateText(originalText, languageCode)
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Could not identify language", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Language identification failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun translateText(originalText: String, sourceLanguage: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(TranslateLanguage.BENGALI)
            .build()
        translator = com.google.mlkit.nl.translate.Translation.getClient(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(originalText)
                    .addOnSuccessListener { translatedText ->
                        progressBar.visibility = View.GONE
                        val intent = Intent(this, TranslateActivity::class.java).apply {
                            putExtra("originalText", originalText)
                            putExtra("translatedText", translatedText)
                        }
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Translation failed: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Model download failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TextExtractionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        translator.close()
    }
}
