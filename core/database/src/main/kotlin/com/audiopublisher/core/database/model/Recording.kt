package com.audiopublisher.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey val id: String,
    val title: String,
    val filePath: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val createdAt: Long,
    val sourceType: SourceType,
    val status: RecordingStatus
)

enum class SourceType {
    PHONE_MIC, WIRED_HEADSET, BLUETOOTH, UNKNOWN
}

enum class RecordingStatus {
    DRAFT, READY, DELETED
}
