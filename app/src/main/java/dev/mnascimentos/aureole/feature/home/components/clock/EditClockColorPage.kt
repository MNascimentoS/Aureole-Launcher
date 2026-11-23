package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.ThemePickerGrid
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerGridParams
import dev.mnascimentos.aureole.composable.sheets.model.colorIn
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ChevronLeft
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.readableOn
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockColorTile
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTileLook
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockMetrics
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
            columns = EditClockMetrics.COLOR_PAGE_COLUMNS
        ),
        modifier = Modifier.padding(horizontal = AureoleDS.dimens.small)
    ) {
        items(items = tiles, key = { it.key }, contentType = { "clock_color_tile" }) { tile ->
            val candidate = tileColor(tile, role)
            ClockColorTileChip(
                name = clockColorTileName(tile),
                look = when (role) {
                    ComponentColorRole.CARD -> ClockTileLook(
                        candidate,
                        textColor.takeIf { tile != ClockColorTile.None }
                    )
                    ComponentColorRole.TEXT -> ClockTileLook(background = cardColor, strip = candidate)
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
private fun tileColor(tile: ClockColorTile, role: ComponentColorRole): Color? = when (tile) {
    ClockColorTile.None -> null
    is ClockColorTile.Entry -> when (val entry = tile.entry) {
        ComponentColorEntry.Theme -> clockDefaultColor(role)
        is ComponentColorEntry.Current -> entry.ref.resolve(AureoleDS.colors)
        is ComponentColorEntry.Palette -> role.colorIn(entry.palette)
    }
}

/** A tile of the color page: [look] gives its background (null: none, the sheet's neutral surface) and its strip. */
@Composable
private fun ClockColorTileChip(name: String, look: ClockTileLook, isSelected: Boolean, onSelect: () -> Unit) {
    val colors = AureoleDS.colors
    val background = look.background ?: colors.surface
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = AureoleDS.dimens.sizeLarge)
            .clip(shape)
            .background(background)
            .border(
                if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal,
                if (isSelected) colors.onSurfaceHigh else colors.outline,
                shape
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onSelect)
            .padding(AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        val strip = look.strip
        if (strip == null) {
            NoneColorDot(modifier = Modifier.size(AureoleDS.dimens.iconMediumSmall))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AureoleDS.dimens.iconMediumSmall)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusMinimal))
                    .background(strip)
            )
        }
        AureoleText(
            text = name,
            color = readableOn(background, colors),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.smallTitle,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        )
    }
}

/** Back arrow of the color page header: back to the Colors tab, keeping the saved color (RF-E5). */
@Composable
internal fun EditClockColorPageBack(onBack: () -> Unit) {
    val description = stringResource(R.string.home_clock_color_back)
    IconButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = description }) {
        AureoleDS.icons.ChevronLeft(
            modifier = Modifier.size(AureoleDS.dimens.iconMedium),
            tint = AureoleDS.colors.onSurfaceHigh
        )
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
