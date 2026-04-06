package com.audiopublisher.core.database.di

import android.content.Context
import androidx.room.Room
import com.audiopublisher.core.database.AppDatabase
import com.audiopublisher.core.database.dao.RecordingDao
import com.audiopublisher.core.database.repository.RecordingRepository
import com.audiopublisher.core.database.repository.RecordingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "audio_publisher.db").build()

    @Provides
    fun provideRecordingDao(db: AppDatabase): RecordingDao = db.recordingDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecordingRepository(impl: RecordingRepositoryImpl): RecordingRepository
}
