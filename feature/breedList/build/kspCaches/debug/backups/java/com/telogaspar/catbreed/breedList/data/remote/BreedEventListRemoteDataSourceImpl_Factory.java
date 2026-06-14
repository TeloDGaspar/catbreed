package com.telogaspar.catbreed.breedList.data.remote;

import com.telogaspar.catbreed.breedList.data.api.BreedsEventApi;
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
public final class BreedEventListRemoteDataSourceImpl_Factory implements Factory<BreedEventListRemoteDataSourceImpl> {
  private final Provider<BreedsEventApi> breedsEventApiProvider;

  private BreedEventListRemoteDataSourceImpl_Factory(
      Provider<BreedsEventApi> breedsEventApiProvider) {
    this.breedsEventApiProvider = breedsEventApiProvider;
  }

  @Override
  public BreedEventListRemoteDataSourceImpl get() {
    return newInstance(breedsEventApiProvider.get());
  }

  public static BreedEventListRemoteDataSourceImpl_Factory create(
      Provider<BreedsEventApi> breedsEventApiProvider) {
    return new BreedEventListRemoteDataSourceImpl_Factory(breedsEventApiProvider);
  }

  public static BreedEventListRemoteDataSourceImpl newInstance(BreedsEventApi breedsEventApi) {
    return new BreedEventListRemoteDataSourceImpl(breedsEventApi);
  }
}
