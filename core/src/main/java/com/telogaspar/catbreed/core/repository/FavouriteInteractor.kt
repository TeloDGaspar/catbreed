package com.telogaspar.catbreed.core.repository

import kotlinx.coroutines.flow.Flow

interface FavouriteInteractor {
    fun getFavouriteIds(): Flow<Set<String>>
    suspend fun addFavourite(breedId: String)
    suspend fun removeFavourite(breedId: String)
}
