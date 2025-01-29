package com.example.Text_Summarizer.services

import androidx.room.*

@Dao
interface TextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertText(textEntity: TextEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(textEntities: List<TextEntity>): List<Long>

    @Query("SELECT * FROM texts WHERE id = :id")
    suspend fun getTextEntity(id: Long): TextEntity?

    @Query("SELECT * FROM texts")
    suspend fun getAllTexts(): List<TextEntity>

    @Delete
    suspend fun deleteText(text: TextEntity): Int

    @Update
    suspend fun updateText(textEntity: TextEntity): Int

    @Query("UPDATE texts SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String?): Int

    @Query("UPDATE texts SET description = :description WHERE id = :id")
    suspend fun updateDescription(id: Long, description: String?): Int

    @Query("DELETE FROM texts")
    suspend fun deleteAllTexts(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDeletion(deletion: DeletionEntity)

    @Query("SELECT * FROM deletions")
    fun getAllDeletions(): List<DeletionEntity>

    @Query("DELETE FROM deletions WHERE text_id = :textId")
    fun deleteDeletionByTextId(textId: Long)

}