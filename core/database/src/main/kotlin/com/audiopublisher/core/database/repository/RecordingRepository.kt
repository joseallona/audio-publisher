package com.audiopublisher.core.database.repository

import com.audiopublisher.core.database.model.Recording
import kotlinx.coroutines.flow.Flow

interface RecordingRepository {
    fun getAllActive(): Flow<List<Recording>>
    suspend fun getById(id: String): Recording?
    suspend fun save(recording: Recording)
    suspend fun updateTitle(id: String, title: String)
    suspend fun delete(id: String)
    suspend fun getDeletedFilePaths(): List<String>
}
