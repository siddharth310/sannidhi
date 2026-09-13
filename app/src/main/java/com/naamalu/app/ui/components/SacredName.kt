package com.naamalu.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.naamalu.app.domain.model.Script
import com.naamalu.app.ui.theme.NaamaluTheme
import com.naamalu.app.ui.theme.notoSerifFor

@Composable
fun SacredName(
    text: String,
    script: Script,
    style: TextStyle = NaamaluTheme.typography.nameHero,
) {
    Text(
        text = text,
        style = style.copy(fontFamily = notoSerifFor(script)),
        color = NaamaluTheme.colors.textPrimary,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
    )
}
