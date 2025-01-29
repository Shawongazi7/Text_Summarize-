package com.example.Text_Summarizer.modules

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.Text_Summarizer.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.Slider
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.GenerativeModelFutures

import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.TextPart
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.text.SimpleDateFormat
import java.util.*

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.ai.client.generativeai.type.content

import androidx.activity.viewModels
import com.example.Text_Summarizer.services.TextViewModel

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var summey_btn: Button
    private lateinit var editText: EditText
    private lateinit var summaryLengthLabel: TextView
    private lateinit var summerySizeSlider: Slider
    private var isSummarizing = false
    private lateinit var generativeModel: GenerativeModel
    private var summaryLength = "Short" // Default summary length
    private val textViewModel: TextViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure window to handle IME properly
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_home_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        // Initialize Gemini model
        val apiKey = getString(R.string.api_key)
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )

        summerySizeSlider = findViewById(R.id.summery_size)
        summaryLengthLabel = findViewById(R.id.summary_length_label)
        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val greeting: TextView = findViewById(R.id.greeting)
        val date: TextView = findViewById(R.id.date)
        val time_img: ImageView = findViewById(R.id.time_img)
        summey_btn = findViewById(R.id.summey_btn)
        editText = findViewById(R.id.editTextTextMultiLine)
        progressBar = findViewById(R.id.progressBar)

        setupGreeting(greeting, date, time_img)
        setupInputHandling()
        setupBottomNavigation(bottom_navigation)
        setupSlider()

        summey_btn.setOnClickListener {
            hideKeyboard()
            val inputText = editText.text.toString().trim()

            if (inputText.isEmpty()) {
                Toast.makeText(this, "Please enter text to summarize", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val wordCount = inputText.split("\\s+".toRegex()).size
            if (wordCount > 1000) {
                Toast.makeText(this, "Input text exceeds 1000 words limit", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            summarizeText(inputText)
        }

        bottom_navigation.selectedItemId = R.id.page_1
    }

    private fun setupSlider() {
        summerySizeSlider.addOnChangeListener { slider, value, fromUser ->
            summaryLength = when (value.toInt()) {
                1 -> "short"
                2 -> "moderate"
                3 -> "long"
                else -> "moderate"
            }
            summaryLengthLabel.text = "Summary Length: ${summaryLength.capitalize()}"
        }
    }

    private fun summarizeText(inputText: String) {
        if (isSummarizing) return
        hideKeyboard()

        if (inputText.isEmpty()) {
            handleApiError("Please enter text to summarize")
            return
        }
        if (inputText.length > 5000) {
            handleApiError("Input too long. Maximum input length is 5000 characters.")
            return
        }
        if (!isNetworkAvailable()) {
            handleApiError("Please turn on internet")
            return
        }

        val inputLanguage = detectLanguage(inputText)
        var targetLanguage = "en"

        if (inputLanguage == "bn") {
            targetLanguage = "bn"
        }

        isSummarizing = true
        summey_btn.isEnabled = false
        progressBar.visibility = ProgressBar.VISIBLE

        val generationConfig = GenerationConfig.Builder()
            .build()

        val model = GenerativeModelFutures.from(
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = getString(R.string.api_key),
                generationConfig = generationConfig
            )
        )
        val maxWords = when (summaryLength) {
            "short" -> 20
            "moderate" -> 70
            "long" -> 100
            else -> 70
        }

        val prompt = content {
            text("Please summarize the following text concisely in $targetLanguage with a $summaryLength length (maximum $maxWords words): $inputText")
        }

        val response = model.generateContent(prompt)

        Futures.addCallback(
            response,
            object : FutureCallback<GenerateContentResponse> {
                override fun onSuccess(result: GenerateContentResponse) {
                    runOnUiThread {
                        resetUI()
                        val summary = result.candidates.firstOrNull()?.content?.parts
                            ?.filterIsInstance<TextPart>()
                            ?.joinToString("") { it.text }
                            ?: "No summary generated"


                        val intent =
                            Intent(this@HomeScreenActivity, ResultScreenActivity::class.java)
                        intent.putExtra("summary", summary)
                        intent.putExtra("originalText", inputText)
                        startActivity(intent)
                    }
                }

                override fun onFailure(t: Throwable) {
                    runOnUiThread {
                        handleApiError("Error: ${t.message}")
                        resetUI()
                    }
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun detectLanguage(text: String): String {
        if (text.contains("[\\u0980-\\u09FF]+".toRegex())) {
            return "bn"
        }
        return "en"
    }

    private fun resetUI() {
        isSummarizing = false
        summey_btn.isEnabled = true
        progressBar.visibility = ProgressBar.GONE
    }

    private fun handleApiError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setupInputHandling() {
        editText.setOnEditorActionListener { v, actionId, event ->
            hideKeyboard()
            true
        }
    }

    private fun hideKeyboard() {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.ime())
    }

    private fun setupGreeting(greeting: TextView, date: TextView, time_img: ImageView) {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

        val greetingMessage = when (currentHour) {
            in 6..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
        greeting.text = greetingMessage

        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        date.text = dateFormat.format(currentTime.time)

        val imageResId = when {
            currentHour in 4..9 -> R.drawable.morining_sun
            currentHour in 10..15 -> R.drawable.noon_sun
            currentHour in 16..17 -> R.drawable.evenining_sun
            else -> R.drawable.night_moon
        }
        time_img.setImageResource(imageResId)
    }

    private fun setupBottomNavigation(bottom_navigation: BottomNavigationView) {
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}