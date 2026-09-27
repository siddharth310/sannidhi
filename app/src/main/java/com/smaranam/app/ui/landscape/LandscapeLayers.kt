package com.smaranam.app.ui.landscape

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.smaranam.app.ui.theme.Sky
import com.smaranam.app.ui.theme.SunDawn
import com.smaranam.app.ui.theme.paletteFor
import kotlin.random.Random

/**
 * §6.1 / illustrations.md §2 — layered hills, gopuram silhouette, palms, sun/moon/stars/birds.
 */
@Composable
fun LandscapeLayers(
    sky: Sky,
    modifier: Modifier = Modifier,
    seed: Int = 7,
) {
    val palette = paletteFor(sky)
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(
            brush = Brush.verticalGradient(
                colors = palette.gradientStops,
                startY = 0f,
                endY = h,
            ),
        )
        val hills = palette.hillLayers
        drawHill(w, h, 0.78f, hills.getOrElse(0) { hills.last() })
        drawGopuram(w, h * 0.52f, hills.getOrElse(1) { hills.last() })
        drawHill(w, h, 0.86f, hills.getOrElse(1) { hills.last() })
        drawPalm(w * 0.18f, h * 0.72f, hills.getOrElse(2) { hills.last() })
        drawPalm(w * 0.82f, h * 0.74f, hills.getOrElse(2) { hills.last() })
        drawHill(w, h, 0.94f, hills.getOrElse(2) { hills.last() })
        drawHill(w, h, 1f, hills.getOrElse(3) { hills.last() })

        if (palette.showSun) {
            val sunColor = palette.sunColor ?: SunDawn
            val cx = w * if (sky == Sky.SANDHYA) 0.78f else 0.22f
            val cy = h * 0.28f
            drawCircle(sunColor.copy(alpha = 0.35f), radius = w * 0.14f, center = Offset(cx, cy))
            drawCircle(sunColor, radius = w * 0.055f, center = Offset(cx, cy))
        }
        if (palette.showMoon) {
            drawCircle(Color(0xFFE8E0D0), radius = w * 0.04f, center = Offset(w * 0.72f, h * 0.22f))
        }
        if (palette.showStars) {
            val rnd = Random(seed)
            repeat(24) {
                drawCircle(
                    Color.White.copy(alpha = 0.5f + rnd.nextFloat() * 0.5f),
                    radius = 1.2f + rnd.nextFloat() * 1.5f,
                    center = Offset(rnd.nextFloat() * w, rnd.nextFloat() * h * 0.45f),
                )
            }
        }
        if (palette.showBirds) {
            drawBirds(w, h, Color(0xFF2A1A14).copy(alpha = 0.35f))
        }
    }
}

private fun DrawScope.drawHill(w: Float, h: Float, baseline: Float, color: Color) {
    val path = Path().apply {
        moveTo(0f, h * baseline)
        cubicTo(w * 0.25f, h * (baseline - 0.08f), w * 0.55f, h * (baseline + 0.02f), w, h * (baseline - 0.05f))
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawGopuram(centerX: Float, baseY: Float, color: Color) {
    val sh = size.height
    val sw = size.width
    val path = Path().apply {
        moveTo(centerX, baseY - sh * 0.18f)
        lineTo(centerX - sw * 0.04f, baseY)
        lineTo(centerX + sw * 0.04f, baseY)
        close()
        moveTo(centerX, baseY - sh * 0.22f)
        lineTo(centerX - sw * 0.025f, baseY - sh * 0.12f)
        lineTo(centerX + sw * 0.025f, baseY - sh * 0.12f)
        close()
    }
    drawPath(path, color.copy(alpha = 0.85f))
}

private fun DrawScope.drawPalm(x: Float, baseY: Float, color: Color) {
    val sh = size.height
    val sw = size.width
    drawLine(color, Offset(x, baseY), Offset(x, baseY - sh * 0.12f), strokeWidth = 3f)
    drawLine(color, Offset(x, baseY - sh * 0.1f), Offset(x - sw * 0.04f, baseY - sh * 0.14f), strokeWidth = 2f)
    drawLine(color, Offset(x, baseY - sh * 0.1f), Offset(x + sw * 0.04f, baseY - sh * 0.14f), strokeWidth = 2f)
}

private fun DrawScope.drawBirds(w: Float, h: Float, color: Color) {
    listOf(0.35f to 0.25f, 0.5f to 0.22f).forEach { (fx, fy) ->
        val cx = w * fx
        val cy = h * fy
        drawLine(color, Offset(cx - 8f, cy), Offset(cx, cy - 3f), strokeWidth = 2f)
        drawLine(color, Offset(cx, cy - 3f), Offset(cx + 8f, cy), strokeWidth = 2f)
    }
}
