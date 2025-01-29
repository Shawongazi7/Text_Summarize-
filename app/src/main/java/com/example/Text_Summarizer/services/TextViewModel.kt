package com.example.Text_Summarizer.services

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.launch

class TextViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TextRepository = TextRepository(application)
    val _allTexts = MutableLiveData<List<TextEntity>>()
    private val getAllTextEntity: LiveData<List<TextEntity>> get() = _allTexts

    init {
        loadAllTexts()
    }

    private fun loadAllTexts() {
        viewModelScope.launch {
            val texts = repository.getAllTexts()
            _allTexts.postValue(texts)
        }
    }

    fun insertText(textEntity: TextEntity) {
        viewModelScope.launch {
            repository.insertText(textEntity)
            loadAllTexts() // Refresh the list after insertion
        }
    }

    fun deleteText(text: TextEntity) {
        viewModelScope.launch {
            repository.deleteText(text)
            loadAllTexts() // Refresh the list after deletion
            scheduleSync(getApplication())
        }
    }

    fun deleteAllTexts() {
        viewModelScope.launch {
            repository.deleteAllTexts()
            loadAllTexts()
        }
    }

    fun updateText(textEntity: TextEntity) {
        viewModelScope.launch {
            repository.updateText(textEntity)
            loadAllTexts() // Refresh the list after update
        }
    }

    fun updateTitle(id: Long, title: String?) {
        viewModelScope.launch {
            repository.updateTitle(id, title)
            loadAllTexts() // Refresh the list after update
        }
    }

    fun updateDescription(id: Long, description: String?) {
        viewModelScope.launch {
            repository.updateDescription(id, description)
            loadAllTexts() // Refresh the list after update
        }
    }

    fun getTextEntity(id: Long): LiveData<TextEntity?> {
        val textEntity = MutableLiveData<TextEntity?>()
        viewModelScope.launch {
            textEntity.postValue(repository.getTextEntity(id))
        }
        return textEntity
    }

    fun getAllTexts(): LiveData<List<TextEntity>> {
        return getAllTextEntity
    }

    private fun scheduleSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }
}