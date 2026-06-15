package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.telogaspar.catbreed.breedList.domain.model.Breed
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BreedRowCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val breed = Breed(
        breedId = "abys",
        breedName = "Abyssinian",
        imageUrl = null,
        lifeSpan = "14 - 15",
        origin = "Egypt",
        temperament = "Active",
        description = "Energetic cat",
        weightMetric = "3 - 5",
    )

    @Test
    fun rendersBreedNameAndOrigin() {
        composeRule.setContent {
            BreedRowCard(breed = breed, isFavourited = false, onToggleFavourite = {}, onClick = {})
        }

        composeRule.onNodeWithText("Abyssinian").assertIsDisplayed()
        composeRule.onNodeWithText("Egypt").assertIsDisplayed()
    }

    @Test
    fun whenNotFavourited_showsAddToFavouritesAffordance() {
        composeRule.setContent {
            BreedRowCard(breed = breed, isFavourited = false, onToggleFavourite = {}, onClick = {})
        }

        composeRule.onNodeWithContentDescription("Add to favourites").assertIsDisplayed()
    }

    @Test
    fun whenFavourited_showsRemoveFromFavouritesAffordance() {
        composeRule.setContent {
            BreedRowCard(breed = breed, isFavourited = true, onToggleFavourite = {}, onClick = {})
        }

        composeRule.onNodeWithContentDescription("Remove from favourites").assertIsDisplayed()
    }

    @Test
    fun tappingHeart_invokesToggleCallback() {
        var toggleCount = 0
        composeRule.setContent {
            BreedRowCard(
                breed = breed,
                isFavourited = false,
                onToggleFavourite = { toggleCount++ },
                onClick = {},
            )
        }

        composeRule.onNodeWithContentDescription("Add to favourites").performClick()

        assertEquals(1, toggleCount)
    }

    @Test
    fun tappingCard_invokesClickCallback() {
        var clickCount = 0
        composeRule.setContent {
            BreedRowCard(
                breed = breed,
                isFavourited = false,
                onToggleFavourite = {},
                onClick = { clickCount++ },
            )
        }

        composeRule.onNodeWithText("Abyssinian").performClick()

        assertEquals(1, clickCount)
    }
}
