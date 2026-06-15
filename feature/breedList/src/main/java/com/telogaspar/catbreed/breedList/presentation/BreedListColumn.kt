package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telogaspar.catbreed.breedList.domain.model.Breed
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts

@Composable
internal fun BreedListColumn(
    breeds: List<Breed>,
    isLoadingMore: Boolean,
    isLastPage: Boolean,
    isSearching: Boolean,
    listState: LazyListState,
    favouriteIds: Set<String>,
    onBreedClick: (String) -> Unit,
    onToggleFavourite: (String) -> Unit,
) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(breeds, key = { _, b -> b.breedId }) { _, breed ->
            BreedRowCard(
                breed = breed,
                isFavourited = breed.breedId in favouriteIds,
                onToggleFavourite = { onToggleFavourite(breed.breedId) },
                onClick = { onBreedClick(breed.breedId) },
            )
        }

        if (!isSearching) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        isLoadingMore -> Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = colors.goldDeep,
                            )
                            Text("Loading more…", fontFamily = fonts.sans, fontSize = 13.sp, color = colors.ink2, fontWeight = FontWeight.Medium)
                        }
                        isLastPage -> Text(
                            text = "End of list · ${breeds.size} breeds",
                            fontFamily = fonts.sans,
                            fontSize = 13.sp,
                            color = colors.ink3,
                            fontWeight = FontWeight.Medium,
                        )
                        else -> Text(
                            text = "Scroll to see more",
                            fontFamily = fonts.sans,
                            fontSize = 13.sp,
                            color = colors.ink3,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
