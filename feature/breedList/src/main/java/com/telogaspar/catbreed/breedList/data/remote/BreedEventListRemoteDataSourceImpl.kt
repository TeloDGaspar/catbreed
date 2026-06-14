package com.telogaspar.catbreed.breedList.data.remote

import com.telogaspar.catbreed.breedList.data.api.BreedsEventApi
import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import javax.inject.Inject

internal class BreedEventListRemoteDataSourceImpl @Inject constructor(
    private val breedsEventApi: BreedsEventApi
) : BreedEventListRemoteDataSource {
    override suspend fun fetchBreedList(): List<BreedsResponse> {
        return breedsEventApi.getBreeds()
    }
}