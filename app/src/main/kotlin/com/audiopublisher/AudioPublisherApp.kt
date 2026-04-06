package com.audiopublisher

import android.app.Application
import android.util.Log
import com.audiopublisher.core.database.repository.RecordingRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class AudioPublisherApp : Application() {

    @Inject lateinit var repository: RecordingRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        cleanupOrphanFiles()
    }

    private fun cleanupOrphanFiles() {
        scope.launch {
            try {
                val deletedPaths = repository.getDeletedFilePaths().toSet()
                deletedPaths.forEach { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                        Log.d("AudioPublisherApp", "Deleted orphan file: $path")
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioPublisherApp", "Orphan cleanup failed", e)
            }
        }
    }
}
