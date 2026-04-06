package com.audiopublisher.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.audiopublisher.core.database.dao.RecordingDao
import com.audiopublisher.core.database.model.Recording

@Database(
    entities = [Recording::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
}
