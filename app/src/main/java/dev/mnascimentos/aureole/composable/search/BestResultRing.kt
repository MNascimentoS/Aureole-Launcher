package dev.mnascimentos.aureole.composable.search

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/**
 * Double ring that marks the best search result: a surface-colored gap inside a medium-emphasis outline.
 * Does nothing when [isBest] is false. The theme is read before the check, so toggling [isBest] never changes
 * which composition locals are read.
 */
@Composable
@ReadOnlyComposable
fun Modifier.bestResultRing(isBest: Boolean): Modifier {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    val width = AureoleDS.dimens.minimal
    val outerColor = AureoleDS.colors.onSurfaceMedium
    val innerColor = AureoleDS.colors.surface
    if (!isBest) return this
    return this
        .border(width, outerColor, shape)
        .padding(width)
        .border(width, innerColor, shape)
}
