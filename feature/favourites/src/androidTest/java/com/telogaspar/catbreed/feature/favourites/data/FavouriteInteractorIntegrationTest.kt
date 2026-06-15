package com.telogaspar.catbreed.feature.favourites.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.telogaspar.catbreed.core.database.CatBreedsDatabase
import com.telogaspar.catbreed.feature.favourites.data.local.FavouriteInteractorImpl
import com.telogaspar.catbreed.feature.favourites.data.local.FavouriteRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class FavouriteInteractorIntegrationTest {

    private lateinit var db: CatBreedsDatabase
    private lateinit var interactor: FavouriteInteractorImpl

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CatBreedsDatabase::class.java,
        ).allowMainThreadQueries().build()

        interactor = FavouriteInteractorImpl(FavouriteRepositoryImpl(db.favouriteDao()))
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `GIVEN empty database WHEN getFavouriteIds is observed THEN emits empty set`() = runTest {
        val result = interactor.getFavouriteIds().first()

        assertEquals(emptySet(), result)
    }

    @Test
    fun `GIVEN addFavourite called WHEN getFavouriteIds is observed THEN emits set containing that id`() = runTest {
        interactor.addFavourite("abys")

        val result = interactor.getFavouriteIds().first()

        assertEquals(setOf("abys"), result)
    }

    @Test
    fun `GIVEN two addFavourite calls WHEN getFavouriteIds is observed THEN emits set with both ids`() = runTest {
        interactor.addFavourite("abys")
        interactor.addFavourite("aege")

        val result = interactor.getFavouriteIds().first()

        assertEquals(setOf("abys", "aege"), result)
    }

    @Test
    fun `GIVEN same id added twice WHEN getFavouriteIds is observed THEN emits set with one entry`() = runTest {
        interactor.addFavourite("abys")
        interactor.addFavourite("abys")

        val result = interactor.getFavouriteIds().first()

        assertEquals(setOf("abys"), result)
    }

    @Test
    fun `GIVEN addFavourite then removeFavourite WHEN getFavouriteIds is observed THEN emits empty set`() = runTest {
        interactor.addFavourite("abys")
        interactor.removeFavourite("abys")

        val result = interactor.getFavouriteIds().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN removeFavourite on unknown id WHEN getFavouriteIds is observed THEN emits empty set`() = runTest {
        interactor.removeFavourite("abys")

        val result = interactor.getFavouriteIds().first()

        assertTrue(result.isEmpty())
    }
}
