package com.telogaspar.catbreed.breedList.domain

import kotlinx.coroutines.flow.Flow

interface BreedListRepository {
    fun fetchBreedList(page: Int, limit: Int): Flow<List<Breed>>
    fun fetchBreedById(id: String): Flow<Breed>
}

data class Breed(
    val breedId: String,
    val breedName: String,
    val imageUrl: String?,
    val lifeSpan: String?,
    val origin: String?,
    val temperament: String?,
    val description: String?,
    val weightMetric: String?,
)