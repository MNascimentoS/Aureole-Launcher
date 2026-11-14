package dev.mnascimentos.aureole.composable.sheets.model

import androidx.compose.ui.text.font.FontFamily
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts

private const val LIGHT_SUFFIX = " Light"
private const val DEFAULT_FONT_NAME = "Istok Web"

/**
 * Palette and font the user is trying out in the palette/font sheets, before tapping Apply.
 * A null field means "no change from the current theme".
 *
 * @property palette The palette as displayed (already mirrored to its light variant on a light system).
 * @property fontName Name of the font in `AvailableFonts`.
 */
data class ThemePreviewState(
    val palette: ThemePalette? = null,
    val fontName: String? = null
)

/** Theme the preview must render: the [base] theme with the [pending] choices laid on top. */
fun resolvePreviewConfig(base: AureoleThemeConfig, pending: ThemePreviewState): AureoleThemeConfig =
    base.copy(
        aureoleColors = pending.palette?.colors ?: base.aureoleColors,
        aureoleFontName = pending.fontName ?: base.aureoleFontName
    )

/** True when the pending choices would change the [base] theme. */
fun isDirty(base: AureoleThemeConfig, pending: ThemePreviewState): Boolean =
    resolvePreviewConfig(base, pending) != base

/** Discarding (Cancel, swipe down, tap outside) goes back to the initial state: nothing pending. */
fun ThemePreviewState.discard(): ThemePreviewState = ThemePreviewState()

/** Palette name without the " Light" suffix that `toLight()` adds. */
fun String.basePaletteName(): String = removeSuffix(LIGHT_SUFFIX).trim()

/** The dark palette that [name] (possibly the light variant) refers to, or null when unknown. */
fun findBasePalette(name: String): ThemePalette? =
    allPalettes.firstOrNull { it.name.equals(name.basePaletteName(), ignoreCase = true) }

/** The palette "Apply" saves: the one tapped, or, with no tap, the one already shown as selected. */
fun resolveAppliedPalette(pendingName: String?, selectedName: String): ThemePalette? =
    findBasePalette(pendingName ?: selectedName)

/** The font "Apply" saves: the one tapped, or, with no tap, the one already shown as selected. */
fun resolveAppliedFont(pendingName: String?, selectedName: String): String = pendingName ?: selectedName

/** Name of the font in `AvailableFonts` that matches [family]; falls back to the default font. */
fun fontNameOf(family: FontFamily): String =
    AvailableFonts.entries.firstOrNull { it.value == family }?.key ?: DEFAULT_FONT_NAME
