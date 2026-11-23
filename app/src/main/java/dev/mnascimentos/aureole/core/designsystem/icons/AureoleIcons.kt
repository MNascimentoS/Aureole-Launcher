package dev.mnascimentos.aureole.core.designsystem.icons

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/**
 * A central registry of all icons used in the Aureole Design System.
 */
@Immutable
class AureoleIcons

@Composable
internal fun AureoleIcon(
    iconResId: Int,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    Icon(
        painter = painterResource(id = iconResId),
        contentDescription = null,
        modifier = modifier,
        tint = tint ?: AureoleDS.colors.onSurfaceMedium
    )
}

val LocalAureoleIcons = staticCompositionLocalOf { AureoleIcons() }
