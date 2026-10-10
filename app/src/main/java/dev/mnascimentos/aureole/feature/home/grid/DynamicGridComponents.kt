package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.mnascimentos.aureole.feature.home.grid.model.DragTargetSlot

private const val GRID_OFFSET_PX = 8f
private const val GRID_MARGIN_PX = 16f
private const val GRID_LINE_WIDTH = 2f
private const val GRID_CORNER_RADIUS = 24f
private const val DASH_LENGTH_PX = 10f
private const val TOP_BAR_Z_INDEX = 100f

@Composable
fun GridBackgroundOverlay(
    cellWidthPx: Float,
    cellHeightPx: Float,
    activeDragTarget: DragTargetSlot?,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    Canvas(modifier = modifier.fillMaxSize()) {
        val stroke = Stroke(
            width = GRID_LINE_WIDTH,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH_LENGTH_PX, DASH_LENGTH_PX), 0f)
        )

        activeDragTarget?.let { target ->
            val targetColor = if (target.isValid) primaryColor else errorColor
            val left = (target.col * cellWidthPx) + GRID_OFFSET_PX
            val top = (target.row * cellHeightPx) + GRID_OFFSET_PX
            val width = (target.colSpan * cellWidthPx) - GRID_MARGIN_PX
            val height = (target.rowSpan * cellHeightPx) - GRID_MARGIN_PX

            if (width > 0f && height > 0f) {
                drawRoundRect(
                    color = targetColor.copy(alpha = 0.25f),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS)
                )
                drawRoundRect(
                    color = targetColor.copy(alpha = 0.8f),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS),
                    style = stroke
                )
            }
        }
    }
}
