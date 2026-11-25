package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.colorpage.AureoleColorRow
import dev.mnascimentos.aureole.composable.colorpage.ColorPageTileChip
import dev.mnascimentos.aureole.composable.colorpage.colorPageTileName
import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageMetrics
import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageTile
import dev.mnascimentos.aureole.composable.colorpage.model.ColorTileLook
import dev.mnascimentos.aureole.composable.colorpage.model.cardColorTiles
import dev.mnascimentos.aureole.composable.colorpage.model.selectedCardColorTile
import dev.mnascimentos.aureole.composable.colorpage.model.toCardColorRef
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.composable.controls.model.SegmentStyle
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.sheets.ThemePickerGrid
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerGridParams
import dev.mnascimentos.aureole.composable.sheets.model.colorIn
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupColorTarget

/**
 * The Appearance tab (RF-E1, RF-E4, RF-E5): the two color rows, no section label, each opening its color page
 * ([onOpenColor]); FOLDERS IN THE GROUP (app preview or folder icon, D-06); and "Show names" (D-15).
 */
@Composable
internal fun ColumnScope.GroupAppearanceTab(state: EditContainerState, onOpenColor: (GroupColorTarget) -> Unit) {
    GroupColorTarget.entries.forEach { target ->
        val saved = state.colorOf(target)
        AureoleColorRow(
            title = stringResource(target.titleRes),
            value = colorPageTileName(selectedCardColorTile(saved, target.themeRef, isSystemInDarkTheme())),
            color = saved?.resolve(AureoleDS.colors),
            onClick = { onOpenColor(target) }
        )
    }
    GroupSectionLabel(
        text = stringResource(R.string.home_container_group_folders_label),
        modifier = Modifier.padding(top = AureoleDS.dimens.large)
    )
    GroupHelpText(text = stringResource(R.string.home_container_group_folders_help))
    AureoleSegmentedControl(
        options = listOf(
            stringResource(R.string.home_container_group_folders_preview),
            stringResource(R.string.home_container_group_folders_icon)
        ),
        selectedIndex = if (state.isGridFolderEnabled) 0 else 1,
        onSelect = { state.isGridFolderEnabled = it == 0 },
        style = SegmentStyle(role = Role.RadioButton)
    )
    AureoleSwitchRow(
        SwitchRowParams(
            title = stringResource(R.string.home_container_group_show_names),
            checked = state.showFolderLabels,
            onCheckedChange = { state.showFolderLabels = it }
        ),
        modifier = Modifier.padding(top = AureoleDS.dimens.xSmall)
    )
}

/**
 * The color page of [target], in the sheet in place of the tabs (RF-E2, D-04): "None" first, "Theme", "Current" when
 * the saved color is in no palette, then the palettes. A tap reports the color through [onPick] (null: "None").
 */
@Composable
internal fun GroupColorPage(state: EditContainerState, target: GroupColorTarget, onPick: (ThemeColorRef?) -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val saved = state.colorOf(target)
    val tiles = remember(saved, target, isDarkTheme) { cardColorTiles(saved, target.themeRef, isDarkTheme) }
    val marked = selectedCardColorTile(saved, target.themeRef, isDarkTheme).key
    ThemePickerGrid(
        params = ThemePickerGridParams(label = stringResource(target.titleRes), columns = ColorPageMetrics.COLUMNS)
    ) {
        items(items = tiles, key = { it.key }, contentType = { "group_color_tile" }) { tile ->
            val candidate = groupTileColor(tile, target)
            ColorPageTileChip(
                name = colorPageTileName(tile),
                look = ColorTileLook(background = candidate, strip = candidate?.let { AureoleDS.colors.onSurfaceHigh }),
                isSelected = tile.key == marked,
                onSelect = { onPick(tile.toCardColorRef(target.themeRef)) }
            )
        }
    }
}

/** The saved color of [target]; null is "None". */
internal fun EditContainerState.colorOf(target: GroupColorTarget): ThemeColorRef? = when (target) {
    GroupColorTarget.BACKGROUND -> backgroundColorRef
    GroupColorTarget.ITEM -> itemColorRef
}

/** Saves [ref] as the color of [target]. */
internal fun EditContainerState.setColor(target: GroupColorTarget, ref: ThemeColorRef?) {
    when (target) {
        GroupColorTarget.BACKGROUND -> backgroundColorRef = ref
        GroupColorTarget.ITEM -> itemColorRef = ref
    }
}

/** The color a tile stands for under the current theme; null for "None". */
@Composable
@ReadOnlyComposable
private fun groupTileColor(tile: ColorPageTile, target: GroupColorTarget): Color? = when (tile) {
    ColorPageTile.None -> null
    is ColorPageTile.Entry -> when (val entry = tile.entry) {
        ComponentColorEntry.Theme -> target.themeRef.resolve(AureoleDS.colors)
        is ComponentColorEntry.Current -> entry.ref.resolve(AureoleDS.colors)
        is ComponentColorEntry.Palette -> ComponentColorRole.CARD.colorIn(entry.palette)
    }
}
