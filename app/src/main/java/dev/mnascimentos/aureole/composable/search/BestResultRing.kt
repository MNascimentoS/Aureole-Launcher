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
 * Does nothing when [isBest] is false.
 */
@Composable
@ReadOnlyComposable
fun Modifier.bestResultRing(isBest: Boolean): Modifier {
    if (!isBest) return this
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    val width = AureoleDS.dimens.minimal
    return this
        .border(width, AureoleDS.colors.onSurfaceMedium, shape)
        .padding(width)
        .border(width, AureoleDS.colors.surface, shape)
}
