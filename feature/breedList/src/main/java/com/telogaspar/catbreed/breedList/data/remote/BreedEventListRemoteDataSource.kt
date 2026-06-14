package com.telogaspar.catbreed.breedList.data.remote

import com.telogaspar.catbreed.breedList.data.model.BreedsResponse

internal interface BreedEventListRemoteDataSource {

    suspend fun fetchBreedList(): List<BreedsResponse>
}