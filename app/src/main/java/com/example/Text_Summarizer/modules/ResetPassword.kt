package com.example.Text_Summarizer.modules

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.modules.LoginActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {
    private var v_forgotpassemail: EditText? = null
    private var v_pass_recover_button: Button? = null
    private var v_gobacktologin: TextView? = null

    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        window.statusBarColor = ContextCompat.getColor(this, R.color.log_reg_color)

        FirebaseApp.initializeApp(this)

        //getSupportActionBar().hide();
        v_forgotpassemail = findViewById(R.id.forgotpassemail)
        v_pass_recover_button = findViewById(R.id.pass_recover_button)
        v_gobacktologin = findViewById(R.id.gobacktologin)
        firebaseAuth = FirebaseAuth.getInstance()

        v_gobacktologin?.setOnClickListener( {
            val intent = Intent(
                this@ResetPassword,
                LoginActivity::class.java
            )
            startActivity(intent)
        })

        v_pass_recover_button?.setOnClickListener( {
            val mail = v_forgotpassemail?.text.toString().trim { it <= ' ' }
            if (mail.isEmpty()) {
                Toast.makeText(applicationContext, "Enter Your Mail", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth!!.sendPasswordResetEmail(mail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Pass Recover Mail Sent",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        startActivity(
                            Intent(
                                this@ResetPassword,
                                LoginActivity::class.java
                            )
                        )
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Email Is not REGISTERED",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}