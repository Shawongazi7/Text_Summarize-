//package com.example.Text_Summarizer.modules
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import com.example.Text_Summarizer.R
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class RecentScreenActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_recent_screen)
//
//        val bottom_navigation : BottomNavigationView = findViewById(R.id.bottom_navigation)
//        val back_to_home : Button = findViewById(R.id.summery_btn)
//
//        back_to_home.setOnClickListener(){
//            val intent = Intent(this, HomeScreenActivity::class.java)
//            startActivity(intent)
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        }
//
//        bottom_navigation.selectedItemId = R.id.page_2
//
//        bottom_navigation.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    val intent = Intent(this, HomeScreenActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//                R.id.page_2 -> {
//                    true
//                }
//                R.id.page_3 -> {
//                    val intent = Intent(this, SavedScreenActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//                R.id.page_4 -> {
//                    val intent = Intent(this, ProfileActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//                else -> false
//            } }
//    }
//}