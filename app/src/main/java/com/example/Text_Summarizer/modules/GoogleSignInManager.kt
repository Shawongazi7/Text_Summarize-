package com.example.Text_Summarizer.modules

import android.content.Context
import com.example.Text_Summarizer.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignInManager private constructor() {

    private var googleSignInClient: GoogleSignInClient? = null

    companion object {
        @Volatile
        private var instance: GoogleSignInManager? = null

        @Synchronized
        fun getInstance(): GoogleSignInManager {
            return instance ?: synchronized(this) {
                instance ?: GoogleSignInManager().also { instance = it }
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        if (googleSignInClient == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(context, gso)
        }
        return googleSignInClient!!
    }

    fun signOut(context: Context, onCompleteCallback: Runnable?) {
        googleSignInClient?.signOut()?.addOnCompleteListener {
            googleSignInClient?.revokeAccess()?.addOnCompleteListener {
                // Reset the client to force a new account selection next time
                googleSignInClient = null
                onCompleteCallback?.run()
            }
        } ?: onCompleteCallback?.run()
    }
}