package com.telogaspar.catbreed.breedList.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts

@Composable
internal fun BreedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(CircleShape)
            .background(colors.sunken)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = colors.ink3, modifier = Modifier.size(19.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(fontFamily = fonts.sans, fontSize = 15.sp, color = colors.ink),
            cursorBrush = SolidColor(colors.goldDeep),
            decorationBox = { inner ->
                Box {
                    if (query.isEmpty()) {
                        Text("Search by breed or origin", fontFamily = fonts.sans, fontSize = 15.sp, color = colors.ink3)
                    }
                    inner()
                }
            },
        )
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Rounded.Clear, contentDescription = "Clear", tint = colors.ink2, modifier = Modifier.size(17.dp))
            }
        }
    }
}
