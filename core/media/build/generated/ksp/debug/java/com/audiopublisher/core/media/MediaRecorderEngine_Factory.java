package com.audiopublisher.core.media;

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
public final class MediaRecorderEngine_Factory implements Factory<MediaRecorderEngine> {
  private final Provider<Context> contextProvider;

  public MediaRecorderEngine_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MediaRecorderEngine get() {
    return newInstance(contextProvider.get());
  }

  public static MediaRecorderEngine_Factory create(Provider<Context> contextProvider) {
    return new MediaRecorderEngine_Factory(contextProvider);
  }

  public static MediaRecorderEngine newInstance(Context context) {
    return new MediaRecorderEngine(context);
  }
}
