package com.telogaspar.catbreed.breedList.data.repository

import com.telogaspar.catbreed.breedList.data.mapper.EventMapper
import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import com.telogaspar.catbreed.breedList.data.model.Image
import com.telogaspar.catbreed.breedList.data.model.Weight
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.breedList.domain.BreedException
import com.telogaspar.catbreed.core.database.dao.CatBreedDao
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
    private val mapper: EventMapper = mockk()
    private val catBreedDao: CatBreedDao = mockk(relaxed = true)
    private val repository = BreedListRepositoryImpl(remoteDataSource, mapper, catBreedDao)

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
    fun `GIVEN data source returns results WHEN fetchBreedList is called THEN upserts breeds to local db`() = runTest {
        val response = listOf(breedsResponse("1"))
        val breeds = listOf(breed("1"))
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } returns response
        every { mapper.map(response) } returns breeds

        repository.fetchBreedList(page = 0, limit = 15).toList()

        coVerify(exactly = 1) { catBreedDao.upsertBreeds(any()) }
    }

    @Test
    fun `GIVEN api fails and db has cached data WHEN fetchBreedList is called THEN emits cached breeds`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } throws RuntimeException("network down")
        coEvery { catBreedDao.getBreedsPage(limit = 15, offset = 0) } returns listOf(catBreedEntity("1"))

        val emissions = repository.fetchBreedList(page = 0, limit = 15).toList()

        assertEquals(1, emissions.size)
        assertEquals("1", emissions.first().first().breedId)
        verify(exactly = 0) { mapper.map(any()) }
    }

    @Test
    fun `GIVEN api fails and db is empty WHEN fetchBreedList is called THEN throws NetworkException`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } throws RuntimeException("network down")
        coEvery { catBreedDao.getBreedsPage(limit = 15, offset = 0) } returns emptyList()

        assertFailsWith<BreedException.NetworkException> {
            repository.fetchBreedList(page = 0, limit = 15).toList()
        }
    }

    @Test
    fun `GIVEN api returns empty and db is empty WHEN fetchBreedList is called THEN throws EmptyResultException`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } returns emptyList()
        coEvery { catBreedDao.getBreedsPage(limit = 15, offset = 0) } returns emptyList()

        assertFailsWith<BreedException.EmptyResultException> {
            repository.fetchBreedList(page = 0, limit = 15).toList()
        }
        verify(exactly = 0) { mapper.map(any()) }
    }

    @Test
    fun `GIVEN api returns empty and db has cached data WHEN fetchBreedList is called THEN emits cached breeds`() = runTest {
        coEvery { remoteDataSource.fetchBreedList(page = 0, limit = 15) } returns emptyList()
        coEvery { catBreedDao.getBreedsPage(limit = 15, offset = 0) } returns listOf(catBreedEntity("1"))

        val emissions = repository.fetchBreedList(page = 0, limit = 15).toList()

        assertEquals(1, emissions.size)
        assertEquals("1", emissions.first().first().breedId)
    }

    @Test
    fun `GIVEN breed exists in db WHEN fetchBreedById is called THEN emits that breed`() = runTest {
        coEvery { catBreedDao.getBreedById("abys") } returns catBreedEntity("abys")

        val emissions = repository.fetchBreedById("abys").toList()

        assertEquals(1, emissions.size)
        assertEquals("abys", emissions.first().breedId)
    }

    @Test
    fun `GIVEN breed does not exist in db WHEN fetchBreedById is called THEN throws NetworkException`() = runTest {
        coEvery { catBreedDao.getBreedById("unknown") } returns null

        assertFailsWith<BreedException.NetworkException> {
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
