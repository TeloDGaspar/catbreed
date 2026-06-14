package com.telogaspar.catbreed.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Upsert
    suspend fun insertFavourite(favourite: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE breedId = :breedId")
    suspend fun deleteFavourite(breedId: String)

    @Query("SELECT breedId FROM favourites")
    fun getFavouriteIds(): Flow<List<String>>

    @Query("""
        SELECT b.* FROM cat_breeds b
        INNER JOIN favourites f ON b.id = f.breedId
        ORDER BY b.name
    """)
    fun getFavouriteBreeds(): Flow<List<CatBreedEntity>>
}
