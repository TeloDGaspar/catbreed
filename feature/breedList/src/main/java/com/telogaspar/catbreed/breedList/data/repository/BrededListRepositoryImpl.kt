package com.telogaspar.catbreed.breedList.data.repository

import com.telogaspar.catbreed.breedList.data.mapper.EventMapper
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.telogaspar.catbreed.breedList.domain.Breed
import javax.inject.Inject

internal class BreedListRepositoryImpl @Inject constructor(
    private val remoteDataSource: BreedEventListRemoteDataSource,
    private val mapper: EventMapper,
) : BreedListRepository {

    override fun fetchBreedList(): Flow<List<Breed>> = flow {
        try {
            // Try fetching from API
            val sportsResponse = remoteDataSource.fetchBreedList()
            if (sportsResponse.isNotEmpty()) {
                val mappedSports = mapper.map(sportsResponse)


                emit(mappedSports)
                return@flow
            }
        } catch (e: Exception) {

        }
    }
}