package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.telogaspar.catbreed.breedList.domain.model.Breed
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BreedListColumnTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun breed(id: String, name: String = "Breed $id") = Breed(
        breedId = id,
        breedName = name,
        imageUrl = null,
        lifeSpan = "10 - 15",
        origin = "Origin $id",
        temperament = "Active",
        description = "Description $id",
        weightMetric = "3 - 5",
    )

    @Test
    fun rendersAllBreedRows() {
        composeRule.setContent {
            BreedListColumn(
                breeds = listOf(breed("1", "Abyssinian"), breed("2", "Bengal")),
                isLoadingMore = false,
                isLastPage = false,
                isSearching = false,
                listState = rememberLazyListState(),
                favouriteIds = emptySet(),
                onBreedClick = {},
                onToggleFavourite = {},
            )
        }

        composeRule.onNodeWithText("Abyssinian").assertIsDisplayed()
        composeRule.onNodeWithText("Bengal").assertIsDisplayed()
    }

    @Test
    fun reflectsFavouriteStatePerBreed() {
        composeRule.setContent {
            BreedListColumn(
                breeds = listOf(breed("1", "Abyssinian"), breed("2", "Bengal")),
                isLoadingMore = false,
                isLastPage = false,
                isSearching = false,
                listState = rememberLazyListState(),
                favouriteIds = setOf("1"),
                onBreedClick = {},
                onToggleFavourite = {},
            )
        }

        // Breed 1 is favourited → remove affordance; breed 2 is not → add affordance.
        composeRule.onNodeWithContentDescription("Remove from favourites").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add to favourites").assertIsDisplayed()
    }

    @Test
    fun tappingRow_invokesClickWithBreedId() {
        var clickedId: String? = null
        composeRule.setContent {
            BreedListColumn(
                breeds = listOf(breed("1", "Abyssinian"), breed("2", "Bengal")),
                isLoadingMore = false,
                isLastPage = false,
                isSearching = false,
                listState = rememberLazyListState(),
                favouriteIds = emptySet(),
                onBreedClick = { clickedId = it },
                onToggleFavourite = {},
            )
        }

        composeRule.onNodeWithText("Bengal").performClick()

        assertEquals("2", clickedId)
    }

    @Test
    fun tappingHeart_invokesToggleWithBreedId() {
        var toggledId: String? = null
        composeRule.setContent {
            BreedListColumn(
                breeds = listOf(breed("1", "Abyssinian"), breed("2", "Bengal")),
                isLoadingMore = false,
                isLastPage = false,
                isSearching = false,
                listState = rememberLazyListState(),
                favouriteIds = setOf("1"), // makes breed 1's heart the unique "Remove" node
                onBreedClick = {},
                onToggleFavourite = { toggledId = it },
            )
        }

        composeRule.onNodeWithContentDescription("Remove from favourites").performClick()

        assertEquals("1", toggledId)
    }

    @Test
    fun whenLastPage_showsEndOfListFooter() {
        composeRule.setContent {
            BreedListColumn(
                breeds = listOf(breed("1"), breed("2")),
                isLoadingMore = false,
                isLastPage = true,
                isSearching = false,
                listState = rememberLazyListState(),
                favouriteIds = emptySet(),
                onBreedClick = {},
                onToggleFavourite = {},
            )
        }

        composeRule.onNodeWithText("End of list · 2 breeds").assertIsDisplayed()
    }

    @Test
    fun whenLoadingMore_showsLoadingFooter() {
        composeRule.setContent {
            BreedListColumn(
                breeds = listOf(breed("1")),
                isLoadingMore = true,
                isLastPage = false,
                isSearching = false,
                listState = rememberLazyListState(),
                favouriteIds = emptySet(),
                onBreedClick = {},
                onToggleFavourite = {},
            )
        }

        composeRule.onNodeWithText("Loading more…").assertIsDisplayed()
    }

    @Test
    fun whenSearching_footerIsHidden() {
        composeRule.setContent {
            BreedListColumn(
                breeds = listOf(breed("1")),
                isLoadingMore = false,
                isLastPage = true,
                isSearching = true,
                listState = rememberLazyListState(),
                favouriteIds = emptySet(),
                onBreedClick = {},
                onToggleFavourite = {},
            )
        }

        composeRule.onNodeWithText("End of list · 1 breeds").assertDoesNotExist()
        composeRule.onNodeWithText("Scroll to see more").assertDoesNotExist()
    }
}
