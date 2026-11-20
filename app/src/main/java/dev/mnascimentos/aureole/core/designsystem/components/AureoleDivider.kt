package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

private const val DIVIDER_ALPHA = 0.5f

/**
 * The Design System divider: a hairline in the outline color with [AureoleDS.dimens.xSmall] above and below, so it
 * takes 16 dp of height (plus the line) between two groups of rows. Menus and the apps drawer share it.
 */
@Composable
fun AureoleDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = AureoleDS.dimens.xSmall),
        thickness = AureoleDS.dimens.borderMinimal,
        color = AureoleDS.colors.outline.copy(alpha = DIVIDER_ALPHA)
    )
}
