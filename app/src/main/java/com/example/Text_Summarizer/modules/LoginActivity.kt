package com.example.Text_Summarizer.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private var v_loginemail: EditText? = null
    private var v_loginpassword: EditText? = null
    private var v_login: AppCompatButton? = null
    private var v_createAcc: AppCompatButton? = null
    private var v_guestLogin: AppCompatButton? = null
    private var v_gotof_pass: TextView? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_2)
        FirebaseApp.initializeApp(this)

        v_loginemail = findViewById(R.id.loginemail)
        v_loginpassword = findViewById(R.id.loginpassword)
        v_login = findViewById(R.id.login)
        v_gotof_pass = findViewById(R.id.v_gotof_pass)
        v_createAcc = findViewById(R.id.createAcc)
        v_guestLogin = findViewById(R.id.guestLogin)
        val googleSignInButton = findViewById<SignInButton>(R.id.googleSignInButton)

        window.statusBarColor = ContextCompat.getColor(this, R.color.log_reg_color)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val firebaseUser = firebaseAuth!!.currentUser

        if (firebaseUser != null) {
            finish()
            startActivity(Intent(this@LoginActivity, HomeScreenActivity::class.java))
        }

        // Get GoogleSignInClient from GoogleSignInManager
        googleSignInClient = GoogleSignInManager.getInstance().getGoogleSignInClient(this)

        googleSignInButton.setOnClickListener { signInWithGoogle() }

        v_login?.setOnClickListener {
            val mail = v_loginemail?.text.toString().trim { it <= ' ' }
            val pass = v_loginpassword?.text.toString().trim { it <= ' ' }
            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(applicationContext, "Fill All the Fields", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth?.signInWithEmailAndPassword(mail, pass)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth!!.currentUser
                            if (user != null) {
                                if (user.isEmailVerified) {
                                    updateUserEmailVerifiedFlag(user.uid)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please verify your email address.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    firebaseAuth!!.signOut()
                                }
                            }
                        } else {
                            val exception = task.exception
                            val errorMessage = exception?.message ?: "Unknown error"
                            Toast.makeText(
                                applicationContext,
                                "Login Failed: $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("LoginActivity", "Login failed", exception)
                        }
                    }
            }
        }

        v_gotof_pass?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ResetPassword::class.java))
        }

        v_createAcc?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        v_guestLogin?.setOnClickListener { guestLogin() }
    }

    private fun signInWithGoogle() {
        // Ensure the user is always prompted to select an account
        googleSignInClient?.signOut()?.addOnCompleteListener {
            googleSignInClient?.revokeAccess()?.addOnCompleteListener {
                googleSignInClient = GoogleSignInManager.getInstance().getGoogleSignInClient(this)
                val signInIntent = googleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this@LoginActivity, "Google sign in failed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth!!.currentUser
                    Toast.makeText(
                        this@LoginActivity,
                        "Google Sign-In successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, HomeScreenActivity::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication Failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun guestLogin() {
        val intent = Intent(this@LoginActivity, HomeScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateUserEmailVerifiedFlag(userId: String) {
        firestore!!.collection("users").document(userId)
            .update("isEmailVerified", true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Logged In", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this@LoginActivity, HomeScreenActivity::class.java))
                } else {
                    val exception = task.exception
                    val errorMessage = exception?.message ?: "Unknown error"
                    Toast.makeText(
                        applicationContext,
                        "Failed to update email verification flag: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("LoginActivity", "Failed to update email verification flag", exception)
                    firebaseAuth!!.signOut()
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"

        fun logout(context: Context?, onCompleteCallback: Runnable?) {
            FirebaseAuth.getInstance().signOut()
            if (context != null) {
                GoogleSignInManager.getInstance().signOut(context, onCompleteCallback)
            }
        }
    }
}