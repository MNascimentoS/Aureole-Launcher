package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageTile
import dev.mnascimentos.aureole.composable.colorpage.model.cardColorTiles
import dev.mnascimentos.aureole.composable.colorpage.model.selectedCardColorTile
import dev.mnascimentos.aureole.composable.colorpage.model.toCardColorRef
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.componentColorEntries
import dev.mnascimentos.aureole.composable.sheets.model.selectedColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.toColorRef
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes

/**
 * The tile that stands for the clock's [saved] color of [role]: for the card, null is "None" and the theme card
 * ([CLOCK_CARD_THEME_REF]) is "Theme"; for the text, null is "Theme". Else the palette that has it, or "Current".
 */
fun selectedClockColorTile(
    saved: ThemeColorRef?,
    role: ComponentColorRole,
    isDarkTheme: Boolean,
    palettes: List<ThemePalette> = allPalettes
): ColorPageTile = when (role) {
    ComponentColorRole.CARD -> selectedCardColorTile(saved, CLOCK_CARD_THEME_REF, isDarkTheme, palettes)
    ComponentColorRole.TEXT -> ColorPageTile.Entry(selectedColorEntry(saved, role, isDarkTheme, palettes))
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
): List<ColorPageTile> = when (role) {
    ComponentColorRole.CARD -> cardColorTiles(saved, CLOCK_CARD_THEME_REF, isDarkTheme, palettes)
    ComponentColorRole.TEXT -> componentColorEntries(saved, role, isDarkTheme, palettes).map { ColorPageTile.Entry(it) }
}

/** What picking this tile saves for [role]: null for "None" and for the text "Theme", the theme card for the card. */
fun ColorPageTile.toClockColorRef(role: ComponentColorRole): ThemeColorRef? = when (role) {
    ComponentColorRole.CARD -> toCardColorRef(CLOCK_CARD_THEME_REF)
    ComponentColorRole.TEXT -> (this as? ColorPageTile.Entry)?.entry?.takeUnless { it == ComponentColorEntry.Theme }
        ?.toColorRef(role)
}
