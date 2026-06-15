package com.telogaspar.catbreed.feature.favourites.data.local

import com.telogaspar.catbreed.core.repository.FavouriteInteractor
import com.telogaspar.catbreed.feature.favourites.domain.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavouriteInteractorImpl @Inject constructor(
    private val repository: FavouriteRepository,
) : FavouriteInteractor {

    override fun getFavouriteIds(): Flow<Set<String>> = repository.getFavouriteIds()

    override suspend fun addFavourite(breedId: String) = repository.addFavourite(breedId)

    override suspend fun removeFavourite(breedId: String) = repository.removeFavourite(breedId)
}
