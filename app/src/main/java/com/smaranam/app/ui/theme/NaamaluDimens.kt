package com.smaranam.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** §4 — space, shape, elevation */
object NaamaluDimens {
    val gutter = 20.dp
    val cardGapMin = 12.dp
    val cardGapMax = 18.dp

    val radiusTile = 26.dp
    val radiusCard = 28.dp
    val radiusSheetTop = 30.dp
    val radiusNav = 28.dp
    val radiusButton = 999.dp

    val touchMin = 48.dp
    val ctaHeight = 60.dp
    val iconButtonMin = 44.dp
    val iconButtonMax = 56.dp

    val skyHeaderHome = 380.dp
    val skyHeaderPuja = 380.dp
    val skyHeaderSankalpa = 420.dp
    val sheetOverlap = 24.dp

    val navBarHeight = 78.dp
    val navSideMargin = 12.dp
    val navBottomGap = 16.dp
    val scrollBottomPadding = 110.dp

    val tileHeight = 124.dp
    val sectionCardHeight = 176.dp

    val shapeTile = RoundedCornerShape(radiusTile)
    val shapeCard = RoundedCornerShape(radiusCard)
    val shapeSheetTop = RoundedCornerShape(topStart = radiusSheetTop, topEnd = radiusSheetTop)
    val shapeNav = RoundedCornerShape(radiusNav)
    val shapePill = RoundedCornerShape(radiusButton)
}
