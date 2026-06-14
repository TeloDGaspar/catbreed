package com.telogaspar.catbreed.feature.favourites.di

import com.telogaspar.catbreed.feature.favourites.data.FavouriteRepositoryImpl
import com.telogaspar.catbreed.feature.favourites.domain.FavouriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavouritesModule {

    @Binds
    @Singleton
    abstract fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository
}
