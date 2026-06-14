package com.telogaspar.catbreed.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.telogaspar.catbreed.core.database.dao.CatBreedDao
import com.telogaspar.catbreed.core.database.dao.FavouriteDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity

@Database(
    entities = [CatBreedEntity::class, FavouriteEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class CatBreedsDatabase : RoomDatabase() {
    abstract fun catBreedDao(): CatBreedDao
    abstract fun favouriteDao(): FavouriteDao
}
