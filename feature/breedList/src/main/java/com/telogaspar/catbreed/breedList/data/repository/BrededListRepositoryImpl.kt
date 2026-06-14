package com.telogaspar.catbreed.breedList.data.repository

import com.telogaspar.catbreed.breedList.data.mapper.EventMapper
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.breedList.domain.BreedException
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import com.telogaspar.catbreed.core.database.dao.CatBreedDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class BreedListRepositoryImpl @Inject constructor(
    private val remoteDataSource: BreedEventListRemoteDataSource,
    private val mapper: EventMapper,
    private val catBreedDao: CatBreedDao
) : BreedListRepository {

    override fun fetchBreedList(): Flow<List<Breed>> = flow {
        try {
            val response = remoteDataSource.fetchBreedList()
            if (response.isEmpty()) throw BreedException.EmptyResultException()
            val breeds = mapper.map(response)
            catBreedDao.upsertBreeds(breeds.map { it.toEntity() })
            emit(breeds)
        } catch (e: Exception) {
            val cached = catBreedDao.getBreedsPage(limit = 50, offset = 0)
            if (cached.isNotEmpty()) {
                emit(cached.map { it.toDomain() })
            } else {
                throw e as? BreedException ?: BreedException.NetworkException(e)
            }
        }
    }

    private fun CatBreedEntity.toDomain() = Breed(
        breedId = id,
        breedName = name,
        origin = origin,
        temperament = temperament,
        description = description,
        lifeSpan = lifeSpan,
        imageUrl = imageUrl,
    )

    private fun Breed.toEntity() = CatBreedEntity(
        id = breedId,
        name = breedName,
        origin = origin.orEmpty(),
        temperament = temperament.orEmpty(),
        description = description.orEmpty(),
        lifeSpan = lifeSpan.orEmpty(),
        imageUrl = imageUrl,
    )
}
