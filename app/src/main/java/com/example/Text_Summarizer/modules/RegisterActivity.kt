//right image uploaded in imgbb and email varification for registration
package com.example.Text_Summarizer.modules

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private var v_signinfullname: EditText? = null
    private var v_signinemail: EditText? = null
    private var v_signinpassword: EditText? = null
    private var v_signinconfirmpassword: EditText? = null
    private var v_signup: AppCompatButton? = null
    private var v_gotologin: TextView? = null
    private var v_profileImage: ImageView? = null
    private var progressDialog: ProgressDialog? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    private var imageUri: Uri? = null
    private val imgbbApiKey = "5f5d65dc7d6239745cd2338eadd75e7b"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        FirebaseApp.initializeApp(this)

        v_signinfullname = findViewById(R.id.signinfullname)
        v_signinemail = findViewById(R.id.signinemail)
        v_signinpassword = findViewById(R.id.signinpassword)
        v_signinconfirmpassword = findViewById(R.id.signinconfirmpassword)
        v_signup = findViewById(R.id.signup)
        v_gotologin = findViewById(R.id.gotologin)
        v_profileImage = findViewById(R.id.profile_image)

        window.statusBarColor = ContextCompat.getColor(this, R.color.log_reg_color)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Registering...")
        progressDialog!!.setCancelable(false)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        v_gotologin?.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        v_signup?.setOnClickListener {
            val fullName = v_signinfullname?.text.toString().trim()
            val mail = v_signinemail?.text.toString().trim()
            val pass = v_signinpassword?.text.toString().trim()
            val confirmPass = v_signinconfirmpassword?.text.toString().trim()
            val passwordRegex =
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()
            if (fullName.isEmpty() || mail.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(applicationContext, "Fill All the Fields", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT)
                    .show()
            } else if (!pass.matches(passwordRegex)) {
                Toast.makeText(
                    applicationContext,
                    "Password must be at least 8 characters long and include at least one letter, one number, and one special character",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (pass != confirmPass) {
                Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog!!.setMessage("Creating user...")
                progressDialog!!.show()
                firebaseAuth!!.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            sendVerificationEmail(fullName, mail)
                        } else {
                            progressDialog!!.dismiss()
                            val exception = task.exception
                            val errorMessage = exception?.message ?: "Unknown error"
                            Toast.makeText(
                                applicationContext,
                                "Failed to Register: $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("RegisterActivity", "Registration failed", exception)
                        }
                    }
            }
        }
    }

    private fun sendVerificationEmail(fullName: String, mail: String) {
        val user = firebaseAuth!!.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadProfileImageToImgBB(fullName, mail)
            } else {
                progressDialog!!.dismiss()
                val exception = task.exception
                val errorMessage = exception?.message ?: "Failed to send verification email."
                Toast.makeText(
                    applicationContext,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
                Log.e("RegisterActivity", "Failed to send verification email", exception)
            }
        }
    }

    fun chooseProfileImage(view: android.view.View?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                contentResolver.takePersistableUriPermission(
                    imageUri!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Log.e("RegisterActivity", "Failed to take persistable URI permission", e)
            }
            v_profileImage!!.setImageURI(imageUri)
        }
    }

    private fun uploadProfileImageToImgBB(fullName: String, mail: String) {
        if (imageUri == null) {
            saveUserInfo(fullName, mail, null)
            return
        }

        val resizedImageUri = resizeImage(imageUri!!)
        val inputStream: InputStream? = contentResolver.openInputStream(resizedImageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val base64Image = bitmapToBase64(bitmap)

        progressDialog!!.setMessage("Uploading image...")
        progressDialog!!.show()

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("key", imgbbApiKey)
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog!!.dismiss()
                Log.e("RegisterActivity", "Image upload failed", e)
                saveUserInfo(fullName, mail, null)
            }

            override fun onResponse(call: Call, response: Response) {
                progressDialog!!.dismiss()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "")
                    val imageUrl = jsonObject.getJSONObject("data").getString("url")
                    saveUserInfo(fullName, mail, imageUrl)
                } else {
                    Log.e("RegisterActivity", "Image upload failed: ${response.message}")
                    saveUserInfo(fullName, mail, null)
                }
            }
        })
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun resizeImage(imageUri: Uri): Uri {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 500, 500, true)

        val resizedImageFile = File(cacheDir, "resized_image.jpg")
        val outputStream = FileOutputStream(resizedImageFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(resizedImageFile)
    }

    private fun saveUserInfo(fullName: String, mail: String, profileImageUrl: String?) {
        val currentUser = firebaseAuth!!.currentUser
        if (currentUser == null) {
            Log.e("RegisterActivity", "Current user is null")
            Toast.makeText(applicationContext, "Error: User not found", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val user: MutableMap<String, Any> = HashMap()
        user["fullName"] = fullName
        user["email"] = mail
        user["isEmailVerified"] = false // Add this flag to indicate email verification status
        if (profileImageUrl != null) {
            user["profileImageUrl"] = profileImageUrl
        }

        firestore!!.collection("users").document(userId).set(user)
            .addOnCompleteListener { task: Task<Void?> ->
                progressDialog!!.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Registered Successfully. Please verify your email.",
                        Toast.LENGTH_SHORT
                    ).show()
                    firebaseAuth!!.signOut()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val exception = task.exception
                    val errorMessage = exception?.message ?: "Unknown error"
                    Toast.makeText(
                        applicationContext,
                        "Error saving user info: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("RegisterActivity", "Failed to save user info", exception)
                }
            }
    }
}