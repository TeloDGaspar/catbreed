package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts

@Composable
internal fun BreedListAppBar() {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 14.dp)) {
        Row(modifier = Modifier.height(44.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
        }
        Text(
            text = "CAT BREEDS",
            fontFamily = fonts.sans,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.5.sp,
            letterSpacing = 2.sp,
            color = colors.goldDeep,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Cat Breeds",
            fontFamily = fonts.serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp,
            letterSpacing = (-0.6).sp,
            color = colors.ink,
            lineHeight = 32.sp,
        )
    }
}
