package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.composable.colorpage.ColorPageTileChip
import dev.mnascimentos.aureole.composable.colorpage.colorPageTileName
import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageMetrics
import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageTile
import dev.mnascimentos.aureole.composable.colorpage.model.ColorTileLook
import dev.mnascimentos.aureole.composable.sheets.ThemePickerGrid
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerGridParams
import dev.mnascimentos.aureole.composable.sheets.model.colorIn
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorRefFor
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorTiles
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorTitleRes
import dev.mnascimentos.aureole.feature.home.components.clock.model.selectedClockColorTile
import dev.mnascimentos.aureole.feature.home.components.clock.model.toClockColorRef
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * The color page of the clock editor, shown in the sheet itself in place of the tabs (RF-E4, D-02): "None" first for
 * the card, "Theme", "Current" when the saved color is in no palette, then the palettes. Each card tile is that card
 * with a strip in the current text color; each text tile is a strip in that color over the current card (RF-E6).
 * Tapping a tile reports its color through [onPick] (null: "None" or the text "Theme").
 */
@Composable
internal fun EditClockColorPage(
    uiState: MainUiState,
    role: ComponentColorRole,
    onPick: (ThemeColorRef?) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val saved = clockColorRefFor(uiState, role)
    val tiles = remember(saved, role, isDarkTheme) { clockColorTiles(saved, role, isDarkTheme) }
    val marked = selectedClockColorTile(saved, role, isDarkTheme).key
    val textColor = clockColorRefFor(uiState, ComponentColorRole.TEXT)?.resolve(AureoleDS.colors)
        ?: clockDefaultColor(ComponentColorRole.TEXT)
    val cardColor = clockColorRefFor(uiState, ComponentColorRole.CARD)?.resolve(AureoleDS.colors)
    ThemePickerGrid(
        params = ThemePickerGridParams(
            label = stringResource(clockColorTitleRes(role)),
            columns = ColorPageMetrics.COLUMNS
        ),
        modifier = Modifier.padding(horizontal = AureoleDS.dimens.small)
    ) {
        items(items = tiles, key = { it.key }, contentType = { "clock_color_tile" }) { tile ->
            val candidate = tileColor(tile, role)
            ColorPageTileChip(
                name = colorPageTileName(tile),
                look = when (role) {
                    ComponentColorRole.CARD -> ColorTileLook(
                        candidate,
                        textColor.takeIf { tile != ColorPageTile.None }
                    )
                    ComponentColorRole.TEXT -> ColorTileLook(background = cardColor, strip = candidate)
                },
                isSelected = tile.key == marked,
                onSelect = { onPick(tile.toClockColorRef(role)) }
            )
        }
    }
}

/** The color a tile stands for under the current theme; null for "None". */
@Composable
@ReadOnlyComposable
private fun tileColor(tile: ColorPageTile, role: ComponentColorRole): Color? = when (tile) {
    ColorPageTile.None -> null
    is ColorPageTile.Entry -> when (val entry = tile.entry) {
        ComponentColorEntry.Theme -> clockDefaultColor(role)
        is ComponentColorEntry.Current -> entry.ref.resolve(AureoleDS.colors)
        is ComponentColorEntry.Palette -> role.colorIn(entry.palette)
    }
}

@AureolePreview
@Composable
private fun EditClockColorPagePreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.background(AureoleDS.colors.surface)) {
            EditClockColorPage(uiState = MainUiState(), role = ComponentColorRole.CARD, onPick = {})
        }
    }
}
