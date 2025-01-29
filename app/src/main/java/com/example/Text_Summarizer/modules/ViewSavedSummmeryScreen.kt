package com.example.Text_Summarizer.modules

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.modelsheets.editSummeryModel
import com.example.Text_Summarizer.modelsheets.savedSummeryDeleteModel
import com.example.Text_Summarizer.services.TextViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ViewSavedSummmeryScreen : AppCompatActivity() {
    private val textViewModel: TextViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_saved_summmery_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_cream)

        val backBtnSavedView: ImageButton = findViewById(R.id.back_btn_saved_view)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val deleteSummeryBtn: ImageButton = findViewById(R.id.delete_summery_btn)
        val editSummeryBtn: ImageButton = findViewById(R.id.edit_summery_btn)
        val copy_btn: ImageButton = findViewById(R.id.copy_btn)
        val textId: Long =
            intent.getLongExtra("TEXT_ID", 1L) // get the id of the text entity you want to view

        val summeryTopic: TextView = findViewById(R.id.title)
        val saveCardDate: TextView = findViewById(R.id.card_date)
        val descriptionContent: TextView = findViewById(R.id.card_description)
        val summarizedTxt: TextView = findViewById(R.id.card_summery)
        val orgTxt: TextView = findViewById(R.id.card_org)

        textViewModel.getTextEntity(textId).observe(this, { entity ->
            entity?.let { textEntity ->
                if (entity != null) {
                    Log.d("ViewSavedSummaryScreen", "TextEntity fetched: $entity")
                    summeryTopic.text = textEntity.title
                    saveCardDate.text = textEntity.date
                    descriptionContent.text = textEntity.description
                    summarizedTxt.text = textEntity.summary
                    orgTxt.text = textEntity.originalText

                    copy_btn.setOnClickListener {
                        val clipboardManager =
                            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("summary", summarizedTxt.text)
                        clipboardManager.setPrimaryClip(clipData)

                        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                    editSummeryBtn.setOnClickListener {
                        val summeryModel = editSummeryModel(textEntity)
                        summeryModel.show(supportFragmentManager, editSummeryModel.TAG)
                    }

                    deleteSummeryBtn.setOnClickListener {
                        val conformDeleteModel = savedSummeryDeleteModel(textEntity)
                        conformDeleteModel.show(supportFragmentManager, savedSummeryDeleteModel.TAG)
                    }
                } else {
                    Log.d("VewSavedSummaryScreen", "TextEntity is null")
                }
            }
        })

        bottomNavigation.selectedItemId = R.id.page_3

        backBtnSavedView.setOnClickListener {
            val intent = Intent(this, SavedScreenActivity::class.java)
            startActivity(intent)
        }

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

//                R.id.page_2 -> {
//                    val intent = Intent(this, RecentScreenActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                    true
//                }

                R.id.page_3 -> true
                R.id.page_4 -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }

                else -> false
            }
        }
    }
}