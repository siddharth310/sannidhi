package com.smaranam.app.ui.icons

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.path

internal fun ImageVector.Builder.addSvgPath(pathData: String, fill: Brush) {
    val nodes = PathParser().parsePathString(pathData).toNodes()
    path(fill = fill) {
        nodes.forEach { node ->
            when (node) {
                is PathNode.MoveTo -> moveTo(node.x, node.y)
                is PathNode.LineTo -> lineTo(node.x, node.y)
                is PathNode.HorizontalTo -> horizontalLineTo(node.x)
                is PathNode.VerticalTo -> verticalLineTo(node.y)
                is PathNode.CurveTo -> curveTo(node.x1, node.y1, node.x2, node.y2, node.x3, node.y3)
                is PathNode.QuadTo -> quadTo(node.x1, node.y1, node.x2, node.y2)
                is PathNode.Close -> close()
                else -> Unit
            }
        }
    }
}

internal fun ImageVector.Builder.addSvgPath(pathData: String, color: Color) =
    addSvgPath(pathData, SolidColor(color))
