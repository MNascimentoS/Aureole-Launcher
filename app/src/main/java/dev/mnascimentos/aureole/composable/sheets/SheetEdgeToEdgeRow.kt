package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/**
 * Horizontal room between the edge of an overlay sheet and its content: the padding of the sheet body plus the one
 * of its content column (`OverlaySheetBody`).
 */
@Composable
@ReadOnlyComposable
fun sheetContentInset(): Dp = AureoleDS.dimens.xxSmall + AureoleDS.dimens.small

/**
 * A row that scrolls sideways inside a sheet (RF-13) runs the whole width of the sheet: it grows by [inset] on each
 * side past the content padding, so what scrolls reaches the sheet's edge instead of being cut on a straight line
 * before it. Pair it with [edgeToEdgeRowPadding] of the same [inset], so the first and last items still line up with
 * the rest of the sheet at rest.
 */
fun Modifier.edgeToEdgeRow(inset: Dp): Modifier = layout { measurable, constraints ->
    val insetPx = inset.roundToPx()
    val width = edgeToEdgeWidth(constraints.maxWidth, insetPx)
    val placeable = measurable.measure(
        if (width == null) constraints else constraints.copy(minWidth = width, maxWidth = width)
    )
    val layoutWidth = if (width == null) placeable.width else constraints.maxWidth
    layout(layoutWidth, placeable.height) { placeable.placeRelative(if (width == null) 0 else -insetPx, 0) }
}

/** The content padding of an [edgeToEdgeRow]: the [inset] it grew by, on both sides. */
fun edgeToEdgeRowPadding(inset: Dp): PaddingValues = PaddingValues(horizontal = inset)

/** Width of a row widened by [insetPx] on each side, or null when the parent width is unbounded (left as is). */
internal fun edgeToEdgeWidth(maxWidth: Int, insetPx: Int): Int? =
    if (maxWidth == Constraints.Infinity) null else maxWidth + 2 * insetPx.coerceAtLeast(0)
