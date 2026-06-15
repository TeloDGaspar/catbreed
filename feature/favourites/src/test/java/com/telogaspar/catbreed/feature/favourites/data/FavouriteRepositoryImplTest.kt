package com.telogaspar.catbreed.feature.favourites.data

import com.telogaspar.catbreed.core.database.dao.FavouriteDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity
import com.telogaspar.catbreed.feature.favourites.data.local.FavouriteRepositoryImpl
import com.telogaspar.catbreed.feature.favourites.domain.model.FavouriteBreed
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class FavouriteRepositoryImplTest {

    private val dao: FavouriteDao = mockk(relaxed = true)
    private val repository = FavouriteRepositoryImpl(dao)

    @Test
    fun `GIVEN a breedId WHEN addFavourite is called THEN delegates to dao insertFavourite`() = runTest {
        repository.addFavourite("abys")

        coVerify(exactly = 1) { dao.insertFavourite(FavouriteEntity("abys")) }
    }

    @Test
    fun `GIVEN a breedId WHEN removeFavourite is called THEN delegates to dao deleteFavourite`() = runTest {
        repository.removeFavourite("abys")

        coVerify(exactly = 1) { dao.deleteFavourite("abys") }
    }

    @Test
    fun `GIVEN dao emits a list of ids WHEN getFavouriteIds is called THEN returns a set`() = runTest {
        every { dao.getFavouriteIds() } returns flowOf(listOf("abys", "aege", "abys"))

        val result = repository.getFavouriteIds().first()

        assertEquals(setOf("abys", "aege"), result)
    }

    @Test
    fun `GIVEN dao emits favourite breeds WHEN getFavouriteBreeds is called THEN maps them to domain model`() = runTest {
        val entity = CatBreedEntity(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active",
            description = "Energetic cat",
            lifeSpan = "14 - 15",
            imageUrl = null,
            weightMetric = "3 - 5",
        )
        every { dao.getFavouriteBreeds() } returns flowOf(listOf(entity))

        val result = repository.getFavouriteBreeds().first()

        val expected = FavouriteBreed(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            imageUrl = null,
            lifeSpan = "14 - 15",
        )
        assertEquals(listOf(expected), result)
    }

    @Test
    fun `GIVEN dao emits empty list WHEN getFavouriteIds is called THEN returns empty set`() = runTest {
        every { dao.getFavouriteIds() } returns flowOf(emptyList())

        val result = repository.getFavouriteIds().first()

        assertEquals(emptySet(), result)
    }

    @Test
    fun `GIVEN dao emits empty list WHEN getFavouriteBreeds is called THEN returns empty list`() = runTest {
        every { dao.getFavouriteBreeds() } returns flowOf(emptyList())

        val result = repository.getFavouriteBreeds().first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `GIVEN dao emits multiple values WHEN getFavouriteIds collected THEN each emission is mapped to set`() = runTest {
        every { dao.getFavouriteIds() } returns flow {
            emit(listOf("abys"))
            emit(listOf("abys", "aege"))
        }

        val results = repository.getFavouriteIds().toList()

        assertEquals(listOf(setOf("abys"), setOf("abys", "aege")), results)
    }
}
