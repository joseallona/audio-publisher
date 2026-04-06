package com.audiopublisher.core.database.repository;

import com.audiopublisher.core.database.dao.RecordingDao;
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
public final class RecordingRepositoryImpl_Factory implements Factory<RecordingRepositoryImpl> {
  private final Provider<RecordingDao> daoProvider;

  public RecordingRepositoryImpl_Factory(Provider<RecordingDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RecordingRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static RecordingRepositoryImpl_Factory create(Provider<RecordingDao> daoProvider) {
    return new RecordingRepositoryImpl_Factory(daoProvider);
  }

  public static RecordingRepositoryImpl newInstance(RecordingDao dao) {
    return new RecordingRepositoryImpl(dao);
  }
}
