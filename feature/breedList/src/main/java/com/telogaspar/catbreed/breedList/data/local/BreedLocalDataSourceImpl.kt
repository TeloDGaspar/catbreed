package com.telogaspar.catbreed.breedList.data.local

import com.telogaspar.catbreed.core.database.dao.CatBreedDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import javax.inject.Inject

internal class BreedLocalDataSourceImpl @Inject constructor(
    private val dao: CatBreedDao,
) : BreedLocalDataSource {

    override suspend fun upsertBreeds(breeds: List<CatBreedEntity>) =
        dao.upsertBreeds(breeds)

    override suspend fun getBreedsPage(limit: Int, offset: Int): List<CatBreedEntity> =
        dao.getBreedsPage(limit = limit, offset = offset)

    override suspend fun getBreedById(id: String): CatBreedEntity? =
        dao.getBreedById(id)
}
