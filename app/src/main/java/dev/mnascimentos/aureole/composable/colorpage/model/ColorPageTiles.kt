package dev.mnascimentos.aureole.composable.colorpage.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.componentColorEntries
import dev.mnascimentos.aureole.composable.sheets.model.selectedColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.toColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes

/**
 * One tile of a color page shown inside a component's sheet (the clock, the app & folder group): "None" (a card
 * that can be switched off) or a tile of the palette picker in color mode.
 */
sealed interface ColorPageTile {
    /** Stable key of the tile in the grid. */
    val key: String

    /** No card at all; saved as a null color. */
    data object None : ColorPageTile {
        override val key: String = "color_none"
    }

    /** "Theme", "Current" or a palette, as the palette picker in color mode has them. */
    data class Entry(val entry: ComponentColorEntry) : ColorPageTile {
        override val key: String get() = entry.key
    }
}

/**
 * How a tile of a color page looks: its [background] (null: no card, the sheet's neutral surface) and its [strip]
 * (null: the "None" mark).
 */
data class ColorTileLook(val background: Color?, val strip: Color?)

/**
 * The tile that stands for the [saved] color of a card whose theme color is saved as [themeRef]: null is "None",
 * [themeRef] is "Theme", else the palette that has it, or "Current".
 */
fun selectedCardColorTile(
    saved: ThemeColorRef?,
    themeRef: ThemeColorRef,
    isDarkTheme: Boolean,
    palettes: List<ThemePalette> = allPalettes
): ColorPageTile = when (saved) {
    null -> ColorPageTile.None
    themeRef -> ColorPageTile.Entry(ComponentColorEntry.Theme)
    else -> ColorPageTile.Entry(selectedColorEntry(saved, ComponentColorRole.CARD, isDarkTheme, palettes))
}

/**
 * Tiles of the color page of a card, in order: "None", "Theme" ([themeRef]), "Current" when the saved color is in
 * no palette, and every palette.
 */
fun cardColorTiles(
    saved: ThemeColorRef?,
    themeRef: ThemeColorRef,
    isDarkTheme: Boolean,
    palettes: List<ThemePalette> = allPalettes
): List<ColorPageTile> {
    // The theme card is "Theme", not a "Current" color.
    val shownAsSaved = saved.takeUnless { it == themeRef }
    val entries = componentColorEntries(shownAsSaved, ComponentColorRole.CARD, isDarkTheme, palettes)
        .map { ColorPageTile.Entry(it) }
    return listOf(ColorPageTile.None) + entries
}

/** What picking this tile saves for a card whose theme color is [themeRef]: null for "None", [themeRef] for "Theme". */
fun ColorPageTile.toCardColorRef(themeRef: ThemeColorRef): ThemeColorRef? = when (this) {
    ColorPageTile.None -> null
    is ColorPageTile.Entry -> when (entry) {
        ComponentColorEntry.Theme -> themeRef
        else -> entry.toColorRef(ComponentColorRole.CARD)
    }
}

/** Fixed values of the color pages that are not design system tokens. */
object ColorPageMetrics {
    /** Length of each dash (and gap) of the "None" dot, as a share of the dot size. */
    const val NONE_DOT_DASH_FRACTION = 0.12f

    /** Columns of a color page, as in the palette picker in color mode. */
    const val COLUMNS = 3
}
