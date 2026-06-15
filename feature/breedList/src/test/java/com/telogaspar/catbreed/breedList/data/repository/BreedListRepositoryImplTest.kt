package com.telogaspar.catbreed.breedList.data.repository

import com.telogaspar.catbreed.breedList.data.local.BreedLocalDataSource
import com.telogaspar.catbreed.breedList.data.mapper.EventMapper
import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import com.telogaspar.catbreed.breedList.data.model.Image
import com.telogaspar.catbreed.breedList.data.model.Weight
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.domain.model.Breed
import com.telogaspar.catbreed.breedList.domain.exception.BreedException
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class BreedListRepositoryImplTest {

    private val remoteDataSource: BreedEventListRemoteDataSource = mockk()
    private val localDataSource: BreedLocalDataSource = mockk(relaxed = true)
    private val mapper: EventMapper = mockk()
    private val repository = BreedListRepositoryImpl(remoteDataSource, localDataSource, mapper)

    @Test
    fun `GIVEN data source returns results WHEN fetchBreedList is called THEN emits mapped breeds`() = runTest {
        val response = listOf(breedsResponse("1"))
        val breeds = listOf(breed("1"))
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } returns response
        every { mapper.map(response) } returns breeds

        val emissions = repository.fetchBreedList(page = 0, limit = 15).toList()

        assertEquals(listOf(breeds), emissions)
    }

    @Test
    fun `GIVEN data source returns results WHEN fetchBreedList is called THEN upserts breeds to local data source`() = runTest {
        val response = listOf(breedsResponse("1"))
        val breeds = listOf(breed("1"))
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } returns response
        every { mapper.map(response) } returns breeds

        repository.fetchBreedList(page = 0, limit = 15).toList()

        coVerify(exactly = 1) { localDataSource.upsertBreeds(any()) }
    }

    @Test
    fun `GIVEN api fails and cache has data WHEN fetchBreedList is called THEN emits cached breeds`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } throws RuntimeException("network down")
        coEvery { localDataSource.getBreedsPage(limit = 15, offset = 0) } returns listOf(catBreedEntity("1"))

        val emissions = repository.fetchBreedList(page = 0, limit = 15).toList()

        assertEquals(1, emissions.size)
        assertEquals("1", emissions.first().first().breedId)
        verify(exactly = 0) { mapper.map(any()) }
    }

    @Test
    fun `GIVEN api fails and cache is empty WHEN fetchBreedList is called THEN throws NetworkException`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } throws RuntimeException("network down")
        coEvery { localDataSource.getBreedsPage(limit = 15, offset = 0) } returns emptyList()

        assertFailsWith<BreedException.NetworkException> {
            repository.fetchBreedList(page = 0, limit = 15).toList()
        }
    }

    @Test
    fun `GIVEN api returns empty and cache is empty WHEN fetchBreedList is called THEN throws EmptyResultException`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } returns emptyList()
        coEvery { localDataSource.getBreedsPage(limit = 15, offset = 0) } returns emptyList()

        assertFailsWith<BreedException.EmptyResultException> {
            repository.fetchBreedList(page = 0, limit = 15).toList()
        }
        verify(exactly = 0) { mapper.map(any()) }
    }

    @Test
    fun `GIVEN api returns empty and cache has data WHEN fetchBreedList is called THEN emits cached breeds`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } returns emptyList()
        coEvery { localDataSource.getBreedsPage(limit = 15, offset = 0) } returns listOf(catBreedEntity("1"))

        val emissions = repository.fetchBreedList(page = 0, limit = 15).toList()

        assertEquals(1, emissions.size)
        assertEquals("1", emissions.first().first().breedId)
    }

    @Test
    fun `GIVEN breed exists in cache WHEN fetchBreedById is called THEN emits that breed`() = runTest {
        coEvery { localDataSource.getBreedById("abys") } returns catBreedEntity("abys")

        val emissions = repository.fetchBreedById("abys").toList()

        assertEquals(1, emissions.size)
        assertEquals("abys", emissions.first().breedId)
    }

    @Test
    fun `GIVEN breed does not exist in cache WHEN fetchBreedById is called THEN throws NotFoundException`() = runTest {
        coEvery { localDataSource.getBreedById("unknown") } returns null

        assertFailsWith<BreedException.NotFoundException> {
            repository.fetchBreedById("unknown").toList()
        }
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
        weightMetric = "3 - 5",
    )

    private fun catBreedEntity(id: String) = CatBreedEntity(
        id = id,
        name = "Breed $id",
        imageUrl = "https://cat/$id.png",
        lifeSpan = "10 - 15",
        origin = "Origin $id",
        temperament = "Active",
        description = "description-$id",
        weightMetric = "3 - 5",
    )
}
