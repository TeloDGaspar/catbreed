package com.telogaspar.catbreed.breedList.data.repository

import com.telogaspar.catbreed.breedList.data.local.BreedLocalDataSource
import com.telogaspar.catbreed.breedList.data.mapper.EventMapper
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.breedList.domain.BreedException
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class BreedListRepositoryImpl @Inject constructor(
    private val remoteDataSource: BreedEventListRemoteDataSource,
    private val localDataSource: BreedLocalDataSource,
    private val mapper: EventMapper,
) : BreedListRepository {

    override fun fetchBreedList(page: Int, limit: Int): Flow<List<Breed>> = flow {
        val response = remoteDataSource.fetchBreedList(page = page, limit = limit)
        if (response.isEmpty()) throw BreedException.EmptyResultException()
        val breeds = mapper.map(response)
        localDataSource.upsertBreeds(breeds.map { it.toEntity() })
        emit(breeds)
    }.catch { e ->
        val cached = localDataSource.getBreedsPage(limit = limit, offset = page * limit)
        if (cached.isNotEmpty()) {
            emit(cached.map { it.toDomain() })
        } else {
            throw e as? BreedException ?: BreedException.NetworkException(e)
        }
    }.flowOn(Dispatchers.IO)

    override fun fetchBreedById(id: String): Flow<Breed> = flow {
        val entity = localDataSource.getBreedById(id)
            ?: throw BreedException.NetworkException(NoSuchElementException("Breed $id not found"))
        emit(entity.toDomain())
    }

    private fun CatBreedEntity.toDomain() = Breed(
        breedId = id,
        breedName = name,
        origin = origin,
        temperament = temperament,
        description = description,
        lifeSpan = lifeSpan,
        imageUrl = imageUrl,
        weightMetric = weightMetric,
    )

    private fun Breed.toEntity() = CatBreedEntity(
        id = breedId,
        name = breedName,
        origin = origin.orEmpty(),
        temperament = temperament.orEmpty(),
        description = description.orEmpty(),
        lifeSpan = lifeSpan.orEmpty(),
        imageUrl = imageUrl,
        weightMetric = weightMetric.orEmpty(),
    )
}
