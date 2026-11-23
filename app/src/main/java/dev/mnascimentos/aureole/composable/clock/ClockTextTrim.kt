package dev.mnascimentos.aureole.composable.clock

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.roundToInt

/**
 * Trims the empty band the font reserves above and below the glyphs: the layout gets shorter by the given
 * ratios of the text height, and the content is drawn where it was, so no glyph is clipped.
 * The text is measured at its own height, free of the height left in the card: a squeezed text would trim its
 * ratios from the wrong height and the next line would land on its glyphs (relogio-home-sheet RF-B4).
 */
fun Modifier.trimVerticalBands(topRatio: Float, bottomRatio: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
    val top = (placeable.height * topRatio).roundToInt()
    val bottom = (placeable.height * bottomRatio).roundToInt()
    layout(placeable.width, constraints.constrainHeight(placeable.height - top - bottom)) {
        placeable.place(0, -top)
    }
}

/**
 * Puts [gap] above the content; a negative gap pulls the content up, closer to the line above (drawn, not
 * clipped).
 */
fun Modifier.lineGap(gap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val gapPx = gap.roundToPx()
    layout(placeable.width, (placeable.height + gapPx).coerceAtLeast(0)) { placeable.place(0, gapPx) }
}

/**
 * Draws the content at its own size, free of the incoming constraints, shrunk by [scale] from the top start: the
 * layout takes the scaled size. The clock preview uses it to show the clock at the size it has on the home.
 */
fun Modifier.scaledLayout(scale: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(Constraints())
    val width = constraints.constrainWidth((placeable.width * scale).roundToInt())
    val height = constraints.constrainHeight((placeable.height * scale).roundToInt())
    layout(width, height) {
        placeable.placeWithLayer(0, 0) {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0f, 0f)
        }
    }
}
