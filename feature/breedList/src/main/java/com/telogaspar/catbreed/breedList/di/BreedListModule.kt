package com.telogaspar.catbreed.breedList.di

import com.telogaspar.catbreed.breedList.data.api.BreedsEventApi
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSourceImpl
import com.telogaspar.catbreed.breedList.data.repository.BreedListRepositoryImpl
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BreedListModule {

    @Binds
    @Singleton
    internal abstract fun bindBreedEventListRemoteDataSource(
        impl: BreedEventListRemoteDataSourceImpl
    ): BreedEventListRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindBreedListRepository(
        impl: BreedListRepositoryImpl
    ): BreedListRepository

    companion object {
        @Provides
        @Singleton
        fun provideBreedsEventApi(retrofit: Retrofit): BreedsEventApi =
            retrofit.create(BreedsEventApi::class.java)
    }
}
