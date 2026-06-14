package com.telogaspar.catbreed.breedList.data.api

import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface BreedsEventApi {
    @GET("v1/breeds")
    suspend fun getBreeds(
        @Query("limit") limit: Int = 50
    ): List<BreedsResponse>
}