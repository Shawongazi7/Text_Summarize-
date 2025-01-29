package com.example.Text_Summarizer.services

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TextRepository(context: Context) {
    private val db: TextDatabase = Room.databaseBuilder(
        context.applicationContext,
        TextDatabase::class.java, "text-database"
    ).build()

    private val textDao: TextDao = db.textDao()

    suspend fun insertText(textEntity: TextEntity): Long {
        return withContext(Dispatchers.IO) {
            textDao.insertText(textEntity)
        }
    }

    suspend fun getAllTexts(): List<TextEntity> {
        return withContext(Dispatchers.IO) {
            textDao.getAllTexts()
        }
    }

    suspend fun deleteText(text: TextEntity) {
        withContext(Dispatchers.IO) {
            textDao.deleteText(text)
            textDao.insertDeletion(DeletionEntity(textId = text.id))
        }
    }

    suspend fun deleteAllTexts(): Int {
        return withContext(Dispatchers.IO) {
            textDao.deleteAllTexts()
        }
    }

    suspend fun updateText(textEntity: TextEntity): Int {
        return withContext(Dispatchers.IO) {
            textDao.updateText(textEntity)
        }
    }

    suspend fun updateTitle(id: Long, title: String?): Int {
        return withContext(Dispatchers.IO) {
            textDao.updateTitle(id, title)
        }
    }

    suspend fun updateDescription(id: Long, description: String?): Int {
        return withContext(Dispatchers.IO) {
            textDao.updateDescription(id, description)
        }
    }

    suspend fun getTextEntity(id: Long): TextEntity? {
        return withContext(Dispatchers.IO) {
            textDao.getTextEntity(id)
        }
    }

    suspend fun getAllDeletions(): List<DeletionEntity> {
        return withContext(Dispatchers.IO) {
            textDao.getAllDeletions()
        }
    }

    suspend fun deleteDeletionByTextId(textId: Long) {
        withContext(Dispatchers.IO) {
            textDao.deleteDeletionByTextId(textId)
        }
    }
}