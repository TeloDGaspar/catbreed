package com.telogaspar.catbreed.breedList.presentation

import androidx.lifecycle.SavedStateHandle
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import com.telogaspar.catbreed.core.repository.FavouriteInteractor
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BreedDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository: BreedListRepository = mockk()
    private val favouriteInteractor: FavouriteInteractor = mockk(relaxed = true) {
        every { getFavouriteIds() } returns flowOf(emptySet())
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ──────────────────────────────────────────────────────────

    @Test
    fun `GIVEN viewModel is created WHEN loading starts THEN isLoading is true`() = runTest {
        every { repository.fetchBreedById("abys") } returns flowOf(breed())

        val viewModel = viewModel("abys")

        assertTrue(viewModel.uiState.value.isLoading)
    }

    // ── Success ────────────────────────────────────────────────────────────────

    @Test
    fun `GIVEN repository returns breed WHEN viewModel is created THEN state contains breed`() = runTest {
        val expected = breed()
        every { repository.fetchBreedById("abys") } returns flowOf(expected)

        val viewModel = viewModel("abys")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(expected, state.breed)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `GIVEN repository returns breed WHEN loaded THEN uses breedId from SavedStateHandle`() = runTest {
        val breed = breed(id = "beng")
        every { repository.fetchBreedById("beng") } returns flowOf(breed)

        val viewModel = viewModel("beng")
        advanceUntilIdle()

        assertEquals("beng", viewModel.uiState.value.breed?.breedId)
    }

    // ── Error ──────────────────────────────────────────────────────────────────

    @Test
    fun `GIVEN repository throws WHEN loading THEN error message is shown`() = runTest {
        every { repository.fetchBreedById("abys") } returns flow {
            throw RuntimeException("breed not found")
        }

        val viewModel = viewModel("abys")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("breed not found", state.error)
        assertFalse(state.isLoading)
        assertNull(state.breed)
    }

    @Test
    fun `GIVEN repository throws WHEN loading THEN breed remains null`() = runTest {
        every { repository.fetchBreedById("abys") } returns flow {
            throw IllegalStateException("db error")
        }

        val viewModel = viewModel("abys")
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.breed)
    }

    // ── Favourites ───────────────────────────────────────────────────────────────

    @Test
    fun `GIVEN breed is in favourites WHEN viewModel is created THEN isFavourite is true`() = runTest {
        every { repository.fetchBreedById("abys") } returns flowOf(breed())
        every { favouriteInteractor.getFavouriteIds() } returns flowOf(setOf("abys"))

        val viewModel = viewModel("abys")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFavourite)
    }

    @Test
    fun `GIVEN breed is not favourited WHEN viewModel is created THEN isFavourite is false`() = runTest {
        every { repository.fetchBreedById("abys") } returns flowOf(breed())
        every { favouriteInteractor.getFavouriteIds() } returns flowOf(setOf("beng"))

        val viewModel = viewModel("abys")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavourite)
    }

    @Test
    fun `GIVEN breed is not favourited WHEN toggleFavourite is called THEN it is added`() = runTest {
        every { repository.fetchBreedById("abys") } returns flowOf(breed())
        every { favouriteInteractor.getFavouriteIds() } returns flowOf(emptySet())

        val viewModel = viewModel("abys")
        advanceUntilIdle()
        viewModel.toggleFavourite()
        advanceUntilIdle()

        coVerify(exactly = 1) { favouriteInteractor.addFavourite("abys") }
    }

    @Test
    fun `GIVEN breed is favourited WHEN toggleFavourite is called THEN it is removed`() = runTest {
        every { repository.fetchBreedById("abys") } returns flowOf(breed())
        every { favouriteInteractor.getFavouriteIds() } returns flowOf(setOf("abys"))

        val viewModel = viewModel("abys")
        advanceUntilIdle()
        viewModel.toggleFavourite()
        advanceUntilIdle()

        coVerify(exactly = 1) { favouriteInteractor.removeFavourite("abys") }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun viewModel(breedId: String) = BreedDetailViewModel(
        repository = repository,
        favouriteInteractor = favouriteInteractor,
        savedStateHandle = SavedStateHandle(mapOf("breedId" to breedId)),
    )

    private fun breed(id: String = "abys") = Breed(
        breedId = id,
        breedName = "Abyssinian",
        origin = "Ethiopia",
        temperament = "Active, Energetic",
        description = "A beautiful cat.",
        lifeSpan = "14 - 15",
        imageUrl = null,
        weightMetric = "3 - 5",
    )
}
