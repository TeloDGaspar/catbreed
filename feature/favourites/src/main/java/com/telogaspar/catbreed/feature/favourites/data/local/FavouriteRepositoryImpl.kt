package com.telogaspar.catbreed.feature.favourites.data.local

import com.telogaspar.catbreed.core.database.dao.FavouriteDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity
import com.telogaspar.catbreed.feature.favourites.domain.FavouriteRepository
import com.telogaspar.catbreed.feature.favourites.domain.model.FavouriteBreed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val dao: FavouriteDao,
) : FavouriteRepository {

    override fun getFavouriteBreeds(): Flow<List<FavouriteBreed>> =
        dao.getFavouriteBreeds().map { entities -> entities.map { it.toDomain() } }

    override fun getFavouriteIds(): Flow<Set<String>> =
        dao.getFavouriteIds().map { it.toSet() }

    override suspend fun addFavourite(breedId: String) {
        dao.insertFavourite(FavouriteEntity(breedId))
    }

    override suspend fun removeFavourite(breedId: String) {
        dao.deleteFavourite(breedId)
    }

    private fun CatBreedEntity.toDomain() = FavouriteBreed(
        id = id,
        name = name,
        origin = origin,
        imageUrl = imageUrl,
        lifeSpan = lifeSpan,
    )
}