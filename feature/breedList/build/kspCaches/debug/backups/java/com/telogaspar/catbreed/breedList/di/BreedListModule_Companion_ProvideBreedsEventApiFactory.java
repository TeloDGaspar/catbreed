package com.telogaspar.catbreed.breedList.di;

import com.telogaspar.catbreed.breedList.data.api.BreedsEventApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BreedListModule_Companion_ProvideBreedsEventApiFactory implements Factory<BreedsEventApi> {
  private final Provider<Retrofit> retrofitProvider;

  private BreedListModule_Companion_ProvideBreedsEventApiFactory(
      Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public BreedsEventApi get() {
    return provideBreedsEventApi(retrofitProvider.get());
  }

  public static BreedListModule_Companion_ProvideBreedsEventApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new BreedListModule_Companion_ProvideBreedsEventApiFactory(retrofitProvider);
  }

  public static BreedsEventApi provideBreedsEventApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(BreedListModule.Companion.provideBreedsEventApi(retrofit));
  }
}
