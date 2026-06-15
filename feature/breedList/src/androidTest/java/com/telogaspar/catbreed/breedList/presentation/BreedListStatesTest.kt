package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BreedListStatesTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun errorState_showsMessageAndTitle() {
        composeRule.setContent {
            BreedListErrorState(message = "No internet connection", onRetry = {})
        }

        composeRule.onNodeWithText("Could not load").assertIsDisplayed()
        composeRule.onNodeWithText("No internet connection").assertIsDisplayed()
    }

    @Test
    fun errorState_tappingTryAgain_invokesRetry() {
        var retryCount = 0
        composeRule.setContent {
            BreedListErrorState(message = "boom", onRetry = { retryCount++ })
        }

        composeRule.onNodeWithText("Try again").performClick()

        assertEquals(1, retryCount)
    }

    @Test
    fun emptyState_showsQueryInMessage() {
        composeRule.setContent {
            BreedListEmptyState(query = "sphynx", onClear = {})
        }

        composeRule.onNodeWithText("No results").assertIsDisplayed()
        composeRule.onNodeWithText("No breed matches \"sphynx\". Try another name or origin.")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_tappingClearSearch_invokesClear() {
        var cleared = false
        composeRule.setContent {
            BreedListEmptyState(query = "sphynx", onClear = { cleared = true })
        }

        composeRule.onNodeWithText("Clear search").performClick()

        assertEquals(true, cleared)
    }
}
