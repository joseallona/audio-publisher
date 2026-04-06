package com.audiopublisher.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.audiopublisher.core.database.model.Recording
import com.audiopublisher.core.database.model.RecordingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {

    @Query("SELECT * FROM recordings WHERE status != 'DELETED' ORDER BY createdAt DESC")
    fun getAllActive(): Flow<List<Recording>>

    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getById(id: String): Recording?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recording: Recording)

    @Update
    suspend fun update(recording: Recording)

    @Query("UPDATE recordings SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: RecordingStatus)

    @Query("UPDATE recordings SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: String, title: String)

    @Query("SELECT filePath FROM recordings WHERE status = 'DELETED'")
    suspend fun getDeletedFilePaths(): List<String>
}
