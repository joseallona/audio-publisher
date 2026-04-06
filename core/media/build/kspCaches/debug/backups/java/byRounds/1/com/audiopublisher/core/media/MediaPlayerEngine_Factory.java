package com.audiopublisher.core.media;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class MediaPlayerEngine_Factory implements Factory<MediaPlayerEngine> {
  @Override
  public MediaPlayerEngine get() {
    return newInstance();
  }

  public static MediaPlayerEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MediaPlayerEngine newInstance() {
    return new MediaPlayerEngine();
  }

  private static final class InstanceHolder {
    private static final MediaPlayerEngine_Factory INSTANCE = new MediaPlayerEngine_Factory();
  }
}
