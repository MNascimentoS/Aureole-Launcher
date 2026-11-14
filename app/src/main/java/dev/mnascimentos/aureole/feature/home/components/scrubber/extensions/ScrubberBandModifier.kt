package dev.mnascimentos.aureole.feature.home.components.scrubber.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberBand

/**
 * Takes [heightFraction] of the parent height and leaves [bottomPadding] under the letters, through [scrubberBand]:
 * the same function the home uses to know where the letters are, so both agree to the pixel. Equivalent to
 * `fillMaxHeight(heightFraction).padding(bottom = bottomPadding)`.
 */
fun Modifier.scrubberBand(heightFraction: Float, bottomPadding: Dp): Modifier = layout { measurable, constraints ->
    if (!constraints.hasBoundedHeight) {
        val placeable = measurable.measure(constraints)
        return@layout layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
    val band = scrubberBand(constraints.maxHeight, heightFraction, bottomPadding.roundToPx())
    val outer = band.outerPx.coerceIn(constraints.minHeight, constraints.maxHeight)
    val letters = band.lettersPx.coerceAtMost(outer)
    val placeable = measurable.measure(constraints.copy(minHeight = letters, maxHeight = letters))
    layout(placeable.width, outer) { placeable.place(0, 0) }
}
