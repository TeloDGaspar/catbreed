package com.telogaspar.catbreed.feature.favourites.domain

import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun getFavouriteBreeds(): Flow<List<CatBreedEntity>>
    fun getFavouriteIds(): Flow<Set<String>>
    suspend fun addFavourite(breedId: String)
    suspend fun removeFavourite(breedId: String)
}
