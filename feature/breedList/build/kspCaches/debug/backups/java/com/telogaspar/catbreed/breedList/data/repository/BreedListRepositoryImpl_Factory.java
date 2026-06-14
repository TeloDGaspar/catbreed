package com.telogaspar.catbreed.breedList.data.repository;

import com.telogaspar.catbreed.breedList.data.mapper.EventMapper;
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class BreedListRepositoryImpl_Factory implements Factory<BreedListRepositoryImpl> {
  private final Provider<BreedEventListRemoteDataSource> remoteDataSourceProvider;

  private final Provider<EventMapper> mapperProvider;

  private BreedListRepositoryImpl_Factory(
      Provider<BreedEventListRemoteDataSource> remoteDataSourceProvider,
      Provider<EventMapper> mapperProvider) {
    this.remoteDataSourceProvider = remoteDataSourceProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public BreedListRepositoryImpl get() {
    return newInstance(remoteDataSourceProvider.get(), mapperProvider.get());
  }

  public static BreedListRepositoryImpl_Factory create(
      Provider<BreedEventListRemoteDataSource> remoteDataSourceProvider,
      Provider<EventMapper> mapperProvider) {
    return new BreedListRepositoryImpl_Factory(remoteDataSourceProvider, mapperProvider);
  }

  public static BreedListRepositoryImpl newInstance(BreedEventListRemoteDataSource remoteDataSource,
      EventMapper mapper) {
    return new BreedListRepositoryImpl(remoteDataSource, mapper);
  }
}
