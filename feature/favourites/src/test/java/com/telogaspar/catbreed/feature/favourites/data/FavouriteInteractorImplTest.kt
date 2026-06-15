package com.telogaspar.catbreed.feature.favourites.data

import com.telogaspar.catbreed.feature.favourites.data.local.FavouriteInteractorImpl
import com.telogaspar.catbreed.feature.favourites.domain.FavouriteRepository
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

class FavouriteInteractorImplTest {

    private val repository: FavouriteRepository = mockk(relaxed = true)
    private val interactor = FavouriteInteractorImpl(repository)

    @Test
    fun `GIVEN a breedId WHEN addFavourite is called THEN delegates to repository`() = runTest {
        interactor.addFavourite("abys")

        coVerify(exactly = 1) { repository.addFavourite("abys") }
    }

    @Test
    fun `GIVEN a breedId WHEN removeFavourite is called THEN delegates to repository`() = runTest {
        interactor.removeFavourite("abys")

        coVerify(exactly = 1) { repository.removeFavourite("abys") }
    }

    @Test
    fun `GIVEN repository emits a set of ids WHEN getFavouriteIds is collected THEN passes through unchanged`() = runTest {
        every { repository.getFavouriteIds() } returns flowOf(setOf("abys", "aege"))

        val result = interactor.getFavouriteIds().first()

        assertEquals(setOf("abys", "aege"), result)
    }

    @Test
    fun `GIVEN repository emits empty set WHEN getFavouriteIds is collected THEN returns empty set`() = runTest {
        every { repository.getFavouriteIds() } returns flowOf(emptySet())

        val result = interactor.getFavouriteIds().first()

        assertEquals(emptySet(), result)
    }

    @Test
    fun `GIVEN repository emits multiple values WHEN getFavouriteIds collected THEN all emissions pass through`() = runTest {
        every { repository.getFavouriteIds() } returns flow {
            emit(setOf("abys"))
            emit(setOf("abys", "aege"))
        }

        val results = interactor.getFavouriteIds().toList()

        assertEquals(listOf(setOf("abys"), setOf("abys", "aege")), results)
    }
}
