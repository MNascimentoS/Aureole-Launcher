package dev.mnascimentos.aureole.core.designsystem.utils

import androidx.compose.foundation.Indication
import androidx.compose.material3.ripple
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

/**
 * The one touch highlight of the app: the ripple tinted with the high-emphasis content color of the theme, so it is
 * light over a dark theme and dark over a light one. Menu rows and app items press with this same highlight.
 */
fun pressHighlightColor(colors: AureoleColors): Color = colors.onSurfaceHigh

/** The [Indication] every clickable uses, provided once by `AureoleLauncherTheme` through `LocalIndication`. */
fun aureolePressIndication(colors: AureoleColors): Indication = ripple(color = pressHighlightColor(colors))
