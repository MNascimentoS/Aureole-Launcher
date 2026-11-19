package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.runtime.saveable.Saver
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewState
import dev.mnascimentos.aureole.composable.sheets.model.basePaletteName
import dev.mnascimentos.aureole.composable.sheets.model.findBasePalette
import dev.mnascimentos.aureole.core.designsystem.palette.toLight

/**
 * Palette and font tried in the pickers inside the Appearance sheet (D-09), not saved yet. A null field means
 * nothing was tapped in that picker.
 *
 * @property paletteName Name of the base palette (never the " Light" variant).
 * @property fontName Name of the font in `AvailableFonts`.
 */
data class AppearanceDraft(
    val paletteName: String? = null,
    val fontName: String? = null
)

/**
 * What leaving a picker saves: the palette and the font to save (null: nothing to save) and the draft left after.
 */
data class AppearanceSave(
    val paletteName: String?,
    val fontName: String?,
    val rest: AppearanceDraft
)

/**
 * What to save when [leaving] goes away: back from that picker saves only its choice and keeps the other (back
 * from the background picker saves neither); closing the sheet (null) saves both. A choice equal to the one
 * already selected saves nothing.
 */
fun AppearanceDraft.saveOnLeaving(
    leaving: SettingsInnerKind?,
    selectedPalette: String,
    selectedFont: String
): AppearanceSave {
    val savesPalette = leaving == null || leaving == SettingsInnerKind.PALETTE
    val savesFont = leaving == null || leaving == SettingsInnerKind.FONT
    val palette = paletteName.takeIf { savesPalette }
        ?.takeUnless { it.basePaletteName().equals(selectedPalette.basePaletteName(), ignoreCase = true) }
    val font = fontName.takeIf { savesFont }?.takeUnless { it == selectedFont }
    val rest = AppearanceDraft(
        paletteName = paletteName.takeUnless { savesPalette },
        fontName = fontName.takeUnless { savesFont }
    )
    return AppearanceSave(paletteName = palette, fontName = font, rest = rest)
}

/** The draft as the preview theme reads it; the palette is mirrored to its light variant on a light system. */
fun AppearanceDraft.toPreviewState(isDarkTheme: Boolean): ThemePreviewState = ThemePreviewState(
    palette = paletteName?.let(::findBasePalette)?.let { if (isDarkTheme) it else it.toLight() },
    fontName = fontName
)

private const val DRAFT_SEPARATOR = "\n"

/** The draft as text: the two names, an empty one for nothing tapped. */
fun encodeAppearanceDraft(draft: AppearanceDraft): String =
    listOf(draft.paletteName.orEmpty(), draft.fontName.orEmpty()).joinToString(DRAFT_SEPARATOR)

/** Reads back [encodeAppearanceDraft]. */
fun decodeAppearanceDraft(text: String): AppearanceDraft {
    val parts = text.split(DRAFT_SEPARATOR)
    return AppearanceDraft(
        paletteName = parts.getOrNull(0)?.takeIf { it.isNotEmpty() },
        fontName = parts.getOrNull(1)?.takeIf { it.isNotEmpty() }
    )
}

/** Keeps the draft through a rotation. */
val AppearanceDraftSaver: Saver<AppearanceDraft, String> = Saver(
    save = { encodeAppearanceDraft(it) },
    restore = { decodeAppearanceDraft(it) }
)
