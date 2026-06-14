package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts
import kotlin.math.abs

@Composable
internal fun BreedRowCard(breed: Breed, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.995f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "rowScale",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = colors.shadow)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BreedAvatar(breed = breed)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = breed.breedName,
                fontFamily = fonts.serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                letterSpacing = (-0.2).sp,
                color = colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = colors.goldDeep, modifier = Modifier.size(13.dp))
                Text(
                    text = breed.origin ?: "Unknown",
                    fontFamily = fonts.sans,
                    fontSize = 12.5.sp,
                    color = colors.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun BreedAvatar(breed: Breed) {
    val hue = breedHue(breed.breedName)
    val c1 = Color.hsl(hue, 0.58f, 0.62f)
    val c2 = Color.hsl((hue + 26f) % 360f, 0.46f, 0.38f)

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(colors = listOf(c1, c2), center = Offset(16f, 13f), radius = 70f)),
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(breed.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = breed.breedName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = { BreedAvatarFallback(breed.breedName) },
            error = { BreedAvatarFallback(breed.breedName) },
        )
    }
}

@Composable
private fun BreedAvatarFallback(breedName: String) {
    val fonts = LocalAppFonts.current
    val initials = breedName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = initials, fontFamily = fonts.serif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = Color.White.copy(alpha = 0.95f))
    }
}

private fun breedHue(name: String): Float {
    val hash = name.fold(0) { acc, c -> acc * 31 + c.code }
    return (abs(hash) % 360).toFloat()
}
