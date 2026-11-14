package dev.mnascimentos.aureole.composable.clock

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import kotlin.math.roundToInt

/**
 * Trims the empty band the font reserves above and below the glyphs: the layout gets shorter by the given
 * ratios of the text height, and the content is drawn where it was, so no glyph is clipped.
 */
fun Modifier.trimVerticalBands(topRatio: Float, bottomRatio: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val top = (placeable.height * topRatio).roundToInt()
    val bottom = (placeable.height * bottomRatio).roundToInt()
    layout(placeable.width, placeable.height - top - bottom) { placeable.place(0, -top) }
}
