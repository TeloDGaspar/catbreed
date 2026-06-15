package com.telogaspar.catbreed.breedList.presentation

import com.telogaspar.catbreed.breedList.domain.model.Breed
import com.telogaspar.catbreed.breedList.domain.repository.BreedListRepository
import com.telogaspar.catbreed.core.repository.FavouriteInteractor
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
class BreedListViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository: BreedListRepository = mockk()
    private val favouriteInteractor: FavouriteInteractor = mockk {
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

    // ── Initial load ───────────────────────────────────────────────────────────

    @Test
    fun `GIVEN repository returns breeds WHEN viewModel is created THEN state contains breeds`() = runTest {
        val breeds = listOf(breed("1"), breed("2"))
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(breeds)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(breeds, state.allBreeds)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `GIVEN viewModel is created WHEN loading starts THEN isLoading is true`() = runTest {
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(emptyList())

        val viewModel = BreedListViewModel(repository, favouriteInteractor)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN repository emits full page WHEN loading completes THEN isLastPage is false`() = runTest {
        val breeds = List(15) { breed("$it") }
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(breeds)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLastPage)
    }

    @Test
    fun `GIVEN repository emits partial page WHEN loading completes THEN isLastPage is true`() = runTest {
        val breeds = List(7) { breed("$it") }
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(breeds)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLastPage)
    }

    @Test
    fun `GIVEN repository throws WHEN loading THEN error message is shown`() = runTest {
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flow {
            throw RuntimeException("network error")
        }

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("network error", state.error)
        assertFalse(state.isLoading)
        assertTrue(state.allBreeds.isEmpty())
    }

    // ── Search ─────────────────────────────────────────────────────────────────

    @Test
    fun `GIVEN breeds loaded WHEN searching by name THEN filteredBreeds matches`() = runTest {
        val breeds = listOf(breed("1", name = "Abyssinian"), breed("2", name = "Bengal"))
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(breeds)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        viewModel.onSearchQueryChange("beng")
        advanceUntilIdle()

        val filtered = viewModel.uiState.value.filteredBreeds
        assertEquals(1, filtered.size)
        assertEquals("Bengal", filtered.first().breedName)
    }

    @Test
    fun `GIVEN breeds loaded WHEN searching by origin THEN filteredBreeds matches`() = runTest {
        val breeds = listOf(
            breed("1", name = "Abyssinian", origin = "Ethiopia"),
            breed("2", name = "Bengal", origin = "USA"),
        )
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(breeds)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        viewModel.onSearchQueryChange("ethiopia")
        advanceUntilIdle()

        val filtered = viewModel.uiState.value.filteredBreeds
        assertEquals(1, filtered.size)
        assertEquals("Abyssinian", filtered.first().breedName)
    }

    @Test
    fun `GIVEN search is active WHEN query is cleared THEN all breeds are shown`() = runTest {
        val breeds = listOf(breed("1"), breed("2"))
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(breeds)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        viewModel.onSearchQueryChange("abc")
        viewModel.onSearchQueryChange("")

        assertEquals(breeds, viewModel.uiState.value.filteredBreeds)
    }

    // ── Pagination ─────────────────────────────────────────────────────────────

    @Test
    fun `GIVEN first page loaded WHEN loadNextPage is called THEN second page is appended`() = runTest {
        val page0 = List(15) { breed("p0-$it") }
        val page1 = List(15) { breed("p1-$it") }
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(page0)
        every { repository.fetchBreedList(page = 1, limit = 15) } returns flowOf(page1)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        viewModel.loadNextPage()
        advanceUntilIdle()

        val allBreeds = viewModel.uiState.value.allBreeds
        assertEquals(30, allBreeds.size)
        assertEquals(page0 + page1, allBreeds)
    }

    @Test
    fun `GIVEN isLastPage is true WHEN loadNextPage is called THEN no new request is made`() = runTest {
        val partialPage = List(5) { breed("$it") }
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(partialPage)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isLastPage)

        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(5, viewModel.uiState.value.allBreeds.size)
    }

    @Test
    fun `GIVEN search query is active WHEN loadNextPage is called THEN no new request is made`() = runTest {
        val breeds = List(15) { breed("$it") }
        every { repository.fetchBreedList(page = 0, limit = 15) } returns flowOf(breeds)

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        viewModel.onSearchQueryChange("ab")

        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(15, viewModel.uiState.value.allBreeds.size)
    }

    // ── Retry ──────────────────────────────────────────────────────────────────

    @Test
    fun `GIVEN error state WHEN retry is called THEN breeds are reloaded`() = runTest {
        val breeds = listOf(breed("1"))
        every { repository.fetchBreedList(page = 0, limit = 15) } returnsMany listOf(
            flow { throw RuntimeException("fail") },
            flowOf(breeds),
        )

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(breeds, state.allBreeds)
    }

    @Test
    fun `GIVEN second page loaded WHEN retry is called THEN resets to page 0`() = runTest {
        val page0 = List(15) { breed("p0-$it") }
        val page0After = listOf(breed("fresh-0"))
        every { repository.fetchBreedList(page = 0, limit = 15) } returnsMany listOf(
            flowOf(page0),
            flowOf(page0After),
        )
        every { repository.fetchBreedList(page = 1, limit = 15) } returns flowOf(List(15) { breed("p1-$it") })

        val viewModel = BreedListViewModel(repository, favouriteInteractor)
        advanceUntilIdle()
        viewModel.loadNextPage()
        advanceUntilIdle()

        viewModel.retry()
        advanceUntilIdle()

        assertEquals(page0After, viewModel.uiState.value.allBreeds)
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun breed(id: String, name: String = "Breed $id", origin: String = "Origin $id") = Breed(
        breedId = id,
        breedName = name,
        origin = origin,
        temperament = "Active",
        description = "Description $id",
        lifeSpan = "10 - 15",
        imageUrl = null,
        weightMetric = "3 - 5",
    )
}
