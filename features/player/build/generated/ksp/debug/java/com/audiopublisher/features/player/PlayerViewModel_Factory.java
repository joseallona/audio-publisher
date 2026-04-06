package com.audiopublisher.features.player;

import com.audiopublisher.core.database.repository.RecordingRepository;
import com.audiopublisher.core.media.PlayerEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<RecordingRepository> repositoryProvider;

  private final Provider<PlayerEngine> playerEngineProvider;

  public PlayerViewModel_Factory(Provider<RecordingRepository> repositoryProvider,
      Provider<PlayerEngine> playerEngineProvider) {
    this.repositoryProvider = repositoryProvider;
    this.playerEngineProvider = playerEngineProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(repositoryProvider.get(), playerEngineProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<RecordingRepository> repositoryProvider,
      Provider<PlayerEngine> playerEngineProvider) {
    return new PlayerViewModel_Factory(repositoryProvider, playerEngineProvider);
  }

  public static PlayerViewModel newInstance(RecordingRepository repository,
      PlayerEngine playerEngine) {
    return new PlayerViewModel(repository, playerEngine);
  }
}
