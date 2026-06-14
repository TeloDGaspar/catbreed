package com.telogaspar.catbreed.breedList.data.remote

import com.telogaspar.catbreed.breedList.data.api.BreedsEventApi
import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import com.telogaspar.catbreed.breedList.data.model.Image
import com.telogaspar.catbreed.breedList.data.model.Weight
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BreedEventListRemoteDataSourceImplTest {

    private val api: BreedsEventApi = mockk()
    private val dataSource = BreedEventListRemoteDataSourceImpl(api)

    @Test
    fun `GIVEN api returns a list of breeds WHEN fetchBreedList is called THEN returns the same breeds`() = runTest {
        val expected = listOf(breedsResponse("1"), breedsResponse("2"))
        coEvery { api.getBreeds() } returns expected

        val result = dataSource.fetchBreedList()

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN api is configured WHEN fetchBreedList is called THEN delegates to the api exactly once`() = runTest {
        coEvery { api.getBreeds() } returns emptyList()

        dataSource.fetchBreedList()

        coVerify(exactly = 1) { api.getBreeds() }
    }

    @Test
    fun `GIVEN api returns empty list WHEN fetchBreedList is called THEN returns empty list`() = runTest {
        coEvery { api.getBreeds() } returns emptyList()

        val result = dataSource.fetchBreedList()

        assertEquals(emptyList<BreedsResponse>(), result)
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
}
