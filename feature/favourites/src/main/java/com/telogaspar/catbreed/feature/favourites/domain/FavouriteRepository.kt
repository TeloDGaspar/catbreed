package com.telogaspar.catbreed.feature.favourites.domain

import com.telogaspar.catbreed.core.repository.FavouriteInteractor
import com.telogaspar.catbreed.feature.favourites.domain.model.FavouriteBreed
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository : FavouriteInteractor {
    fun getFavouriteBreeds(): Flow<List<FavouriteBreed>>
}
