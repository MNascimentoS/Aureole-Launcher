package dev.mnascimentos.aureole.core.designsystem.utils

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.model.fadeEdgesFor

/**
 * Fades the content of a scrollable area into transparency where it meets the fixed header (top) or the tab bar
 * (bottom), only on the edges that have more content behind them. The fade height is a DS dimen and only paints
 * over the content: it takes no space and no touch area, so the last item stays whole above the bar (RF-E3, RF-E4).
 */
fun Modifier.fadingEdges(state: ScrollableState, fadeHeight: Dp): Modifier =
    this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val edges = fadeEdgesFor(state.canScrollBackward, state.canScrollForward)
            val height = fadeHeight.toPx().coerceAtMost(size.height / 2)
            if (edges.top) {
                drawRect(
                    brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black), startY = 0f, endY = height),
                    size = Size(size.width, height),
                    blendMode = BlendMode.DstIn
                )
            }
            if (edges.bottom) {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.Black, Color.Transparent),
                        startY = size.height - height,
                        endY = size.height
                    ),
                    topLeft = Offset(0f, size.height - height),
                    size = Size(size.width, height),
                    blendMode = BlendMode.DstIn
                )
            }
        }
