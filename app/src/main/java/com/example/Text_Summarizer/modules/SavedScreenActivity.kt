package com.example.Text_Summarizer.modules

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    private lateinit var noSearchResultsView: View
    private lateinit var searchView: SearchView
    private val textViewModel: TextViewModel by viewModels()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var adapter: TextAdapter
    private var allTexts: List<Text> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        recyclerView = findViewById(R.id.recycler_view_saved_items)
        emptyView = findViewById(R.id.empty_view)
        noSearchResultsView = findViewById(R.id.no_search_results_view)
        searchView = findViewById(R.id.search_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        setupAdapter()
        setupSearchView()
        observeDatabase()
        setupBottomNavigation()
    }

    private fun setupAdapter() {
        adapter = TextAdapter { text ->
            val deleteModel = savedSummeryDeleteModel(TextEntity(text.id, text.title, text.description, text.date))
            deleteModel.show(supportFragmentManager, savedSummeryDeleteModel.TAG)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTexts(newText)
                return true
            }
        })
        
        // Handle search view close button
        searchView.setOnCloseListener {
            filterTexts("")
            false
        }
    }

    private fun filterTexts(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            allTexts
        } else {
            allTexts.filter { text ->
                text.title.contains(query, ignoreCase = true) ||
                text.description.contains(query, ignoreCase = true)
            }
        }
        
        if (allTexts.isEmpty()) {
            // No texts at all - show original empty view
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            noSearchResultsView.visibility = View.GONE
        } else if (filteredList.isEmpty() && !query.isNullOrEmpty()) {
            // Search returned no results - show no search results view
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.GONE
            noSearchResultsView.visibility = View.VISIBLE
        } else {
            // Show filtered results
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            noSearchResultsView.visibility = View.GONE
            adapter.submitList(filteredList)
        }
    }

    private fun observeDatabase() {
        textViewModel.getAllTexts().observe(this, Observer { texts ->
            if (texts == null || texts.isEmpty()) {
                allTexts = emptyList()
                searchView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                noSearchResultsView.visibility = View.GONE

                emptyView.findViewById<View>(R.id.summery_btn)?.setOnClickListener {
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            } else {
                searchView.visibility = View.VISIBLE
                val nonNullTexts: List<Text> = texts.map { textEntity ->
                    Text(
                        id = textEntity.id,
                        title = textEntity.title ?: "",
                        description = textEntity.description ?: "",
                        date = textEntity.date ?: ""
                    )
                }
                allTexts = nonNullTexts
                
                // Apply current search filter
                filterTexts(searchView.query.toString())
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