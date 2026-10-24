package app.sannidhi.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp
import app.sannidhi.ui.theme.Leaf
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.Peacock
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class ModuleKind { Puja, Calendar, Sankalpam, Stories }

@Composable
fun ModuleGlyph(kind: ModuleKind, modifier: Modifier = Modifier) {
    val color = when (kind) {
        ModuleKind.Puja -> Maroon
        ModuleKind.Calendar -> Peacock
        ModuleKind.Sankalpam -> Leaf
        ModuleKind.Stories -> Peacock
    }
    Canvas(modifier) {
        val sx = size.width / 64f
        val sy = size.height / 64f
        val stroke = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        scale(sx, sy, pivot = Offset.Zero) {
            when (kind) {
                ModuleKind.Puja -> listOf(
                    "M18 32c0 12 6 20 14 20s14-8 14-20Z",
                    "M22 32h20",
                    "M24 56h16",
                    "M25 40h14",
                    "M27 26c-4-3-9-3-12 0",
                    "M37 26c4-3 9-3 12 0",
                ).forEach { drawPath(iconPath(it), color, style = stroke) }
                ModuleKind.Calendar -> listOf(
                    "M12 16h40v36a6 6 0 0 1-6 6H18a6 6 0 0 1-6-6Z",
                    "M12 26h40",
                    "M22 10v10",
                    "M42 10v10",
                    "M36 33a8 8 0 1 0 0 12a6 6 0 1 1 0-12Z",
                ).forEach { drawPath(iconPath(it), color, style = stroke) }
                ModuleKind.Sankalpam -> {
                    val beads = 12
                    repeat(beads) { bead ->
                        val angle = bead / beads.toFloat() * (PI * 2) - PI / 2
                        drawCircle(
                            color = color,
                            radius = 2.2f,
                            center = Offset(32f + cos(angle).toFloat() * 16f, 28f + sin(angle).toFloat() * 16f),
                        )
                    }
                    drawCircle(color, radius = 3.5f, center = Offset(32f, 47f), style = stroke)
                    drawPath(iconPath("M32 51v8M28 59h8"), color, style = stroke)
                }
                ModuleKind.Stories -> listOf(
                    "M32 22c-6-4-14-4-20-2v28c6-2 14-2 20 2c6-4 14-4 20-2V20c-6-2-14-2-20 2Z",
                    "M32 22v28",
                    "M44 8l1.6 3.4 3.4 1.6-3.4 1.6L44 18l-1.6-3.4L39 13l3.4-1.6Z",
                ).forEach { drawPath(iconPath(it), color, style = stroke) }
            }
            if (kind == ModuleKind.Puja) {
                drawCircle(color, radius = 6f, center = Offset(32f, 20f), style = stroke)
            }
        }
    }
}

private fun iconPath(data: String): Path = PathParser().parsePathString(data).toPath()
