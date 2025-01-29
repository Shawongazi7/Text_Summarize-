package com.example.Text_Summarizer.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = TextRepository(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val deletions = repository.getAllDeletions()
            val firestore = FirebaseFirestore.getInstance()

            deletions.forEach { deletion ->
                firestore.collection("texts").document(deletion.textId.toString()).delete().await()
                repository.deleteDeletionByTextId(deletion.textId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}