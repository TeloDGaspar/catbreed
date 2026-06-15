package com.telogaspar.catbreed.breedList.data.local

import com.telogaspar.catbreed.core.database.entity.CatBreedEntity

internal interface BreedLocalDataSource {
    suspend fun upsertBreeds(breeds: List<CatBreedEntity>)
    suspend fun getBreedsPage(limit: Int, offset: Int): List<CatBreedEntity>
    suspend fun getBreedById(id: String): CatBreedEntity?
}
