package com.audiopublisher.core.media.di

import com.audiopublisher.core.media.MediaPlayerEngine
import com.audiopublisher.core.media.MediaRecorderEngine
import com.audiopublisher.core.media.PlayerEngine
import com.audiopublisher.core.media.RecorderEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindRecorderEngine(impl: MediaRecorderEngine): RecorderEngine

    @Binds
    @Singleton
    abstract fun bindPlayerEngine(impl: MediaPlayerEngine): PlayerEngine
}
