package com.audiopublisher.features.recording;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class RecordingViewModel_Factory implements Factory<RecordingViewModel> {
  private final Provider<Context> contextProvider;

  public RecordingViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public RecordingViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static RecordingViewModel_Factory create(Provider<Context> contextProvider) {
    return new RecordingViewModel_Factory(contextProvider);
  }

  public static RecordingViewModel newInstance(Context context) {
    return new RecordingViewModel(context);
  }
}
