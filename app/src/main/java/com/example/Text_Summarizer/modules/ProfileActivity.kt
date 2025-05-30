package com.example.Text_Summarizer.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.services.SyncWorker
import com.example.Text_Summarizer.services.TextEntity
import com.example.Text_Summarizer.services.TextViewModel
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var logoutButton: Button
    private lateinit var backupButton: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var textViewModel: TextViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize ViewModel
        textViewModel = ViewModelProvider(this)[TextViewModel::class.java]

        // Initialize UI components
        initializeUIComponents()

        // Setup Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check and handle user authentication
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser)
        } else {
            redirectToLogin()
        }

        // Setup listeners
        setupButtonListeners()
        setupBottomNavigation()
    }

    private fun initializeUIComponents() {
        profileImage = findViewById(R.id.profileImage)
        profileName = findViewById(R.id.profileName)
        profileEmail = findViewById(R.id.profileEmail)
        logoutButton = findViewById(R.id.logoutButton)
        backupButton = findViewById(R.id.backupButton)
    }

    private fun fetchUserData(user: FirebaseUser) {
        val userId = user.uid
        firestore.collection("users").document(userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (documentSnapshot.exists()) {
                        // Load data from Firestore
                        val fullName = documentSnapshot.getString("fullName")
                        val email = documentSnapshot.getString("email")
                        val profileImageUrl = documentSnapshot.getString("profileImageUrl")
                        updateUI(fullName, email, profileImageUrl)
                    } else {
                        // Fallback to Firebase User data
                        val email = user.email
                        val name = extractNameFromEmail(email)
                        val photoUrl = user.photoUrl?.toString()

                        updateUI(name, email, photoUrl)
                        saveUserToFirestore(userId, name, email, photoUrl)
                    }
                } else {
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(name: String?, email: String?, profileImageUrl: String?) {
        profileName.text = name ?: "User"
        profileEmail.text = email ?: "No email"

        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.ic_account)
        }
    }

    private fun extractNameFromEmail(email: String?): String {
        return email?.substringBefore("@") ?: "User"
    }

    private fun saveUserToFirestore(
        userId: String,
        name: String,
        email: String?,
        profileImageUrl: String?
    ) {
        val userData = hashMapOf(
            "fullName" to name,
            "email" to email,
            "profileImageUrl" to profileImageUrl
        )

        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "User profile saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupButtonListeners() {
        logoutButton.setOnClickListener { logout() }
        backupButton.setOnClickListener { showBackupRestoreDialog() }
    }

    private fun showBackupRestoreDialog() {
        val options = arrayOf("Backup to Cloud", "Restore from Cloud")
        AlertDialog.Builder(this)
            .setTitle("Cloud Sync")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> backupUserData()
                    1 -> restoreFromFirestore()
                }
            }
            .show()
    }

    private fun backupUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val userDataRef = firestore.collection("users").document(userId).collection("texts")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch local texts
                val localTexts = textViewModel._allTexts.value ?: emptyList()

                // Fetch existing texts from Firestore
                val firestoreSnapshot = userDataRef.get().await()
                val existingFirestoreTexts = firestoreSnapshot.toObjects(TextEntity::class.java)

                // Sync local texts to Firestore
                val localTextIds = localTexts.map { it.id }

                // Add or update local texts in Firestore
                localTexts.forEach { textEntity ->
                    userDataRef.document(textEntity.id.toString()).set(textEntity)
                }

                // Remove texts from Firestore that no longer exist in local database
                existingFirestoreTexts.forEach { firestoreText ->
                    if (firestoreText.id !in localTextIds) {
                        userDataRef.document(firestoreText.id.toString()).delete()
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Backup and sync completed: ${localTexts.size} texts",
                        Toast.LENGTH_SHORT
                    ).show()
                    schedleSync(this@ProfileActivity)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Backup failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ProfileActivity", "Backup error", e)
                }
            }
        }
    }

    private fun restoreFromFirestore() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val userDataRef = firestore.collection("users").document(userId).collection("texts")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch texts from Firestore
                val firestoreSnapshot = userDataRef.get().await()
                val firestoreTexts = firestoreSnapshot.toObjects(TextEntity::class.java)

                // Clear local database
                textViewModel.deleteAllTexts()

                // Insert Firestore texts into local database
                firestoreTexts.forEach { textEntity ->
                    textViewModel.insertText(textEntity)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Restored ${firestoreTexts.size} texts from cloud",
                        Toast.LENGTH_SHORT
                    ).show()
                    schedleSync(this@ProfileActivity)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Restore failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ProfileActivity", "Restore error", e)
                }
            }
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, OnStartScreenActivity::class.java))
        finish()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.page_4

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    navigateTo(HomeScreenActivity::class.java)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

                R.id.page_2 -> {
                    navigateTo(TextExtractionActivity::class.java)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

                R.id.page_3 -> {
                    navigateTo(SavedScreenActivity::class.java)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

                R.id.page_4 -> {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    if (firebaseUser == null) {
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, OnStartScreenActivity::class.java))
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun schedleSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}