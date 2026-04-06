package com.audiopublisher.core.database.repository

import com.audiopublisher.core.database.dao.RecordingDao
import com.audiopublisher.core.database.model.Recording
import com.audiopublisher.core.database.model.RecordingStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecordingRepositoryImpl @Inject constructor(
    private val dao: RecordingDao
) : RecordingRepository {

    override fun getAllActive(): Flow<List<Recording>> = dao.getAllActive()

    override suspend fun getById(id: String): Recording? = dao.getById(id)

    override suspend fun save(recording: Recording) = dao.insert(recording)

    override suspend fun updateTitle(id: String, title: String) = dao.updateTitle(id, title)

    override suspend fun delete(id: String) = dao.updateStatus(id, RecordingStatus.DELETED)

    override suspend fun getDeletedFilePaths(): List<String> = dao.getDeletedFilePaths()
}
