package com.smaranam.app.ui.layout

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier

/** §6.1 / §6.10 — keep chrome out from under system bars. */
fun Modifier.naamaluStatusBarsPadding(): Modifier = statusBarsPadding()

fun Modifier.naamaluNavigationBarsPadding(): Modifier = navigationBarsPadding()
