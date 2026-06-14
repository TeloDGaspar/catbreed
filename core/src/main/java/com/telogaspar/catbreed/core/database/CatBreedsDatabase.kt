package com.telogaspar.catbreed.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.telogaspar.catbreed.core.database.dao.CatBreedDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity

@Database(
    entities = [CatBreedEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class CatBreedsDatabase : RoomDatabase() {
    abstract fun catBreedDao(): CatBreedDao
}
