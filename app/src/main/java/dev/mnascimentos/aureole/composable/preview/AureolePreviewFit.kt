package dev.mnascimentos.aureole.composable.preview

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import dev.mnascimentos.aureole.composable.preview.model.previewFitScale
import kotlin.math.roundToInt

/** The bound the content is measured with: far past any real preview, but finite (scrolling content needs one). */
private const val NATURAL_SIZE_BOUND_PX = 16_384

/**
 * Draws the content at its own size and shrinks it, never grows it, just enough to fit the room the parent gives:
 * what the editor preview of a group needs when its content changes while it is open. The content is measured with a
 * large finite bound, since a scrolling group cannot be measured with none.
 */
fun Modifier.fitInRoom(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(Constraints(maxWidth = NATURAL_SIZE_BOUND_PX, maxHeight = NATURAL_SIZE_BOUND_PX))
    val roomWidth = if (constraints.hasBoundedWidth) constraints.maxWidth.toFloat() else Float.POSITIVE_INFINITY
    val roomHeight = if (constraints.hasBoundedHeight) constraints.maxHeight.toFloat() else Float.POSITIVE_INFINITY
    val scale = previewFitScale(placeable.width.toFloat(), placeable.height.toFloat(), roomWidth, roomHeight)
    layout((placeable.width * scale).roundToInt(), (placeable.height * scale).roundToInt()) {
        placeable.placeWithLayer(0, 0) {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0f, 0f)
        }
    }
}
