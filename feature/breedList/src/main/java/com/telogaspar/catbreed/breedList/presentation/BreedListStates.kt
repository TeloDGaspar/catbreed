package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts

@Composable
internal fun BreedListLoadingState() {
    val colors = LocalAppColors.current
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by transition.animateFloat(
        initialValue = 1.4f,
        targetValue = -0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerX",
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(colors.sunken, Color(0xFFF0E8DA), colors.sunken),
        start = Offset(shimmerX * 500f, 0f),
        end = Offset((shimmerX + 0.6f) * 500f, 0f),
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false,
    ) {
        items(6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.card)
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(modifier = Modifier.size(58.dp).clip(CircleShape).background(shimmerBrush))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Box(modifier = Modifier.fillMaxWidth(0.58f).height(16.dp).clip(RoundedCornerShape(8.dp)).background(shimmerBrush))
                    Box(modifier = Modifier.fillMaxWidth(0.38f).height(11.dp).clip(RoundedCornerShape(6.dp)).background(shimmerBrush))
                }
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(shimmerBrush))
            }
        }
    }
}

@Composable
internal fun BreedListErrorState(message: String, onRetry: () -> Unit) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Column(
        modifier = Modifier.fillMaxSize().padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(76.dp).clip(CircleShape).background(colors.danger.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Warning, contentDescription = null, tint = colors.danger, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text("Could not load", fontFamily = fonts.serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = colors.ink, letterSpacing = (-0.2).sp)
        Spacer(Modifier.height(7.dp))
        Text(text = message, fontFamily = fonts.sans, fontSize = 14.sp, color = colors.ink2, lineHeight = 21.sp)
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(52.dp),
        ) {
            Box(
                modifier = Modifier.height(52.dp).background(colors.goldGradient, CircleShape).padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Try again", fontFamily = fonts.sans, fontWeight = FontWeight.Bold, fontSize = 15.5.sp, color = colors.onGold)
            }
        }
    }
}

@Composable
internal fun BreedListEmptyState(query: String, onClear: () -> Unit) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Column(
        modifier = Modifier.fillMaxSize().padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(76.dp).clip(CircleShape).background(colors.card).shadow(2.dp, CircleShape, ambientColor = colors.shadow),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = colors.goldDeep, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text("No results", fontFamily = fonts.serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = colors.ink, letterSpacing = (-0.2).sp)
        Spacer(Modifier.height(7.dp))
        Text(
            text = "No breed matches \"$query\". Try another name or origin.",
            fontFamily = fonts.sans,
            fontSize = 14.sp,
            color = colors.ink2,
            lineHeight = 21.sp,
        )
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = onClear,
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.ink),
            border = BorderStroke(1.5.dp, colors.ghost),
            modifier = Modifier.height(52.dp),
        ) {
            Text("Clear search", fontFamily = fonts.sans, fontWeight = FontWeight.Bold, fontSize = 15.5.sp)
        }
    }
}
