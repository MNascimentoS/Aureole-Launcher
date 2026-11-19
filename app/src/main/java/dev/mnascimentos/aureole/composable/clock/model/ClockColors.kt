package dev.mnascimentos.aureole.composable.clock.model

import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.parseThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

/**
 * The color the clock has: the saved [ThemeColorRef] ([refJson]), or, for a clock saved before the standard picker,
 * its ARGB ([legacyArgb], a preset by name) as the same fixed color, so it looks the same (RF-D5). Zero, unknown or
 * malformed data is the theme default, which is null.
 */
fun clockColorRefOf(legacyArgb: Int, refJson: String?): ThemeColorRef? =
    parseThemeColorRef(refJson) ?: legacyArgb.takeIf { it != 0 }?.let { ThemeColorRef.Fixed(it) }

/**
 * ARGB for the clock to draw under [colors], 0 meaning "theme default" (the header's own convention). Without
 * [colors] (no theme at hand) only the legacy ARGB is known.
 */
fun resolveClockColor(legacyArgb: Int, refJson: String?, colors: AureoleColors?): Int =
    if (colors == null) legacyArgb else clockColorRefOf(legacyArgb, refJson)?.resolve(colors)?.toArgb() ?: 0
