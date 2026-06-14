package com.telogaspar.catbreed.core.database

import android.content.Context
import androidx.room.Room
import com.telogaspar.catbreed.core.database.dao.CatBreedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCatBreedsDatabase(@ApplicationContext context: Context): CatBreedsDatabase =
        Room.databaseBuilder(
            context,
            CatBreedsDatabase::class.java,
            "cat_breeds.db",
        ).build()

    @Provides
    @Singleton
    fun provideCatBreedDao(database: CatBreedsDatabase): CatBreedDao =
        database.catBreedDao()
}
