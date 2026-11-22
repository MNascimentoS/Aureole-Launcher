package dev.mnascimentos.aureole.composable.clock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.composable.clock.model.ClockSpace
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
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

/**
 * Puts [gap] above the content; a negative gap pulls the content up, closer to the line above (drawn, not
 * clipped).
 */
fun Modifier.lineGap(gap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val gapPx = gap.roundToPx()
    layout(placeable.width, (placeable.height + gapPx).coerceAtLeast(0)) { placeable.place(0, gapPx) }
}

/** Size of this space of the clock card, from the design tokens (D-10). */
@Composable
@ReadOnlyComposable
internal fun ClockSpace.size(): Dp = when (this) {
    ClockSpace.EDGE -> AureoleDS.dimens.medium
    ClockSpace.NONE -> AureoleDS.dimens.zero
    ClockSpace.WIDER -> AureoleDS.dimens.xxSmall
    ClockSpace.TIGHTER -> -AureoleDS.dimens.xxSmall
}
