package com.smaranam.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.smaranam.app.domain.model.Script
import com.smaranam.app.ui.theme.SmaranamTheme
import com.smaranam.app.ui.theme.notoSerifFor

@Composable
fun SacredName(
    text: String,
    script: Script,
    style: TextStyle = SmaranamTheme.typography.nameHero,
) {
    Text(
        text = text,
        style = style.copy(fontFamily = notoSerifFor(script)),
        color = SmaranamTheme.colors.textPrimary,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
    )
}
