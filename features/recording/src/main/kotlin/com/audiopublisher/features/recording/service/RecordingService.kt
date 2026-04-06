package com.audiopublisher.features.recording.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.audiopublisher.core.database.model.Recording
import com.audiopublisher.core.database.model.RecordingStatus
import com.audiopublisher.core.database.model.SourceType
import com.audiopublisher.core.database.repository.RecordingRepository
import com.audiopublisher.core.media.RecorderEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class RecordingService : Service() {

    @Inject lateinit var recorderEngine: RecorderEngine
    @Inject lateinit var repository: RecordingRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentFilePath: String? = null
    private var isRecording = false

    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY && isRecording) {
                Log.d(TAG, "Headphones unplugged — pausing recording")
                recorderEngine.pause()
                isRecording = false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(
            noisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart()
            ACTION_PAUSE -> handlePause()
            ACTION_RESUME -> handleResume()
            ACTION_STOP -> handleStop()
        }
        return START_STICKY
    }

    private fun handleStart() {
        startForeground(NOTIFICATION_ID, buildNotification())
        val dir = getExternalFilesDir("recordings") ?: filesDir.resolve("recordings").also { it.mkdirs() }
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "${UUID.randomUUID()}.m4a")
        currentFilePath = file.absolutePath

        try {
            recorderEngine.start(file.absolutePath)
            isRecording = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun handlePause() {
        if (isRecording) {
            recorderEngine.pause()
            isRecording = false
        }
    }

    private fun handleResume() {
        if (!isRecording) {
            recorderEngine.resume()
            isRecording = true
        }
    }

    private fun handleStop() {
        try {
            val result = recorderEngine.stop()
            scope.launch {
                val recording = Recording(
                    id = UUID.randomUUID().toString(),
                    title = "Recording ${System.currentTimeMillis()}",
                    filePath = result.filePath,
                    durationMs = result.durationMs,
                    sizeBytes = result.sizeBytes,
                    createdAt = System.currentTimeMillis(),
                    sourceType = SourceType.PHONE_MIC,
                    status = RecordingStatus.READY
                )
                repository.save(recording)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording cleanly", e)
            currentFilePath?.let { File(it).delete() }
        } finally {
            isRecording = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        unregisterReceiver(noisyReceiver)
        recorderEngine.release()
        scope.cancel()
    }

    private fun buildNotification(): Notification {
        val channelId = "recording_channel"
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(channelId) == null) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Recording", NotificationManager.IMPORTANCE_LOW)
            )
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Recording in progress")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val TAG = "RecordingService"
        const val ACTION_START = "com.audiopublisher.RECORD_START"
        const val ACTION_PAUSE = "com.audiopublisher.RECORD_PAUSE"
        const val ACTION_RESUME = "com.audiopublisher.RECORD_RESUME"
        const val ACTION_STOP = "com.audiopublisher.RECORD_STOP"
        private const val NOTIFICATION_ID = 1001
    }
}
