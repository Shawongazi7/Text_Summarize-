//////package com.example.Text_Summarizer.modules
//////
//////import android.Manifest
//////import android.app.AlertDialog
//////import android.content.Intent
//////import android.content.pm.PackageManager
//////import android.graphics.Bitmap
//////import android.net.ConnectivityManager
//////import android.net.Uri
//////import android.os.Bundle
//////import android.os.Environment
//////import android.provider.MediaStore
//////import android.util.Log
//////import android.view.View
//////import android.widget.ImageButton
//////import android.widget.LinearLayout
//////import android.widget.ProgressBar
//////import android.widget.Toast
//////import androidx.activity.result.contract.ActivityResultContracts
//////import androidx.appcompat.app.AppCompatActivity
//////import androidx.core.content.ContextCompat
//////import androidx.core.content.FileProvider
//////import com.example.Text_Summarizer.R
//////import com.google.ai.client.generativeai.GenerativeModel
//////import com.google.ai.client.generativeai.java.GenerativeModelFutures
//////import com.google.ai.client.generativeai.type.GenerateContentResponse
//////import com.google.ai.client.generativeai.type.content
//////import com.google.android.material.bottomnavigation.BottomNavigationView
//////import com.google.common.util.concurrent.FutureCallback
//////import com.google.common.util.concurrent.Futures
//////import com.google.mlkit.vision.barcode.BarcodeScannerOptions
//////import com.google.mlkit.vision.barcode.BarcodeScanning
//////import com.google.mlkit.vision.barcode.common.Barcode
//////import com.google.mlkit.vision.common.InputImage
//////import com.google.mlkit.vision.text.TextRecognition
//////import com.google.mlkit.vision.text.TextRecognizer
//////import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//////import java.io.File
//////import java.io.IOException
//////import java.text.SimpleDateFormat
//////import java.util.Date
//////import java.util.Objects
//////import java.util.concurrent.Executors
//////
//////class TextExtractionActivity : AppCompatActivity() {
//////
//////    private lateinit var pickImageButton: ImageButton
//////    private lateinit var textRecognizer: TextRecognizer
//////    private lateinit var generativeModel: GenerativeModel
//////    private lateinit var generativeModelFutures: GenerativeModelFutures
//////    private var photoUri: Uri? = null
//////    private lateinit var progressBar: ProgressBar
//////
//////    private val requestPermissionLauncher = registerForActivityResult(
//////        ActivityResultContracts.RequestMultiplePermissions()
//////    ) { permissions ->
//////        if (permissions.all { it.value }) {
//////            showImageOptionsDialog()
//////        } else {
//////            showToast("Permissions denied")
//////        }
//////    }
//////
//////    private val imageCaptureLauncher =
//////        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//////            if (success) {
//////                photoUri?.let { uri ->
//////                    handleImageUri(uri)
//////                }
//////            }
//////        }
//////
//////    private val imagePickerLauncher =
//////        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//////            uri?.let { handleImageUri(it) }
//////        }
//////
//////    override fun onCreate(savedInstanceState: Bundle?) {
//////        super.onCreate(savedInstanceState)
//////        setContentView(R.layout.activity_text_extraction_screen)
//////
//////        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//////        pickImageButton = findViewById(R.id.btn_to_pick_image)
//////        progressBar = findViewById(R.id.progressBar)
//////
//////        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//////
//////        // Initialize ML Kit Text Recognizer
//////        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//////
//////        // Initialize Generative AI Model
//////        val apiKey = getString(R.string.api_key)
//////        generativeModel = GenerativeModel(
//////            modelName = "gemini-1.5-flash",
//////            apiKey = apiKey
//////        )
//////        generativeModelFutures = GenerativeModelFutures.from(generativeModel)
//////
//////        bottomNavigation.selectedItemId = R.id.page_2
//////
//////        bottomNavigation.setOnItemSelectedListener { item ->
//////            when (item.itemId) {
//////                R.id.page_1 -> {
//////                    startActivity(Intent(this, HomeScreenActivity::class.java))
//////                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//////                    true
//////                }
//////
//////                R.id.page_2 -> true
//////                R.id.page_3 -> {
//////                    startActivity(Intent(this, SavedScreenActivity::class.java))
//////                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//////                    true
//////                }
//////
//////                R.id.page_4 -> {
//////                    startActivity(Intent(this, ProfileActivity::class.java))
//////                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//////                    true
//////                }
//////
//////                else -> false
//////            }
//////        }
//////
//////        pickImageButton.setOnClickListener {
//////            checkPermissionsAndShowDialog()
//////        }
//////    }
//////
//////    private fun showImageOptionsDialog() {
//////        val builder = AlertDialog.Builder(this)
//////        val inflater = layoutInflater
//////        val dialogView = inflater.inflate(R.layout.dialog_image_options, null)
//////        builder.setView(dialogView)
//////
//////        val dialog = builder.create()
//////        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//////
//////        val pickImageOption = dialogView.findViewById<LinearLayout>(R.id.pick_image_option)
//////        val captureImageOption = dialogView.findViewById<LinearLayout>(R.id.capture_image_option)
//////
//////        pickImageOption.setOnClickListener { v: View? ->
//////            dialog.dismiss()
//////            checkPermissionAndPickImage()
//////        }
//////
//////        captureImageOption.setOnClickListener { v: View? ->
//////            dialog.dismiss()
//////            checkPermissionAndCaptureImage()
//////        }
//////
//////        dialog.show()
//////    }
//////
//////    private fun checkPermissionsAndShowDialog() {
//////        val requiredPermissions = arrayOf(
//////            Manifest.permission.CAMERA,
//////            Manifest.permission.READ_EXTERNAL_STORAGE,
//////            Manifest.permission.WRITE_EXTERNAL_STORAGE
//////        )
//////
//////        val permissionsToRequest = requiredPermissions.filter {
//////            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//////        }
//////
//////        if (permissionsToRequest.isNotEmpty()) {
//////            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
//////        } else {
//////            showImageOptionsDialog()
//////        }
//////    }
//////
//////    private fun checkPermissionAndPickImage() {
//////        imagePickerLauncher.launch("image/*")
//////    }
//////
//////    private fun checkPermissionAndCaptureImage() {
//////        captureImage()
//////    }
//////
//////    private fun captureImage() {
//////        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//////        if (intent.resolveActivity(packageManager) != null) {
//////            val photoFile: File? = try {
//////                createImageFile()
//////            } catch (ex: IOException) {
//////                null
//////            }
//////            photoFile?.let {
//////                photoUri = FileProvider.getUriForFile(
//////                    this,
//////                    "com.example.Text_Summarizer.fileprovider",
//////                    it
//////                )
//////                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//////                imageCaptureLauncher.launch(photoUri)
//////            }
//////        }
//////    }
//////
//////    private fun createImageFile(): File {
//////        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//////        val imageFileName = "JPEG_${timeStamp}_"
//////        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//////        return File.createTempFile(imageFileName, ".jpg", storageDir)
//////    }
//////
//////    private fun handleImageUri(uri: Uri) {
//////        try {
//////            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//////            runOnUiThread {
//////                progressBar.visibility = ProgressBar.VISIBLE
//////            }
//////            processImage(bitmap) {
//////                runOnUiThread {
//////                    progressBar.visibility = ProgressBar.GONE
//////                }
//////            }
//////        } catch (e: Exception) {
//////            showToast("Failed to load image: ${e.message}")
//////            runOnUiThread {
//////                progressBar.visibility = ProgressBar.GONE
//////            }
//////        }
//////    }
//////
//////    private fun processImage(bitmap: Bitmap, onComplete: () -> Unit) {
//////        extractQRCode(bitmap) { qrCodeText ->
//////            if (isNetworkAvailable()) {
//////                extractTextWithGenerativeAI(bitmap) { generativeText ->
//////                    val combinedText =
//////                        if (qrCodeText.isNullOrEmpty()) generativeText else "QR-CODE:\n[$qrCodeText]\n\n$generativeText"
//////                    showExtractedText(combinedText)
//////                    onComplete()
//////                }
//////            } else {
//////                extractTextWithMLKit(bitmap) { mlKitText ->
//////                    val combinedText =
//////                        if (qrCodeText.isNullOrEmpty()) mlKitText else "QR-CODE:\n[$qrCodeText]\n\n$mlKitText"
//////                    showExtractedText(combinedText)
//////                    onComplete()
//////                }
//////            }
//////        }
//////    }
//////
//////    private fun extractQRCode(bitmap: Bitmap, callback: (String?) -> Unit) {
//////        val image = InputImage.fromBitmap(bitmap, 0)
//////        val options = BarcodeScannerOptions.Builder()
//////            .setBarcodeFormats(
//////                Barcode.FORMAT_ALL_FORMATS
//////            )
//////            .build()
//////        val scanner = BarcodeScanning.getClient(options)
//////
//////        scanner.process(image)
//////            .addOnSuccessListener { barcodes ->
//////                var qrCodeText: String? = null
//////                for (barcode in barcodes) {
//////                    val rawValue = barcode.rawValue
//////                    if (barcode.valueType == Barcode.TYPE_WIFI) {
//////                        val ssid = barcode.wifi!!.ssid
//////                        val password = barcode.wifi!!.password
//////                        val encryptionType = barcode.wifi!!.encryptionType
//////                        qrCodeText =
//////                            "WiFi SSID: $ssid \nPassword: $password \nEncryption: $encryptionType"
//////                    } else {
//////                        qrCodeText = rawValue
//////                    }
//////                    break
//////                }
//////                callback(qrCodeText)
//////            }
//////            .addOnFailureListener { e ->
//////                Log.e("QRCodeExtraction", "QR code extraction failed", e)
//////                callback(null)
//////            }
//////    }
//////
//////    private fun extractTextWithMLKit(bitmap: Bitmap, callback: (String) -> Unit) {
//////        val image = InputImage.fromBitmap(bitmap, 0)
//////        textRecognizer.process(image)
//////            .addOnSuccessListener { visionText ->
//////                val extractedText = visionText.text
//////                callback(extractedText)
//////            }
//////            .addOnFailureListener { e ->
//////                showToast("Text extraction failed: ${e.message}")
//////                Log.e("TextExtraction", "Text extraction failed", e)
//////            }
//////    }
//////
//////    private fun extractTextWithGenerativeAI(bitmap: Bitmap, callback: (String) -> Unit) {
//////        val prompt = content {
//////            text("Extract only the text from this image, including any Bangla or handwritten text.")
//////            image(bitmap)
//////        }
//////
//////        val response = generativeModelFutures.generateContent(prompt)
//////        Futures.addCallback(
//////            response,
//////            object : FutureCallback<GenerateContentResponse> {
//////                override fun onSuccess(result: GenerateContentResponse) {
//////                    val extractedText = result.candidates.firstOrNull()?.content?.parts
//////                        ?.filterIsInstance<com.google.ai.client.generativeai.type.TextPart>()
//////                        ?.joinToString("") { it.text }
//////                        ?: "No text extracted"
//////                    callback(extractedText)
//////                }
//////
//////                override fun onFailure(t: Throwable) {
//////                    showToast("Error: ${t.message}")
//////                }
//////            },
//////            Executors.newSingleThreadExecutor()
//////        )
//////    }
//////
//////    private fun showExtractedText(text: String) {
//////        val intent = Intent(this, TextExtractionResultActivity::class.java)
//////        intent.putExtra("originalText", text)
//////        startActivity(intent)
//////    }
//////
//////    private fun isNetworkAvailable(): Boolean {
//////        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//////        val activeNetwork = connectivityManager.activeNetworkInfo
//////        return activeNetwork != null && activeNetwork.isConnected
//////    }
//////
//////    private fun showToast(message: String) {
//////        runOnUiThread {
//////            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//////        }
//////    }
//////
//////    override fun onBackPressed() {
//////        super.onBackPressed()
//////        val intent = Intent(this, HomeScreenActivity::class.java)
//////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//////        startActivity(intent)
//////        finish()
//////    }
//////}
//////
//////
//////
//////
//////
//////if the image need enhance image then use this code
//////
////package com.example.Text_Summarizer.modules
////
////import android.Manifest
////import android.app.AlertDialog
////import android.content.Intent
////import android.content.pm.PackageManager
////import android.graphics.Bitmap
////import android.graphics.ColorMatrix
////import android.graphics.ColorMatrixColorFilter
////import android.graphics.Paint
////import android.net.ConnectivityManager
////import android.net.Uri
////import android.os.Bundle
////import android.os.Environment
////import android.provider.MediaStore
////import android.util.Log
////import android.widget.ImageButton
////import android.widget.ProgressBar
////import android.widget.Toast
////import androidx.activity.result.contract.ActivityResultContracts
////import androidx.appcompat.app.AppCompatActivity
////import androidx.core.content.ContextCompat
////import androidx.core.content.FileProvider
////import com.example.Text_Summarizer.R
////import com.google.android.material.bottomnavigation.BottomNavigationView
////import com.google.mlkit.vision.common.InputImage
////import com.google.mlkit.vision.text.TextRecognition
////import com.google.mlkit.vision.text.TextRecognizer
////import com.google.mlkit.vision.text.latin.TextRecognizerOptions
////import com.google.ai.client.generativeai.GenerativeModel
////import com.google.ai.client.generativeai.java.GenerativeModelFutures
////import com.google.ai.client.generativeai.type.GenerateContentResponse
////import com.google.ai.client.generativeai.type.content
////import com.google.common.util.concurrent.FutureCallback
////import com.google.common.util.concurrent.Futures
////import com.google.mlkit.vision.barcode.BarcodeScannerOptions
////import com.google.mlkit.vision.barcode.BarcodeScanning
////import com.google.mlkit.vision.barcode.common.Barcode
////import java.io.File
////import java.io.IOException
////import java.text.SimpleDateFormat
////import java.util.Date
////import java.util.concurrent.Executors
////
////class TextExtractionActivity : AppCompatActivity() {
////
////    private lateinit var pickImageButton: ImageButton
////    private lateinit var textRecognizer: TextRecognizer
////    private lateinit var generativeModel: GenerativeModel
////    private lateinit var generativeModelFutures: GenerativeModelFutures
////    private var photoUri: Uri? = null
////    private lateinit var progressBar: ProgressBar
////
////    private val requestPermissionLauncher = registerForActivityResult(
////        ActivityResultContracts.RequestMultiplePermissions()
////    ) { permissions ->
////        if (permissions.all { it.value }) {
////            showImageOptionsDialog()
////        } else {
////            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
////        }
////    }
////
////    private val imageCaptureLauncher =
////        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
////            if (success) {
////                photoUri?.let { uri ->
////                    handleImageUri(uri, File(uri.path!!)) // Ensure the file path is correct
////                }
////            }
////        }
////
////    private val imagePickerLauncher =
////        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
////            uri?.let { handleImageUri(it, File(it.path!!)) } // Ensure the file path is correct
////        }
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_text_extraction_screen)
////
////        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
////        pickImageButton = findViewById(R.id.btn_to_pick_image)
////        progressBar = findViewById(R.id.progressBar)
////        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
////
////        // Initialize ML Kit Text Recognizer
////        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
////
////        // Initialize Generative AI Model
////        val apiKey = getString(R.string.api_key)
////        generativeModel = GenerativeModel(
//////            modelName = "gemini-1.5-flash",
////            modelName = "gemini-1.5-pro",
////            apiKey = apiKey
////        )
////        generativeModelFutures = GenerativeModelFutures.from(generativeModel)
////
////        bottomNavigation.selectedItemId = R.id.page_2
////
////        bottomNavigation.setOnItemSelectedListener { item ->
////            when (item.itemId) {
////                R.id.page_1 -> {
////                    startActivity(Intent(this, HomeScreenActivity::class.java))
////                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
////                    true
////                }
////
////                R.id.page_2 -> true
////                R.id.page_3 -> {
////                    startActivity(Intent(this, SavedScreenActivity::class.java))
////                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
////                    true
////                }
////
////                R.id.page_4 -> {
////                    startActivity(Intent(this, ProfileActivity::class.java))
////                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
////                    true
////                }
////
////                else -> false
////            }
////        }
////
////        pickImageButton.setOnClickListener {
////            checkPermissionsAndShowDialog()
////        }
////    }
////
////    private fun checkPermissionsAndShowDialog() {
////        val requiredPermissions = arrayOf(
////            Manifest.permission.CAMERA,
////            Manifest.permission.READ_EXTERNAL_STORAGE,
////            Manifest.permission.WRITE_EXTERNAL_STORAGE
////        )
////
////        val permissionsToRequest = requiredPermissions.filter {
////            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
////        }
////
////        if (permissionsToRequest.isNotEmpty()) {
////            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
////        } else {
////            showImageOptionsDialog()
////        }
////    }
////
////    private fun showImageOptionsDialog() {
////        val options = arrayOf("Take Photo", "Choose from Gallery")
////        AlertDialog.Builder(this)
////            .setTitle("Select Image")
////            .setItems(options) { _, which ->
////                when (which) {
////                    0 -> captureImage()
////                    1 -> imagePickerLauncher.launch("image/*")
////                }
////            }
////            .show()
////    }
////
////    private fun captureImage() {
////        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////        if (intent.resolveActivity(packageManager) != null) {
////            val photoFile: File? = try {
////                createImageFile()
////            } catch (ex: IOException) {
////                null
////            }
////            photoFile?.let {
////                photoUri = FileProvider.getUriForFile(
////                    this,
////                    "com.example.Text_Summarizer.fileprovider",
////                    it
////                )
////                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
////                imageCaptureLauncher.launch(photoUri)
////            }
////        }
////    }
////
////    private fun createImageFile(): File {
////        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
////        val imageFileName = "JPEG_${timeStamp}_"
////        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
////        return File.createTempFile(imageFileName, ".jpg", storageDir)
////    }
////
////    private fun handleImageUri(uri: Uri, imageFile: File) {
////        try {
////            Log.d("ImagePath", "Image file path: ${imageFile.absolutePath}")
////            Log.d("ImageExists", "Image file exists: ${imageFile.exists()}")
////
////            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
////            val enhancedBitmap = enhanceImage(bitmap)
////            runOnUiThread {
////                progressBar.visibility = ProgressBar.VISIBLE
////            }
////            processImage(enhancedBitmap) {
////                runOnUiThread {
////                    // Delete the image file after processing
////                    if (imageFile.exists()) {
////                        val deleted = imageFile.delete()
////                        if (deleted) {
////                            Log.d("ImageDeletion", "Image file deleted successfully")
////                        } else {
////                            Log.e("ImageDeletion", "Failed to delete image file")
////                        }
////                    } else {
////                        Log.e("ImageDeletion", "Image file does not exist")
////                    }
////                    progressBar.visibility = ProgressBar.GONE
////                }
////            }
////        } catch (e: Exception) {
////            runOnUiThread {
////                Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT)
////                    .show()
////                progressBar.visibility = ProgressBar.GONE
////            }
////        }
////    }
////
////    private fun enhanceImage(bitmap: Bitmap): Bitmap {
////        val enhancedBitmap = Bitmap.createBitmap(
////            bitmap.width,
////            bitmap.height,
////            bitmap.config ?: Bitmap.Config.ARGB_8888
////        )
////
////        val canvas = android.graphics.Canvas(enhancedBitmap)
////        val paint = Paint()
////
////        val colorMatrix = ColorMatrix()
////        colorMatrix.setSaturation(0f) // Convert to grayscale
////        val contrast = 1.5f // Increase contrast
////        val scale = contrast + 1
////        val translate = (-0.5f * scale + 0.5f) * 255
////        val contrastMatrix = floatArrayOf(
////            scale, 0f, 0f, 0f, translate,
////            0f, scale, 0f, 0f, translate,
////            0f, 0f, scale, 0f, translate,
////            0f, 0f, 0f, 1f, 0f
////        )
////        colorMatrix.postConcat(ColorMatrix(contrastMatrix))
////
////        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
////        canvas.drawBitmap(bitmap, 0f, 0f, paint)
////
////        return enhancedBitmap
////    }
////
////    private fun processImage(bitmap: Bitmap, onComplete: () -> Unit) {
////        extractQRCode(bitmap) { qrCodeText ->
////            if (isNetworkAvailable()) {
////                extractTextWithGenerativeAI(bitmap) { generativeText ->
////                    val combinedText =
////                        if (qrCodeText.isNullOrEmpty()) generativeText else "QR-CODE:\n[$qrCodeText]\n$generativeText"
////                    showExtractedText(combinedText)
////                    onComplete() // Ensure this is called
////                }
////            } else {
////                extractTextWithMLKit(bitmap) { mlKitText ->
////                    val combinedText =
////                        if (qrCodeText.isNullOrEmpty()) mlKitText else "QR-CODE:\n[$qrCodeText]\n$mlKitText"
////                    showExtractedText(combinedText)
////                    onComplete() // Ensure this is called
////                }
////            }
////        }
////    }
////
////    private fun extractQRCode(bitmap: Bitmap, callback: (String?) -> Unit) {
////        val image = InputImage.fromBitmap(bitmap, 0)
////        val options = BarcodeScannerOptions.Builder()
////            .setBarcodeFormats(
////                Barcode.FORMAT_ALL_FORMATS
////            )
////            .build()
////        val scanner = BarcodeScanning.getClient(options)
////
////        scanner.process(image)
////            .addOnSuccessListener { barcodes ->
////                var qrCodeText: String? = null
////                for (barcode in barcodes) {
////                    val rawValue = barcode.rawValue
////                    if (barcode.valueType == Barcode.TYPE_WIFI) {
////                        val ssid = barcode.wifi!!.ssid
////                        val password = barcode.wifi!!.password
////                        val encryptionType = barcode.wifi!!.encryptionType
////                        qrCodeText =
////                            "WiFi SSID: $ssid \nPassword: $password \nEncryption: $encryptionType"
////                    } else {
////                        qrCodeText = rawValue
////                    }
////                    break
////                }
////                callback(qrCodeText)
////            }
////            .addOnFailureListener { e ->
////                Log.e("QRCodeExtraction", "QR code extraction failed", e)
////                callback(null)
////            }
////    }
////
////    private fun extractTextWithMLKit(bitmap: Bitmap, callback: (String) -> Unit) {
////        val image = InputImage.fromBitmap(bitmap, 0)
////        textRecognizer.process(image)
////            .addOnSuccessListener { visionText ->
////                val extractedText = visionText.text
////                callback(extractedText)
////            }
////            .addOnFailureListener { e ->
////                Toast.makeText(this, "Text extraction failed: ${e.message}", Toast.LENGTH_SHORT)
////                    .show()
////                Log.e("TextExtraction", "Text extraction failed", e)
////            }
////    }
////
////    private fun extractTextWithGenerativeAI(bitmap: Bitmap, callback: (String) -> Unit) {
////        val prompt = content {
////            text("Extract only the text from this image, including any Bangla or handwritten text.")
////            image(bitmap)
////        }
////
////        val response = generativeModelFutures.generateContent(prompt)
////        Futures.addCallback(
////            response,
////            object : FutureCallback<GenerateContentResponse> {
////                override fun onSuccess(result: GenerateContentResponse) {
////                    val extractedText = result.candidates.firstOrNull()?.content?.parts
////                        ?.filterIsInstance<com.google.ai.client.generativeai.type.TextPart>()
////                        ?.joinToString("") { it.text }
////                        ?: "No text extracted"
////                    callback(extractedText)
////                }
////
////                override fun onFailure(t: Throwable) {
////                    runOnUiThread {
////                        Toast.makeText(
////                            this@TextExtractionActivity,
////                            "Error: ${t.message}",
////                            Toast.LENGTH_SHORT
////                        ).show()
////                    }
////                }
////            },
////            Executors.newSingleThreadExecutor()
////        )
////    }
////
////    private fun showExtractedText(text: String) {
////        val intent = Intent(this, TextExtractionResultActivity::class.java)
////        intent.putExtra("originalText", text)
////        startActivity(intent)
////    }
////
////    private fun isNetworkAvailable(): Boolean {
////        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
////        val activeNetwork = connectivityManager.activeNetworkInfo
////        return activeNetwork != null && activeNetwork.isConnected
////    }
////
////    override fun onBackPressed() {
////        super.onBackPressed()
////        val intent = Intent(this, HomeScreenActivity::class.java)
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
////        startActivity(intent)
////        finish()
////    }
////}
////
////
//
//
//
//
//
//
////with compressed image for fasten the process of extracting text using generative ai
////the saved file will be saved in externally in the pictures folder
//
//package com.example.Text_Summarizer.modules
//
//import android.Manifest
//import android.app.AlertDialog
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.ColorMatrix
//import android.graphics.ColorMatrixColorFilter
//import android.graphics.Paint
//import android.net.ConnectivityManager
//import android.net.Uri
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.util.Log
//import android.view.LayoutInflater
//import android.widget.ImageButton
//import android.widget.LinearLayout
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import com.example.Text_Summarizer.R
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.TextRecognizer
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//import com.google.ai.client.generativeai.GenerativeModel
//import com.google.ai.client.generativeai.java.GenerativeModelFutures
//import com.google.ai.client.generativeai.type.GenerateContentResponse
//import com.google.ai.client.generativeai.type.content
//import com.google.common.util.concurrent.FutureCallback
//import com.google.common.util.concurrent.Futures
//import com.google.mlkit.vision.barcode.BarcodeScannerOptions
//import com.google.mlkit.vision.barcode.BarcodeScanning
//import com.google.mlkit.vision.barcode.common.Barcode
//import java.io.File
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.concurrent.Executors
//
//class TextExtractionActivity : AppCompatActivity() {
//
//    private lateinit var pickImageButton: ImageButton
//    private lateinit var textRecognizer: TextRecognizer
//    private lateinit var generativeModel: GenerativeModel
//    private lateinit var generativeModelFutures: GenerativeModelFutures
//    private var photoUri: Uri? = null
//    private lateinit var progressBar: ProgressBar
//    private val cache = mutableMapOf<String, String>()
//    private var storageDir: File? = null // Directory to store images
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        if (permissions.all { it.value }) {
//            showImageOptionsDialog()
//        } else {
//            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private val imageCaptureLauncher =
//        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//            if (success) {
//                photoUri?.let { uri ->
//                    handleImageUri(uri)
//                }
//            }
//        }
//
//    private val imagePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            uri?.let { handleImageUri(it) }
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_text_extraction_screen)
//
//        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        pickImageButton = findViewById(R.id.btn_to_pick_image)
//        progressBar = findViewById(R.id.progressBar)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//
//        // Initialize ML Kit Text Recognizer
//        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//
//        // Initialize Generative AI Model
//        val apiKey = getString(R.string.api_key)
//        generativeModel = GenerativeModel(
//            modelName = "gemini-1.5-flash",
//            apiKey = apiKey
//        )
//        generativeModelFutures = GenerativeModelFutures.from(generativeModel)
//
//        bottomNavigation.selectedItemId = R.id.page_2
//
//        bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//
//                R.id.page_2 -> true
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
//
//        pickImageButton.setOnClickListener {
//            checkPermissionsAndShowDialog()
//        }
//    }
//
//    private fun checkPermissionsAndShowDialog() {
//        val requiredPermissions = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//
//        val permissionsToRequest = requiredPermissions.filter {
//            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//        }
//
//        if (permissionsToRequest.isNotEmpty()) {
//            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
//        } else {
//            showImageOptionsDialog()
//        }
//    }
//
//    private fun showImageOptionsDialog() {
//        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_options, null)
//
//        val dialog = AlertDialog.Builder(this)
//            .setView(dialogView)
//            .create()
//
//        val pickImageOption: LinearLayout = dialogView.findViewById(R.id.pick_image_option)
//        val captureImageOption: LinearLayout = dialogView.findViewById(R.id.capture_image_option)
//
//        pickImageOption.setOnClickListener {
//            imagePickerLauncher.launch("image/*")
//            dialog.dismiss()
//        }
//
//        captureImageOption.setOnClickListener {
//            captureImage()
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//
//    private fun captureImage() {
//        // Create directory when capturing an image
//        storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "text_summarizer")
//        if (!storageDir!!.exists()) {
//            if (!storageDir!!.mkdirs()) {
//                Log.e("CreateDir", "Failed to create text_summarizer directory")
//                return
//            }
//        }
//
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (intent.resolveActivity(packageManager) != null) {
//            val photoFile: File? = try {
//                createImageFile()
//            } catch (ex: IOException) {
//                null
//            }
//            photoFile?.let {
//                photoUri = FileProvider.getUriForFile(
//                    this,
//                    "com.example.Text_Summarizer.fileprovider",
//                    it
//                )
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//                imageCaptureLauncher.launch(photoUri)
//            }
//        }
//    }
//
//    private fun createImageFile(): File {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_${timeStamp}_"
//        return File.createTempFile(imageFileName, ".jpg", storageDir)
//    }
//
//    private fun handleImageUri(uri: Uri) {
//        try {
//            val imageFile = File(uri.path ?: "")
//            Log.d("ImagePath", "Image file path: ${imageFile.absolutePath}")
//            Log.d("ImageExists", "Image file exists: ${imageFile.exists()}")
//
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//            val compressedBitmap = compressImage(bitmap)
//            val enhancedBitmap = enhanceImage(compressedBitmap)
//            runOnUiThread {
//                progressBar.visibility = ProgressBar.VISIBLE
//            }
//            processImage(enhancedBitmap) {
//                runOnUiThread {
//                    // Delete the entire folder after processing
//                    if (storageDir != null && storageDir!!.exists()) {
//                        val deleted = storageDir!!.deleteRecursively()
//                        if (deleted) {
//                            Log.d("FolderDeletion", "text_summarizer directory deleted successfully")
//                        } else {
//                            Log.e("FolderDeletion", "Failed to delete text_summarizer directory")
//                        }
//                    } else {
//                        Log.e("FolderDeletion", "text_summarizer directory does not exist")
//                    }
//                    progressBar.visibility = ProgressBar.GONE
//                }
//            }
//        } catch (e: Exception) {
//            runOnUiThread {
//                Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT)
//                    .show()
//                progressBar.visibility = ProgressBar.GONE
//            }
//        }
//    }
//
//    private fun compressImage(bitmap: Bitmap): Bitmap {
//        val width = bitmap.width
//        val height = bitmap.height
//        val maxWidth = 2044 // New width
//        val maxHeight = 2044 // New height
//
//        if (width > height) {
//            if (width > maxWidth) {
//                val ratio = width.toFloat() / maxWidth.toFloat()
//                val newHeight = (height / ratio).toInt()
//                return Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true)
//            }
//        } else {
//            if (height > maxHeight) {
//                val ratio = height.toFloat() / maxHeight.toFloat()
//                val newWidth = (width / ratio).toInt()
//                return Bitmap.createScaledBitmap(bitmap, newWidth, maxHeight, true)
//            }
//        }
//        return bitmap
//    }
//
//    private fun enhanceImage(bitmap: Bitmap): Bitmap {
//        val enhancedBitmap = Bitmap.createBitmap(
//            bitmap.width,
//            bitmap.height,
//            bitmap.config ?: Bitmap.Config.ARGB_8888
//        )
//
//        val canvas = android.graphics.Canvas(enhancedBitmap)
//        val paint = Paint()
//
//        val colorMatrix = ColorMatrix()
//        colorMatrix.setSaturation(0f) // Convert to grayscale
//        val contrast = 1.5f // Increase contrast
//        val scale = contrast + 1
//        val translate = (-0.5f * scale + 0.5f) * 255
//        val contrastMatrix = floatArrayOf(
//            scale, 0f, 0f, 0f, translate,
//            0f, scale, 0f, 0f, translate,
//            0f, 0f, scale, 0f, translate,
//            0f, 0f, 0f, 1f, 0f
//        )
//        colorMatrix.postConcat(ColorMatrix(contrastMatrix))
//
//        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
//        canvas.drawBitmap(bitmap, 0f, 0f, paint)
//
//        return enhancedBitmap
//    }
//
//    private fun processImage(bitmap: Bitmap, onComplete: () -> Unit) {
//        val bitmapHash = bitmap.hashCode().toString()
//
//        if (cache.containsKey(bitmapHash)) {
//            showExtractedText(cache[bitmapHash]!!)
//            onComplete()
//            return
//        }
//
//        extractQRCode(bitmap) { qrCodeText ->
//            if (isNetworkAvailable()) {
//                extractTextWithGenerativeAI(bitmap) { generativeText ->
//                    val combinedText =
//                        if (qrCodeText.isNullOrEmpty()) generativeText else "QR-CODE:\n[$qrCodeText]\n$generativeText"
//                    cache[bitmapHash] = combinedText
//                    showExtractedText(combinedText)
//                    onComplete() // Ensure this is called
//                }
//            } else {
//                extractTextWithMLKit(bitmap) { mlKitText ->
//                    val combinedText =
//                        if (qrCodeText.isNullOrEmpty()) mlKitText else "QR-CODE:\n[$qrCodeText]\n$mlKitText"
//                    cache[bitmapHash] = combinedText
//                    showExtractedText(combinedText)
//                    onComplete() // Ensure this is called
//                }
//            }
//        }
//    }
//
//    private fun extractQRCode(bitmap: Bitmap, callback: (String?) -> Unit) {
//        val image = InputImage.fromBitmap(bitmap, 0)
//        val options = BarcodeScannerOptions.Builder()
//            .setBarcodeFormats(
//                Barcode.FORMAT_ALL_FORMATS
//            )
//            .build()
//        val scanner = BarcodeScanning.getClient(options)
//
//        scanner.process(image)
//            .addOnSuccessListener { barcodes ->
//                var qrCodeText: String? = null
//                for (barcode in barcodes) {
//                    val rawValue = barcode.rawValue
//                    if (barcode.valueType == Barcode.TYPE_WIFI) {
//                        val ssid = barcode.wifi!!.ssid
//                        val password = barcode.wifi!!.password
//                        val encryptionType = barcode.wifi!!.encryptionType
//                        qrCodeText =
//                            "WiFi SSID: $ssid \nPassword: $password \nEncryption: $encryptionType"
//                    } else {
//                        qrCodeText = rawValue
//                    }
//                    break
//                }
//                callback(qrCodeText)
//            }
//            .addOnFailureListener { e ->
//                Log.e("QRCodeExtraction", "QR code extraction failed", e)
//                callback(null)
//            }
//    }
//
//    private fun extractTextWithMLKit(bitmap: Bitmap, callback: (String) -> Unit) {
//        val image = InputImage.fromBitmap(bitmap, 0)
//        textRecognizer.process(image)
//            .addOnSuccessListener { visionText ->
//                val extractedText = visionText.text
//                callback(extractedText)
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Text extraction failed: ${e.message}", Toast.LENGTH_SHORT)
//                    .show()
//                Log.e("TextExtraction", "Text extraction failed", e)
//            }
//    }
//
//    private fun extractTextWithGenerativeAI(bitmap: Bitmap, callback: (String) -> Unit) {
//        val prompt = content {
//            text("Extract only the text from this image, including any Bangla or handwritten text.")
//            image(bitmap)
//        }
//
//        val response = generativeModelFutures.generateContent(prompt)
//        Futures.addCallback(
//            response,
//            object : FutureCallback<GenerateContentResponse> {
//                override fun onSuccess(result: GenerateContentResponse) {
//                    val extractedText = result.candidates.firstOrNull()?.content?.parts
//                        ?.filterIsInstance<com.google.ai.client.generativeai.type.TextPart>()
//                        ?.joinToString("") { it.text }
//                        ?: "No text extracted"
//                    callback(extractedText)
//                }
//
//                override fun onFailure(t: Throwable) {
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@TextExtractionActivity,
//                            "Error: ${t.message}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            },
//            Executors.newSingleThreadExecutor()
//        )
//    }
//
//    private fun showExtractedText(text: String) {
//        val intent = Intent(this, TextExtractionResultActivity::class.java)
//        intent.putExtra("originalText", text)
//        startActivity(intent)
//    }
//
//    private fun isNetworkAvailable(): Boolean {
//        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork = connectivityManager.activeNetworkInfo
//        return activeNetwork != null && activeNetwork.isConnected
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(this, HomeScreenActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish()
//    }
//}


//without image enhancement and image folder created and deleted after finishing the process

package com.example.Text_Summarizer.modules

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.Text_Summarizer.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors

class TextExtractionActivity : AppCompatActivity() {

    private lateinit var pickImageButton: ImageButton
    private lateinit var textRecognizer: TextRecognizer
    private lateinit var generativeModel: GenerativeModel
    private lateinit var generativeModelFutures: GenerativeModelFutures
    private var photoUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private var storageDir: File? = null // Directory to store images

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            showImageOptionsDialog()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri ->
                    handleImageUri(uri)
                }
            }
        }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleImageUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_extraction_screen)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        pickImageButton = findViewById(R.id.btn_to_pick_image)
        progressBar = findViewById(R.id.progressBar)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        // Initialize ML Kit Text Recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // Initialize Generative AI Model
        val apiKey = getString(R.string.api_key)
        generativeModel = GenerativeModel(
//            modelName = "gemini-1.5-flash",
            modelName = "gemini-2.0-flash-001",
            apiKey = apiKey
        )
        generativeModelFutures = GenerativeModelFutures.from(generativeModel)

        bottomNavigation.selectedItemId = R.id.page_2

        bottomNavigation.setOnItemSelectedListener { item ->
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

        pickImageButton.setOnClickListener {
            checkPermissionsAndShowDialog()
        }
    }

    private fun checkPermissionsAndShowDialog() {
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            showImageOptionsDialog()
        }
    }

    private fun showImageOptionsDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureImage()
                    1 -> imagePickerLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun captureImage() {
        // Create directory when capturing an image
        storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "text_summarizer"
        )
        if (!storageDir!!.exists()) {
            if (!storageDir!!.mkdirs()) {
                Log.e("CreateDir", "Failed to create text_summarizer directory")
                return
            }
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.let {
                photoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.Text_Summarizer.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                imageCaptureLauncher.launch(photoUri)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun handleImageUri(uri: Uri) {
        try {
            val imageFile = File(uri.path ?: "")
            Log.d("ImagePath", "Image file path: ${imageFile.absolutePath}")
            Log.d("ImageExists", "Image file exists: ${imageFile.exists()}")

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            runOnUiThread {
                progressBar.visibility = ProgressBar.VISIBLE
            }
            processImage(bitmap) {
                runOnUiThread {
                    // Delete the entire folder after processing
                    if (storageDir != null && storageDir!!.exists()) {
                        val deleted = storageDir!!.deleteRecursively()
                        if (deleted) {
                            Log.d(
                                "FolderDeletion",
                                "text_summarizer directory deleted successfully"
                            )
                        } else {
                            Log.e("FolderDeletion", "Failed to delete text_summarizer directory")
                        }
                    } else {
                        Log.e("FolderDeletion", "text_summarizer directory does not exist")
                    }
                    progressBar.visibility = ProgressBar.GONE
                }
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                progressBar.visibility = ProgressBar.GONE
            }
        }
    }

    private fun processImage(bitmap: Bitmap, onComplete: () -> Unit) {
        extractQRCode(bitmap) { qrCodeText ->
            if (isNetworkAvailable()) {
                extractTextWithGenerativeAI(bitmap) { generativeText ->
                    val combinedText =
                        if (qrCodeText.isNullOrEmpty()) generativeText else "QR-CODE:\n[$qrCodeText]\n$generativeText"
                    showExtractedText(combinedText)
                    onComplete() // Ensure this is called
                }
            } else {
                extractTextWithMLKit(bitmap) { mlKitText ->
                    val combinedText =
                        if (qrCodeText.isNullOrEmpty()) mlKitText else "QR-CODE:\n[$qrCodeText]\n$mlKitText"
                    showExtractedText(combinedText)
                    onComplete() // Ensure this is called
                }
            }
        }
    }

    private fun extractQRCode(bitmap: Bitmap, callback: (String?) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()
        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                var qrCodeText: String? = null
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    if (barcode.valueType == Barcode.TYPE_WIFI) {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        val encryptionType = barcode.wifi!!.encryptionType
                        qrCodeText =
                            "WiFi SSID: $ssid \nPassword: $password \nEncryption: $encryptionType"
                    } else {
                        qrCodeText = rawValue
                    }
                    break
                }
                callback(qrCodeText)
            }
            .addOnFailureListener { e ->
                Log.e("QRCodeExtraction", "QR code extraction failed", e)
                callback(null)
            }
    }

    private fun extractTextWithMLKit(bitmap: Bitmap, callback: (String) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extractedText = visionText.text
                callback(extractedText)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Text extraction failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("TextExtraction", "Text extraction failed", e)
            }
    }

    private fun extractTextWithGenerativeAI(bitmap: Bitmap, callback: (String) -> Unit) {
        val prompt = content {
            text("Extract only the text from this image, including any Bangla or handwritten text.")
            image(bitmap)
        }

        val response = generativeModelFutures.generateContent(prompt)
        Futures.addCallback(
            response,
            object : FutureCallback<GenerateContentResponse> {
                override fun onSuccess(result: GenerateContentResponse) {
                    val extractedText = result.candidates.firstOrNull()?.content?.parts
                        ?.filterIsInstance<com.google.ai.client.generativeai.type.TextPart>()
                        ?.joinToString("") { it.text }
                        ?: "No text extracted"
                    callback(extractedText)
                }

                override fun onFailure(t: Throwable) {
                    runOnUiThread {
                        Toast.makeText(
                            this@TextExtractionActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            Executors.newSingleThreadExecutor()
        )
    }

    private fun showExtractedText(text: String) {
        val intent = Intent(this, TextExtractionResultActivity::class.java)
        intent.putExtra("originalText", text)
        startActivity(intent)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}