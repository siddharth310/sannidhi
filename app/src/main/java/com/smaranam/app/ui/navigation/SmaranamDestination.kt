package com.smaranam.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.smaranam.app.R

enum class SmaranamDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
) {
    Home(
        route = "home",
        labelRes = R.string.tab_home,
        icon = Icons.Outlined.Home,
    ),
    Library(
        route = "library",
        labelRes = R.string.tab_library,
        icon = Icons.Outlined.AutoStories,
    ),
    Japa(
        route = "japa",
        labelRes = R.string.tab_japa,
        icon = Icons.Outlined.Loop,
    ),
    Saadhana(
        route = "saadhana",
        labelRes = R.string.tab_saadhana,
        icon = Icons.Outlined.Timeline,
    ),
}

val bottomDestinations = SmaranamDestination.entries
