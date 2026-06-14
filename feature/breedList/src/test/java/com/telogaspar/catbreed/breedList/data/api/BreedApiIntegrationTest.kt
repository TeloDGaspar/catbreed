package com.telogaspar.catbreed.breedList.data.api

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BreedApiIntegrationTest {

    private val api: BreedsEventApi = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BreedsEventApi::class.java)

    @Test
    fun `GIVEN live api WHEN getBreeds is called THEN returns a non-empty list`() = runBlocking {
        val breeds = api.getBreeds(limit = 5)

        assertFalse("Expected non-empty breed list", breeds.isEmpty())
    }

    @Test
    fun `GIVEN live api WHEN getBreeds is called THEN each breed has an id and name`() = runBlocking {
        val breeds = api.getBreeds(limit = 5)

        breeds.forEach { breed ->
            assertNotNull("breed.id should not be null", breed.id)
            assertNotNull("breed.name should not be null", breed.name)
        }
    }

    @Test
    fun `GIVEN live api WHEN getBreeds is called THEN prints breed names for manual inspection`() = runBlocking {
        val breeds = api.getBreeds(limit = 10)

        println("=== Breeds from API ===")
        breeds.forEach { println("• ${it.name} (${it.origin}) — ${it.temperament}") }
        println("=== Total: ${breeds.size} ===")
    }
}
