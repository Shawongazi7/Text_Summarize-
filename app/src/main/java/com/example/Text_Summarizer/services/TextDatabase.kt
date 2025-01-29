package com.example.Text_Summarizer.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TextEntity::class, DeletionEntity::class], version = 2, exportSchema = false)
abstract class TextDatabase : RoomDatabase() {
    abstract fun textDao(): TextDao

    companion object {
        // Volatile ensures that the value of INSTANCE is always up-to-date and the same to all execution threads.
        @Volatile
        private var INSTANCE: TextDatabase? = null

        fun getDatabase(context: Context): TextDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TextDatabase::class.java,
                    "text_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}