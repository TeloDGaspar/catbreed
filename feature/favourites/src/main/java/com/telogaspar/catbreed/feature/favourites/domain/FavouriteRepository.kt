package com.telogaspar.catbreed.feature.favourites.domain

import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.repository.FavouriteInteractor
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository : FavouriteInteractor {
    fun getFavouriteBreeds(): Flow<List<CatBreedEntity>>
}
