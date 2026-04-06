package com.audiopublisher.features.recording.service;

import com.audiopublisher.core.database.repository.RecordingRepository;
import com.audiopublisher.core.media.RecorderEngine;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class RecordingService_MembersInjector implements MembersInjector<RecordingService> {
  private final Provider<RecorderEngine> recorderEngineProvider;

  private final Provider<RecordingRepository> repositoryProvider;

  public RecordingService_MembersInjector(Provider<RecorderEngine> recorderEngineProvider,
      Provider<RecordingRepository> repositoryProvider) {
    this.recorderEngineProvider = recorderEngineProvider;
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<RecordingService> create(
      Provider<RecorderEngine> recorderEngineProvider,
      Provider<RecordingRepository> repositoryProvider) {
    return new RecordingService_MembersInjector(recorderEngineProvider, repositoryProvider);
  }

  @Override
  public void injectMembers(RecordingService instance) {
    injectRecorderEngine(instance, recorderEngineProvider.get());
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.audiopublisher.features.recording.service.RecordingService.recorderEngine")
  public static void injectRecorderEngine(RecordingService instance,
      RecorderEngine recorderEngine) {
    instance.recorderEngine = recorderEngine;
  }

  @InjectedFieldSignature("com.audiopublisher.features.recording.service.RecordingService.repository")
  public static void injectRepository(RecordingService instance, RecordingRepository repository) {
    instance.repository = repository;
  }
}
