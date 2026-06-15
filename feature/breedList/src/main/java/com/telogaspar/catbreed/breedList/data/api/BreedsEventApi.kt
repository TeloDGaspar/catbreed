package com.telogaspar.catbreed.breedList.data.api

import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface BreedsEventApi {
    @GET("breeds")
    suspend fun getBreeds(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15,
    ): List<BreedsResponse>
}