package com.telogaspar.catbreed.breedList.data.mapper;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class EventMapper_Factory implements Factory<EventMapper> {
  @Override
  public EventMapper get() {
    return newInstance();
  }

  public static EventMapper_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EventMapper newInstance() {
    return new EventMapper();
  }

  private static final class InstanceHolder {
    static final EventMapper_Factory INSTANCE = new EventMapper_Factory();
  }
}
