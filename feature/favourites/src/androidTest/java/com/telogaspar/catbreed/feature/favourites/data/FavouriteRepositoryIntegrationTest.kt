package com.telogaspar.catbreed.feature.favourites.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.telogaspar.catbreed.core.database.CatBreedsDatabase
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.feature.favourites.data.local.FavouriteRepositoryImpl
import com.telogaspar.catbreed.feature.favourites.domain.model.FavouriteBreed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class FavouriteRepositoryIntegrationTest {

    private lateinit var db: CatBreedsDatabase
    private lateinit var repository: FavouriteRepositoryImpl

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CatBreedsDatabase::class.java,
        ).allowMainThreadQueries().build()

        repository = FavouriteRepositoryImpl(db.favouriteDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    // ── getFavouriteIds ────────────────────────────────────────────────────────

    @Test
    fun `GIVEN empty database WHEN getFavouriteIds is observed THEN emits empty set`() = runTest {
        val result = repository.getFavouriteIds().first()

        assertEquals(emptySet(), result)
    }

    @Test
    fun `GIVEN a favourite is added WHEN getFavouriteIds is observed THEN emits set containing that id`() = runTest {
        repository.addFavourite("abys")

        val result = repository.getFavouriteIds().first()

        assertEquals(setOf("abys"), result)
    }

    @Test
    fun `GIVEN two favourites are added WHEN getFavouriteIds is observed THEN emits set with both ids`() = runTest {
        repository.addFavourite("abys")
        repository.addFavourite("aege")

        val result = repository.getFavouriteIds().first()

        assertEquals(setOf("abys", "aege"), result)
    }

    @Test
    fun `GIVEN a favourite is added twice WHEN getFavouriteIds is observed THEN emits set with one entry`() = runTest {
        repository.addFavourite("abys")
        repository.addFavourite("abys")

        val result = repository.getFavouriteIds().first()

        assertEquals(setOf("abys"), result)
    }

    @Test
    fun `GIVEN a favourite is added then removed WHEN getFavouriteIds is observed THEN emits empty set`() = runTest {
        repository.addFavourite("abys")
        repository.removeFavourite("abys")

        val result = repository.getFavouriteIds().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN removing a breedId that was never added WHEN getFavouriteIds is observed THEN emits empty set`() = runTest {
        repository.removeFavourite("abys")

        val result = repository.getFavouriteIds().first()

        assertTrue(result.isEmpty())
    }

    // ── getFavouriteBreeds ─────────────────────────────────────────────────────

    @Test
    fun `GIVEN empty database WHEN getFavouriteBreeds is observed THEN emits empty list`() = runTest {
        val result = repository.getFavouriteBreeds().first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `GIVEN breed exists in cat_breeds and is favourited WHEN getFavouriteBreeds is observed THEN emits that breed`() = runTest {
        val breed = catBreedEntity("abys", "Abyssinian")
        db.catBreedDao().upsertBreeds(listOf(breed))
        repository.addFavourite("abys")

        val result = repository.getFavouriteBreeds().first()

        assertEquals(listOf(favouriteBreed("abys", "Abyssinian")), result)
    }

    @Test
    fun `GIVEN two breeds favourited WHEN getFavouriteBreeds is observed THEN emits breeds ordered by name`() = runTest {
        val persian = catBreedEntity("pers", "Persian")
        val abyssinian = catBreedEntity("abys", "Abyssinian")
        db.catBreedDao().upsertBreeds(listOf(persian, abyssinian))
        repository.addFavourite("pers")
        repository.addFavourite("abys")

        val result = repository.getFavouriteBreeds().first()

        assertEquals(
            listOf(favouriteBreed("abys", "Abyssinian"), favouriteBreed("pers", "Persian")),
            result,
        )
    }

    @Test
    fun `GIVEN a breed is favourited but not in cat_breeds WHEN getFavouriteBreeds is observed THEN emits empty list`() = runTest {
        repository.addFavourite("abys")

        val result = repository.getFavouriteBreeds().first()

        // JOIN requires a matching cat_breeds row — orphan favourite is invisible
        assertEquals(emptyList(), result)
    }

    @Test
    fun `GIVEN a favourited breed is removed WHEN getFavouriteBreeds is observed THEN that breed is no longer returned`() = runTest {
        val breed = catBreedEntity("abys", "Abyssinian")
        db.catBreedDao().upsertBreeds(listOf(breed))
        repository.addFavourite("abys")
        repository.removeFavourite("abys")

        val result = repository.getFavouriteBreeds().first()

        assertEquals(emptyList(), result)
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private fun catBreedEntity(id: String, name: String) = CatBreedEntity(
        id = id,
        name = name,
        origin = "Unknown",
        temperament = "Active",
        description = "A cat breed.",
        lifeSpan = "10 - 15",
        imageUrl = null,
        weightMetric = "3 - 5",
    )

    private fun favouriteBreed(id: String, name: String) = FavouriteBreed(
        id = id,
        name = name,
        origin = "Unknown",
        imageUrl = null,
        lifeSpan = "10 - 15",
    )
}
