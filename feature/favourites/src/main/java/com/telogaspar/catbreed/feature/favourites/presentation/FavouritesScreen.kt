package com.telogaspar.catbreed.feature.favourites.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts
import kotlin.math.roundToInt

@Composable
fun FavouritesScreen(
    viewModel: FavouritesViewModel = hiltViewModel(),
) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            .statusBarsPadding(),
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Favourites",
                fontFamily = fonts.serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = (-0.5).sp,
                color = colors.ink,
            )
        }

        // Average lifespan banner
        val avgLifespan = uiState.averageLifespan
        if (avgLifespan != null) {
            LifespanBanner(
                averageLifespan = avgLifespan,
                count = uiState.breeds.size,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
        }

        if (uiState.breeds.isEmpty()) {
            EmptyFavourites(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(uiState.breeds, key = { it.id }) { breed ->
                    FavouriteCard(
                        breed = breed,
                        onRemove = { viewModel.removeFavourite(breed.id) },
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun LifespanBanner(
    averageLifespan: Double,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.goldGradient)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "Average lifespan",
                fontFamily = fonts.sans,
                fontSize = 12.sp,
                color = colors.onGold.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${averageLifespan.roundToInt()} years",
                fontFamily = fonts.serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                color = colors.onGold,
            )
        }
        Text(
            text = "$count ${if (count == 1) "breed" else "breeds"}",
            fontFamily = fonts.sans,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onGold.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun EmptyFavourites(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.Rounded.FavoriteBorder,
                contentDescription = null,
                tint = colors.goldDeep.copy(alpha = 0.4f),
                modifier = Modifier.size(56.dp),
            )
            Text(
                text = "No favourites yet",
                fontFamily = fonts.serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = colors.ink2,
            )
            Text(
                text = "Tap the heart on any breed to save it here.",
                fontFamily = fonts.sans,
                fontSize = 14.sp,
                color = colors.ink3,
            )
        }
    }
}
