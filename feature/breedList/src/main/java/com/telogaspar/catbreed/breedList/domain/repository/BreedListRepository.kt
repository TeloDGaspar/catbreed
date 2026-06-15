package com.telogaspar.catbreed.breedList.domain.repository

import com.telogaspar.catbreed.breedList.domain.model.Breed
import kotlinx.coroutines.flow.Flow

interface BreedListRepository {
    fun fetchBreedList(page: Int, limit: Int): Flow<List<Breed>>
    fun fetchBreedById(id: String): Flow<Breed>
}