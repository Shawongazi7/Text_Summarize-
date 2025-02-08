//package com.example.Text_Summarizer.modules
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.Text_Summarizer.R
//import com.example.Text_Summarizer.adapters.Text
//import com.example.Text_Summarizer.adapters.TextAdapter
//import com.example.Text_Summarizer.services.TextEntity
//import com.example.Text_Summarizer.services.TextViewModel
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class SavedScreenActivity : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var emptyView: View
//    private val textViewModel: TextViewModel by viewModels()
//    private lateinit var bottomNavigationView: BottomNavigationView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_saved_screen)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)
//
//        recyclerView = findViewById(R.id.recycler_view_saved_items)
//        emptyView = findViewById(R.id.empty_view)
//        bottomNavigationView = findViewById(R.id.bottom_navigation)
//
//        observeDatabase()  // Observe the database immediately.
//
//        setupBottomNavigation()
//    }
//
//    private fun observeDatabase() {
//        textViewModel.getAllTexts().observe(this, Observer { texts ->
//            //Crucially, check if the list is null or empty
//            if (texts == null || texts.isEmpty()) {
//                // No saved data, show Recent screen content
//                recyclerView.visibility = View.GONE
//                emptyView.visibility = View.VISIBLE
//
//                emptyView.findViewById<View>(R.id.summery_btn)?.setOnClickListener {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                }
//
//
//            } else {
//                // Saved data exists, show Saved screen
//                recyclerView.visibility = View.VISIBLE
//                emptyView.visibility = View.GONE
//                val adapter = TextAdapter()
//                recyclerView.layoutManager = LinearLayoutManager(this)
//                recyclerView.adapter = adapter
//
//                //Crucially, handle potential nulls from the database entities.
//                val nonNullTexts: List<Text> = texts.map { textEntity ->
//                    Text(
//                        id = textEntity.id,
//                        title = textEntity.title ?: "",
//                        description = textEntity.description ?: "",
//                        date = textEntity.date ?: ""
//                    )
//                }
//                adapter.submitList(nonNullTexts)
//            }
//        })
//    }
//
//
//    private fun setupBottomNavigation() {
//        bottomNavigationView.selectedItemId = R.id.page_3
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.page_1 -> {
//                    startActivity(Intent(this, HomeScreenActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//                R.id.page_2 -> {
//                    startActivity(Intent(this, TextExtractionActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//                R.id.page_3 -> true  // Stay on SavedScreen
//                R.id.page_4 -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//}



package com.example.Text_Summarizer.modules

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.adapters.Text
import com.example.Text_Summarizer.adapters.TextAdapter
import com.example.Text_Summarizer.modelsheets.savedSummeryDeleteModel
import com.example.Text_Summarizer.services.TextEntity
import com.example.Text_Summarizer.services.TextViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SavedScreenActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private val textViewModel: TextViewModel by viewModels()
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        recyclerView = findViewById(R.id.recycler_view_saved_items)
        emptyView = findViewById(R.id.empty_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        observeDatabase()

        setupBottomNavigation()
    }

    private fun observeDatabase() {
        textViewModel.getAllTexts().observe(this, Observer { texts ->
            if (texts == null || texts.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE

                emptyView.findViewById<View>(R.id.summery_btn)?.setOnClickListener {
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                val adapter = TextAdapter { text ->
                    val deleteModel = savedSummeryDeleteModel(TextEntity(text.id, text.title, text.description, text.date))
                    deleteModel.show(supportFragmentManager, savedSummeryDeleteModel.TAG)
                }
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter

                val nonNullTexts: List<Text> = texts.map { textEntity ->
                    Text(
                        id = textEntity.id,
                        title = textEntity.title ?: "",
                        description = textEntity.description ?: "",
                        date = textEntity.date ?: ""
                    )
                }
                adapter.submitList(nonNullTexts)
            }
        })
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.page_3
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.page_2 -> {
                    startActivity(Intent(this, TextExtractionActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.page_3 -> true
                R.id.page_4 -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}