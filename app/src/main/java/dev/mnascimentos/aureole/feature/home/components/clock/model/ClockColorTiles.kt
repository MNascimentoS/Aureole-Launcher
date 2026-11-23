package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.componentColorEntries
import dev.mnascimentos.aureole.composable.sheets.model.selectedColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.toColorRef
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes

/** One tile of the clock's color page (RF-E4): "None" (the card only) or a tile of the palette picker in color mode. */
sealed interface ClockColorTile {
    /** Stable key of the tile in the grid. */
    val key: String

    /** No card at all (D-09); saved as a null card color. */
    data object None : ClockColorTile {
        override val key: String = "color_none"
    }

    /** "Theme", "Current" or a palette, as the palette picker in color mode has them. */
    data class Entry(val entry: ComponentColorEntry) : ClockColorTile {
        override val key: String get() = entry.key
    }
}

/**
 * The tile that stands for the clock's [saved] color of [role]: for the card, null is "None" and the theme card
 * ([CLOCK_CARD_THEME_REF]) is "Theme"; for the text, null is "Theme". Else the palette that has it, or "Current".
 */
fun selectedClockColorTile(
    saved: ThemeColorRef?,
    role: ComponentColorRole,
    isDarkTheme: Boolean,
    palettes: List<ThemePalette> = allPalettes
): ClockColorTile = when {
    role == ComponentColorRole.CARD && saved == null -> ClockColorTile.None
    role == ComponentColorRole.CARD && saved == CLOCK_CARD_THEME_REF -> ClockColorTile.Entry(ComponentColorEntry.Theme)
    else -> ClockColorTile.Entry(selectedColorEntry(saved, role, isDarkTheme, palettes))
}

/**
 * Tiles of the clock's color page, in order: "None" first for the card (RF-E4), then "Theme", "Current" when the
 * saved color is in no palette, and every palette.
 */
fun clockColorTiles(
    saved: ThemeColorRef?,
    role: ComponentColorRole,
    isDarkTheme: Boolean,
    palettes: List<ThemePalette> = allPalettes
): List<ClockColorTile> {
    // The theme card is "Theme", not a "Current" color.
    val shownAsSaved = saved.takeUnless { role == ComponentColorRole.CARD && it == CLOCK_CARD_THEME_REF }
    val entries = componentColorEntries(shownAsSaved, role, isDarkTheme, palettes).map { ClockColorTile.Entry(it) }
    return if (role == ComponentColorRole.CARD) listOf(ClockColorTile.None) + entries else entries
}

/** What picking this tile saves for [role]: null for "None" and for the text "Theme", the theme card for the card. */
fun ClockColorTile.toClockColorRef(role: ComponentColorRole): ThemeColorRef? = when (this) {
    ClockColorTile.None -> null
    is ClockColorTile.Entry -> when {
        entry == ComponentColorEntry.Theme && role == ComponentColorRole.CARD -> CLOCK_CARD_THEME_REF
        else -> entry.toColorRef(role)
    }
}

/**
 * How a tile of the clock's color page looks (RF-E6): its [background] (null: no card, the sheet's neutral surface)
 * and its [strip] (null: the "None" mark).
 */
data class ClockTileLook(val background: Color?, val strip: Color?)
