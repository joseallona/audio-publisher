package com.audiopublisher.core.database

import com.audiopublisher.core.database.dao.RecordingDao
import com.audiopublisher.core.database.model.Recording
import com.audiopublisher.core.database.model.RecordingStatus
import com.audiopublisher.core.database.model.SourceType
import com.audiopublisher.core.database.repository.RecordingRepositoryImpl
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecordingRepositoryTest {

    private lateinit var dao: RecordingDao
    private lateinit var repository: RecordingRepositoryImpl

    private val testRecording = Recording(
        id = "test-id",
        title = "Test Recording",
        filePath = "/test/path.m4a",
        durationMs = 5000L,
        sizeBytes = 12345L,
        createdAt = System.currentTimeMillis(),
        sourceType = SourceType.PHONE_MIC,
        status = RecordingStatus.READY
    )

    @Before
    fun setup() {
        dao = mock()
        repository = RecordingRepositoryImpl(dao)
    }

    @Test
    fun `save calls dao insert`() = runTest {
        repository.save(testRecording)
        verify(dao).insert(testRecording)
    }

    @Test
    fun `delete marks status as DELETED`() = runTest {
        repository.delete("test-id")
        verify(dao).updateStatus("test-id", RecordingStatus.DELETED)
    }

    @Test
    fun `updateTitle calls dao updateTitle`() = runTest {
        repository.updateTitle("test-id", "New Title")
        verify(dao).updateTitle("test-id", "New Title")
    }

    @Test
    fun `getAllActive returns flow from dao`() = runTest {
        val recordings = listOf(testRecording)
        whenever(dao.getAllActive()).thenReturn(flowOf(recordings))

        val result = mutableListOf<List<Recording>>()
        repository.getAllActive().collect { result.add(it) }

        assertEquals(1, result.size)
        assertEquals(recordings, result[0])
    }

    @Test
    fun `getDeletedFilePaths delegates to dao`() = runTest {
        val paths = listOf("/path/deleted.m4a")
        whenever(dao.getDeletedFilePaths()).thenReturn(paths)

        val result = repository.getDeletedFilePaths()
        assertEquals(paths, result)
    }
}
