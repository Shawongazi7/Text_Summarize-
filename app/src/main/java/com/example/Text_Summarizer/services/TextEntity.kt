package com.example.Text_Summarizer.services

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "texts")
data class TextEntity(
    @PrimaryKey(autoGenerate = true)
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = 0,

    @ColumnInfo(name = "title")
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String? = null,

    @ColumnInfo(name = "date")
    @get:PropertyName("date")
    @set:PropertyName("date")
    var date: String? = null,

    @ColumnInfo(name = "description")
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String? = null,

    @ColumnInfo(name = "summary")
    @get:PropertyName("summary")
    @set:PropertyName("summary")
    var summary: String? = null,

    @ColumnInfo(name = "original_text")
    @get:PropertyName("originalText")
    @set:PropertyName("originalText")
    var originalText: String? = null
)

@Entity(tableName = "deletions")
data class DeletionEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "text_id")
    var textId: Long
)