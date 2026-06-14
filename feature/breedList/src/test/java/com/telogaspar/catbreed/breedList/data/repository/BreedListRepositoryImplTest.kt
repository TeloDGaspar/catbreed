package com.telogaspar.catbreed.breedList.data.repository

import com.telogaspar.catbreed.breedList.data.mapper.EventMapper
import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import com.telogaspar.catbreed.breedList.data.model.Image
import com.telogaspar.catbreed.breedList.data.model.Weight
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.domain.Breed
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreedListRepositoryImplTest {

    private val remoteDataSource: BreedEventListRemoteDataSource = mockk()
    private val mapper: EventMapper = mockk()
    private val repository = BreedListRepositoryImpl(remoteDataSource, mapper)

    @Test
    fun `GIVEN data source returns results WHEN fetchBreedList is called THEN emits mapped breeds`() = runTest {
        val response = listOf(breedsResponse("1"))
        val breeds = listOf(breed("1"))
        coEvery { remoteDataSource.fetchBreedList() } returns response
        every { mapper.map(response) } returns breeds

        val emissions = repository.fetchBreedList().toList()

        assertEquals(listOf(breeds), emissions)
    }

    @Test
    fun `GIVEN data source returns empty list WHEN fetchBreedList is called THEN emits nothing and mapper is not invoked`() = runTest {
        coEvery { remoteDataSource.fetchBreedList() } returns emptyList()

        val emissions = repository.fetchBreedList().toList()

        assertTrue(emissions.isEmpty())
        verify(exactly = 0) { mapper.map(any()) }
    }

    @Test
    fun `GIVEN data source throws an exception WHEN fetchBreedList is called THEN emits nothing and mapper is not invoked`() = runTest {
        coEvery { remoteDataSource.fetchBreedList() } throws RuntimeException("network down")

        val emissions = repository.fetchBreedList().toList()

        assertTrue(emissions.isEmpty())
        verify(exactly = 0) { mapper.map(any()) }
    }

    private fun breedsResponse(id: String) = BreedsResponse(
        description = "description-$id",
        id = id,
        image = Image(height = 100, id = "img-$id", url = "https://cat/$id.png", width = 100),
        life_span = "10 - 15",
        name = "Breed $id",
        origin = "Origin $id",
        temperament = "Active",
        weight = Weight(imperial = "7 - 10", metric = "3 - 5"),
    )

    private fun breed(id: String) = Breed(
        breedId = id,
        breedName = "Breed $id",
        imageUrl = "https://cat/$id.png",
        lifeSpan = "10 - 15",
        origin = "Origin $id",
        temperament = "Active",
        description = "description-$id",
    )
}
