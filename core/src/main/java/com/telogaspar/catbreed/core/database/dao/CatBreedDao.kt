package com.telogaspar.catbreed.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity

@Dao
interface CatBreedDao {

    @Upsert
    suspend fun upsertBreeds(breeds: List<CatBreedEntity>)

    @Query("SELECT * FROM cat_breeds ORDER BY name LIMIT :limit OFFSET :offset")
    suspend fun getBreedsPage(limit: Int, offset: Int): List<CatBreedEntity>

    @Query("SELECT * FROM cat_breeds WHERE id = :id")
    suspend fun getBreedById(id: String): CatBreedEntity?
}
