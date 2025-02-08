//without the translation button
//package com.example.Text_Summarizer.modules
//
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.text.util.Linkify
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.example.Text_Summarizer.R
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class TextExtractionResultActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.text_extraction_result)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//
//        // Get the original text from the intent
//        val originalText = intent.getStringExtra("originalText") ?: "No text available"
//
//        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        val ans_back_btn: ImageButton = findViewById(R.id.ans_back_btn)
//        val copy_btn: Button = findViewById(R.id.copy_btn)
//        val result_view: EditText = findViewById(R.id.result_view)
//        val saveButton: Button = findViewById(R.id.save_btn)
//        val share_btn: Button = findViewById(R.id.share_btn)
//        val summary_btn: Button = findViewById(R.id.summary_btn)
//        val translate_btn: ImageButton = findViewById(R.id.btn_to_translate)
//
//        // Set the original text
//        result_view.setText(originalText)
//
//        // Apply Linkify to make URLs clickable
//        Linkify.addLinks(result_view, Linkify.WEB_URLS)
//
//        summary_btn.setOnClickListener {
//            val intent = Intent(this, HomeScreenActivity::class.java).apply {
//                putExtra("Extracted_text", originalText)
//            }
//            startActivity(intent)
//        }
//
//        share_btn.setOnClickListener {
//            val sendIntent: Intent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, originalText)
//                type = "text/plain"
//            }
//
//            val shareIntent = Intent.createChooser(sendIntent, "Share Text")
//            startActivity(shareIntent)
//        }
//
//        saveButton.setOnClickListener {
//            val saveExtractedTextActivity = TextExtractionSavedActivity(originalText)
//            saveExtractedTextActivity.show(
//                supportFragmentManager,
//                TextExtractionSavedActivity::class.java.simpleName
//            )
//        }
//
//        ans_back_btn.setOnClickListener {
//            finish() // Better than starting a new activity
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        }
//
//        copy_btn.setOnClickListener {
//            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("originalText", originalText)
//            clipboardManager.setPrimaryClip(clipData)
//
//            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
//        }
//
//        bottom_navigation.selectedItemId = R.id.page_2
//
//        // Using setOnItemSelectedListener instead of deprecated setOnNavigationItemSelectedListener
//        bottom_navigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_2 -> true
//
//                R.id.page_3 -> {
//                    startActivity(Intent(this, SavedScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_4 -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//    }
//}

//with the translation button
//
//package com.example.Text_Summarizer.modules
//
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.text.util.Linkify
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.example.Text_Summarizer.R
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.mlkit.nl.translate.TranslateLanguage
//import com.google.mlkit.nl.translate.Translator
//import com.google.mlkit.nl.translate.TranslatorOptions
//
//class TextExtractionResultActivity : AppCompatActivity() {
//
//    private lateinit var translator: Translator
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.text_extraction_result)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//
//        // Get the original text from the intent
//        val originalText = intent.getStringExtra("originalText") ?: "No text available"
//
//        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        val ans_back_btn: ImageButton = findViewById(R.id.ans_back_btn)
//        val copy_btn: Button = findViewById(R.id.copy_btn)
//        val result_view: EditText = findViewById(R.id.result_view)
//        val saveButton: Button = findViewById(R.id.save_btn)
//        val share_btn: Button = findViewById(R.id.share_btn)
//        val summary_btn: Button = findViewById(R.id.summary_btn)
//        val translate_btn: ImageButton = findViewById(R.id.btn_to_translate)
//
//        // Set the original text
//        result_view.setText(originalText)
//
//        // Apply Linkify to make URLs clickable
//        Linkify.addLinks(result_view, Linkify.WEB_URLS)
//
//        summary_btn.setOnClickListener {
//            val intent = Intent(this, HomeScreenActivity::class.java).apply {
//                putExtra("Extracted_text", originalText)
//            }
//            startActivity(intent)
//        }
//
//        share_btn.setOnClickListener {
//            val sendIntent: Intent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, originalText)
//                type = "text/plain"
//            }
//
//            val shareIntent = Intent.createChooser(sendIntent, "Share Text")
//            startActivity(shareIntent)
//        }
//
//        saveButton.setOnClickListener {
//            val saveExtractedTextActivity = TextExtractionSavedActivity(originalText)
//            saveExtractedTextActivity.show(
//                supportFragmentManager,
//                TextExtractionSavedActivity::class.java.simpleName
//            )
//        }
//
//        ans_back_btn.setOnClickListener {
//            finish() // Better than starting a new activity
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        }
//
//        copy_btn.setOnClickListener {
//            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("originalText", originalText)
//            clipboardManager.setPrimaryClip(clipData)
//
//            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
//        }
//
//        translate_btn.setOnClickListener {
//            translateText(originalText)
//        }
//
//        bottom_navigation.selectedItemId = R.id.page_2
//
//        // Using setOnItemSelectedListener instead of deprecated setOnNavigationItemSelectedListener
//        bottom_navigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_2 -> true
//
//                R.id.page_3 -> {
//                    startActivity(Intent(this, SavedScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_4 -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    private fun translateText(originalText: String) {
//        val options = TranslatorOptions.Builder()
//            .setSourceLanguage(TranslateLanguage.ENGLISH)
//            .setTargetLanguage(TranslateLanguage.BENGALI)
//            .build()
//        translator = com.google.mlkit.nl.translate.Translation.getClient(options)
//
//        translator.downloadModelIfNeeded()
//            .addOnSuccessListener {
//                translator.translate(originalText)
//                    .addOnSuccessListener { translatedText ->
//                        val intent = Intent(this, TranslateActivity::class.java).apply {
//                            putExtra("originalText", originalText)
//                            putExtra("translatedText", translatedText)
//                        }
//                        startActivity(intent)
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(this, "Translation failed: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Model download failed: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//    }
//}


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
            finish() // Better than starting a new activity
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        copy_btn.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("originalText", originalText)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        bottom_navigation.selectedItemId = R.id.page_2

        // Using setOnItemSelectedListener instead of deprecated setOnNavigationItemSelectedListener
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

//    override fun onDestroy() {
//        super.onDestroy()
//        translator.close()
//    }
}


//package com.example.Text_Summarizer.modules
//
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.net.wifi.WifiConfiguration
//import android.net.wifi.WifiManager
//import android.os.Bundle
//import android.text.util.Linkify
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.example.Text_Summarizer.R
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class TextExtractionResultActivity : AppCompatActivity() {
//
//    private lateinit var connectWifiButton: Button
//    private var wifiSSID: String? = null
//    private var wifiPassword: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.text_extraction_result)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//
//        // Get the original text from the intent
//        val originalText = intent.getStringExtra("originalText") ?: "No text available"
//
//        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        val ans_back_btn: ImageButton = findViewById(R.id.ans_back_btn)
//        val copy_btn: Button = findViewById(R.id.copy_btn)
//        val result_view: EditText = findViewById(R.id.result_view)
//        val saveButton: Button = findViewById(R.id.save_btn)
//        val share_btn: Button = findViewById(R.id.share_btn)
//        connectWifiButton = findViewById(R.id.connect_wifi_button)
//
//        // Set the original text
//        result_view.setText(originalText)
//
//        // Apply Linkify to make URLs clickable
//        Linkify.addLinks(result_view, Linkify.WEB_URLS)
//
//        // Check for WiFi credentials in the original text
//        if (originalText.contains("SSID:") && originalText.contains("Password:")) {
//            val ssidStart = originalText.indexOf("SSID:") + 5
//            val ssidEnd = originalText.indexOf("\n", ssidStart)
//            wifiSSID = originalText.substring(ssidStart, ssidEnd).trim()
//
//            val passwordStart = originalText.indexOf("Password:") + 9
//            val passwordEnd = originalText.indexOf("\n", passwordStart)
//            wifiPassword = originalText.substring(passwordStart, passwordEnd).trim()
//
//            connectWifiButton.visibility = Button.VISIBLE
//        }
//
//        connectWifiButton.setOnClickListener {
//            connectToWifi()
//        }
//
//        share_btn.setOnClickListener {
//            val sendIntent: Intent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, originalText)
//                type = "text/plain"
//            }
//
//            val shareIntent = Intent.createChooser(sendIntent, "Share Text")
//            startActivity(shareIntent)
//        }
//
//        saveButton.setOnClickListener {
//            val saveExtractedTextActivity = TextExtractionSavedActivity(originalText)
//            saveExtractedTextActivity.show(
//                supportFragmentManager,
//                TextExtractionSavedActivity::class.java.simpleName
//            )
//        }
//
//        ans_back_btn.setOnClickListener {
//            finish() // Better than starting a new activity
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        }
//
//        copy_btn.setOnClickListener {
//            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("originalText", originalText)
//            clipboardManager.setPrimaryClip(clipData)
//
//            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
//        }
//
//        bottom_navigation.selectedItemId = R.id.page_2
//
//        // Using setOnItemSelectedListener instead of deprecated setOnNavigationItemSelectedListener
//        bottom_navigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_2 -> true
//
//                R.id.page_3 -> {
//                    startActivity(Intent(this, SavedScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_4 -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    private fun connectToWifi() {
//        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val wifiConfig = WifiConfiguration().apply {
//            SSID = String.format("\"%s\"", wifiSSID)
//            preSharedKey = String.format("\"%s\"", wifiPassword)
//        }
//
//        val netId = wifiManager.addNetwork(wifiConfig)
//        wifiManager.disconnect()
//        wifiManager.enableNetwork(netId, true)
//        wifiManager.reconnect()
//
//        Toast.makeText(this, "Connecting to WiFi...", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//    }
//}


//package com.example.Text_Summarizer.modules
//
//import android.Manifest
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.wifi.WifiConfiguration
//import android.net.wifi.WifiManager
//import android.os.Bundle
//import android.text.util.Linkify
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.Text_Summarizer.R
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class TextExtractionResultActivity : AppCompatActivity() {
//
//    private lateinit var connectWifiButton: Button
//    private var wifiSSID: String? = null
//    private var wifiPassword: String? = null
//    private val CHANGE_WIFI_STATE_REQUEST_CODE = 1
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.text_extraction_result)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//
//        // Get the original text from the intent
//        val originalText = intent.getStringExtra("originalText") ?: "No text available"
//
//        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        val ans_back_btn: ImageButton = findViewById(R.id.ans_back_btn)
//        val copy_btn: Button = findViewById(R.id.copy_btn)
//        val result_view: EditText = findViewById(R.id.result_view)
//        val saveButton: Button = findViewById(R.id.save_btn)
//        val share_btn: Button = findViewById(R.id.share_btn)
//        connectWifiButton = findViewById(R.id.connect_wifi_button)
//
//        // Set the original text
//        result_view.setText(originalText)
//
//        // Apply Linkify to make URLs clickable
//        Linkify.addLinks(result_view, Linkify.WEB_URLS)
//
//        // Check for WiFi credentials in the original text
//        if (originalText.contains("SSID:") && originalText.contains("Password:")) {
//            val ssidStart = originalText.indexOf("SSID:") + 5
//            val ssidEnd = originalText.indexOf("\n", ssidStart)
//            wifiSSID = originalText.substring(ssidStart, ssidEnd).trim()
//
//            val passwordStart = originalText.indexOf("Password:") + 9
//            val passwordEnd = originalText.indexOf("\n", passwordStart)
//            wifiPassword = originalText.substring(passwordStart, passwordEnd).trim()
//
//            connectWifiButton.visibility = Button.VISIBLE
//        }
//
//        connectWifiButton.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CHANGE_WIFI_STATE), CHANGE_WIFI_STATE_REQUEST_CODE)
//            } else {
//                connectToWifi()
//            }
//        }
//
//        share_btn.setOnClickListener {
//            val sendIntent: Intent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, originalText)
//                type = "text/plain"
//            }
//
//            val shareIntent = Intent.createChooser(sendIntent, "Share Text")
//            startActivity(shareIntent)
//        }
//
//        saveButton.setOnClickListener {
//            val saveExtractedTextActivity = TextExtractionSavedActivity(originalText)
//            saveExtractedTextActivity.show(
//                supportFragmentManager,
//                TextExtractionSavedActivity::class.java.simpleName
//            )
//        }
//
//        ans_back_btn.setOnClickListener {
//            finish() // Better than starting a new activity
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        }
//
//        copy_btn.setOnClickListener {
//            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("originalText", originalText)
//            clipboardManager.setPrimaryClip(clipData)
//
//            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
//        }
//
//        bottom_navigation.selectedItemId = R.id.page_2
//
//        // Using setOnItemSelectedListener instead of deprecated setOnNavigationItemSelectedListener
//        bottom_navigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_2 -> true
//
//                R.id.page_3 -> {
//                    startActivity(Intent(this, SavedScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_4 -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CHANGE_WIFI_STATE_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                connectToWifi()
//            } else {
//                Toast.makeText(this, "Permission denied to change WiFi state", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun connectToWifi() {
//        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val wifiConfig = WifiConfiguration().apply {
//            SSID = String.format("\"%s\"", wifiSSID)
//            preSharedKey = String.format("\"%s\"", wifiPassword)
//        }
//
//        val netId = wifiManager.addNetwork(wifiConfig)
//        wifiManager.disconnect()
//        wifiManager.enableNetwork(netId, true)
//        wifiManager.reconnect()
//
//        Toast.makeText(this, "Connecting to WiFi...", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//    }
//}
//


//this section of code tried to connect to wifi automatically if the text contains wifi credentials but it was not working
//package com.example.Text_Summarizer.modules
//
//import android.Manifest
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.wifi.WifiConfiguration
//import android.net.wifi.WifiManager
//import android.os.Bundle
//import android.provider.Settings
//import android.text.util.Linkify
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.Text_Summarizer.R
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class TextExtractionResultActivity : AppCompatActivity() {
//
//    private lateinit var connectWifiButton: Button
//    private var wifiSSID: String? = null
//    private var wifiPassword: String? = null
//    private val CHANGE_WIFI_STATE_REQUEST_CODE = 1
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.text_extraction_result)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//
//        // Get the original text from the intent
//        val originalText = intent.getStringExtra("originalText") ?: "No text available"
//
//        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        val ansBackBtn: ImageButton = findViewById(R.id.ans_back_btn)
//        val copyBtn: Button = findViewById(R.id.copy_btn)
//        val resultView: EditText = findViewById(R.id.result_view)
//        val saveButton: Button = findViewById(R.id.save_btn)
//        val shareBtn: Button = findViewById(R.id.share_btn)
//        connectWifiButton = findViewById(R.id.connect_wifi_button)
//
//        // Set the original text
//        resultView.setText(originalText)
//
//        // Apply Linkify to make URLs clickable
//        Linkify.addLinks(resultView, Linkify.WEB_URLS)
//
//        // Check for WiFi credentials in the original text
//        if (originalText.contains("SSID:") && originalText.contains("Password:")) {
//            val ssidStart = originalText.indexOf("SSID:") + 5
//            val ssidEnd = originalText.indexOf("\n", ssidStart)
//            wifiSSID = originalText.substring(ssidStart, ssidEnd).trim()
//
//            val passwordStart = originalText.indexOf("Password:") + 9
//            val passwordEnd = originalText.indexOf("\n", passwordStart)
//            wifiPassword = originalText.substring(passwordStart, passwordEnd).trim()
//
//            connectWifiButton.visibility = Button.VISIBLE
//        }
//
//        connectWifiButton.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CHANGE_WIFI_STATE), CHANGE_WIFI_STATE_REQUEST_CODE)
//            } else {
//                connectToWifi()
//            }
//        }
//
//        shareBtn.setOnClickListener {
//            val sendIntent: Intent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, originalText)
//                type = "text/plain"
//            }
//
//            val shareIntent = Intent.createChooser(sendIntent, "Share Text")
//            startActivity(shareIntent)
//        }
//
//        saveButton.setOnClickListener {
//            val saveExtractedTextActivity = TextExtractionSavedActivity(originalText)
//            saveExtractedTextActivity.show(
//                supportFragmentManager,
//                TextExtractionSavedActivity::class.java.simpleName
//            )
//        }
//
//        ansBackBtn.setOnClickListener {
//            finish() // Better than starting a new activity
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        }
//
//        copyBtn.setOnClickListener {
//            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("originalText", originalText)
//            clipboardManager.setPrimaryClip(clipData)
//
//            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
//        }
//
//        bottomNavigation.selectedItemId = R.id.page_2
//
//        // Using setOnItemSelectedListener instead of deprecated setOnNavigationItemSelectedListener
//        bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_2 -> true
//
//                R.id.page_3 -> {
//                    startActivity(Intent(this, SavedScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_4 -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CHANGE_WIFI_STATE_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                connectToWifi()
//            } else {
//                Toast.makeText(this, "Permission denied to change WiFi state", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun connectToWifi() {
//        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//
//        if (!wifiManager.isWifiEnabled) {
//            wifiManager.isWifiEnabled = true
//            Toast.makeText(this, "WiFi is turned on. Please connect to the desired network from WiFi settings.", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
//            return
//        }
//
//        val wifiConfig = WifiConfiguration().apply {
//            SSID = String.format("\"%s\"", wifiSSID)
//            preSharedKey = String.format("\"%s\"", wifiPassword)
//        }
//
//        val netId = wifiManager.addNetwork(wifiConfig)
//        wifiManager.disconnect()
//        wifiManager.enableNetwork(netId, true)
//        wifiManager.reconnect()
//
//        Toast.makeText(this, "Connecting to WiFi...", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//    }
//}