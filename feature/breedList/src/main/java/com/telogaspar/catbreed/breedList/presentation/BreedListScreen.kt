package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telogaspar.catbreed.core.theme.LocalAppColors

@Composable
fun BreedListScreen(
    viewModel: BreedListViewModel = hiltViewModel(),
    onBreedClick: (String) -> Unit = {},
) {
    val colors = LocalAppColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x24E9C176), colors.paper),
                    center = Offset(Float.POSITIVE_INFINITY / 2, 0f),
                    radius = 900f,
                )
            )
    ) {
        BreedListAppBar()

        BreedSearchBar(
            query = uiState.searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
        )

        when {
            uiState.isLoading -> BreedListLoadingState()
            uiState.error != null && uiState.allBreeds.isEmpty() -> BreedListErrorState(
                message = uiState.error!!,
                onRetry = viewModel::retry,
            )
            uiState.filteredBreeds.isEmpty() && uiState.searchQuery.isNotBlank() -> BreedListEmptyState(
                query = uiState.searchQuery,
                onClear = { viewModel.onSearchQueryChange("") },
            )
            else -> BreedListColumn(
                breeds = uiState.filteredBreeds,
                isLoadingMore = uiState.isLoadingMore,
                isLastPage = uiState.isLastPage,
                isSearching = uiState.searchQuery.isNotBlank(),
                listState = listState,
                onBreedClick = onBreedClick,
            )
        }
    }
}
