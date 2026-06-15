package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts
import kotlin.math.abs

@Composable
fun BreedDetailScreen(
    onBack: () -> Unit,
    viewModel: BreedDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalAppColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.paper)
    ) {
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.goldDeep)
            }
            uiState.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BreedListErrorState(message = uiState.error!!, onRetry = {})
            }
            uiState.breed != null -> DetailContent(
                breed = uiState.breed!!,
                isFavourite = uiState.isFavourite,
                onToggleFavourite = viewModel::toggleFavourite,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun DetailContent(
    breed: Breed,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalAppColors.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            DetailHero(breed = breed)
            DetailBody(breed = breed)
            // Space for sticky CTA
            Spacer(Modifier.height(90.dp))
        }

        // Back button — floating over hero
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 12.dp, top = 4.dp)
                .size(42.dp)
                .background(Color(0x6B14100A), CircleShape)
                .graphicsLayer { clip = true },
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(21.dp),
            )
        }

        // Sticky CTA footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, colors.paper),
                        startY = 0f,
                        endY = 60f,
                    )
                )
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Button(
                onClick = onToggleFavourite,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.goldGradient, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = colors.onGold,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = if (isFavourite) "Remove from favourites" else "Add to favourites",
                            fontFamily = LocalAppFonts.current.sans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp,
                            color = colors.onGold,
                        )
                    }
                }
            }
        }
    }
}

// ── Hero ───────────────────────────────────────────────────────────────────────

@Composable
private fun DetailHero(breed: Breed) {
    val fonts = LocalAppFonts.current
    val hue = breedDetailHue(breed.breedName)
    val c1 = Color.hsl(hue, 0.58f, 0.60f)
    val c2 = Color.hsl((hue + 28f) % 360f, 0.50f, 0.32f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(c1, c2),
                    center = Offset(0f, 0f),
                    radius = 900f,
                )
            )
    ) {
        // Cat silhouette watermark
        androidx.compose.foundation.Canvas(modifier = Modifier.size(240.dp).align(Alignment.TopEnd).padding(end = 0.dp, top = 22.dp)) {
            // simplified cat ear silhouette drawn as a path
        }

        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x2E14100A),
                            0.46f to Color(0x1F14100A),
                            1.0f to Color(0x9E14100A),
                        )
                    )
                )
        )

        // Text
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
        ) {
            Text(
                text = breed.origin ?: "",
                fontFamily = fonts.sans,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.86f),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = breed.breedName,
                fontFamily = fonts.serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 34.sp,
                letterSpacing = (-0.7).sp,
                lineHeight = 36.sp,
                color = Color.White,
            )
        }
    }
}

// ── Body ───────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailBody(breed: Breed) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {

        // Stat cards
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                label = "Life span",
                value = breed.lifeSpan?.replace(" - ", "–") ?: "—",
                unit = "years",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Weight",
                value = breed.weightMetric?.replace(" - ", "–") ?: "—",
                unit = "kg",
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(22.dp))

        // Origin
        DetailSection(title = "Origin") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = colors.goldDeep, modifier = Modifier.size(18.dp))
                Text(breed.origin ?: "Unknown", fontFamily = fonts.sans, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = colors.ink)
            }
        }

        Spacer(Modifier.height(22.dp))

        // Temperament chips
        val traits = breed.temperament?.split(", ")?.filter { it.isNotBlank() } ?: emptyList()
        if (traits.isNotEmpty()) {
            DetailSection(title = "Temperament") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    traits.forEach { trait -> TemperamentChip(trait) }
                }
            }
            Spacer(Modifier.height(22.dp))
        }

        // Description
        if (!breed.description.isNullOrBlank()) {
            DetailSection(title = "Description") {
                Text(
                    text = breed.description,
                    fontFamily = fonts.sans,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = colors.ink,
                )
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .then(
                Modifier.background(
                    Color.Transparent,
                    shape = RoundedCornerShape(14.dp),
                )
            )
            .padding(horizontal = 12.dp, vertical = 13.dp),
    ) {
        Text(
            text = label.uppercase(),
            fontFamily = fonts.sans,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp,
            letterSpacing = 1.3.sp,
            color = colors.ink3,
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, fontFamily = fonts.serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = colors.ink)
            Text(unit, fontFamily = fonts.sans, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = colors.ink2)
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Column {
        Text(
            text = title.uppercase(),
            fontFamily = fonts.sans,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.5.sp,
            letterSpacing = 1.7.sp,
            color = colors.goldDeep,
        )
        Spacer(Modifier.height(9.dp))
        content()
    }
}

@Composable
private fun TemperamentChip(text: String) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.card)
            .then(
                Modifier.background(Color.Transparent)
            )
            .padding(horizontal = 13.dp, vertical = 7.dp),
    ) {
        Text(text, fontFamily = fonts.sans, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = colors.ink)
    }
}

private fun breedDetailHue(name: String): Float {
    val hash = name.fold(0) { acc, c -> acc * 31 + c.code }
    return (abs(hash) % 360).toFloat()
}
