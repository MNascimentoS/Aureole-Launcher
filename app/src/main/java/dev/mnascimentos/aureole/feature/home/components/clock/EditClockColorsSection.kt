package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.selectedColorEntry
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorRefFor
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorTitleRes
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

/**
 * Colors tab of the clock (RF-F1): "Text color" and "Card color" rows, each with a dot of the current color and
 * its name, opening the palette picker in color mode over the sheet ([onOpenColorPicker]). The card switch stays
 * between them; with the card off its color cannot be picked, so its row gives way to the switch.
 */
@Composable
internal fun EditClockColorsSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onOpenColorPicker: (ComponentColorRole) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        EditClockColorRow(uiState = uiState, role = ComponentColorRole.TEXT, onClick = onOpenColorPicker)
        EditClockCardSwitch(checked = uiState.isClockBackgroundEnabled, onToggle = actions.onToggleClockBackground)
        if (uiState.isClockBackgroundEnabled) {
            EditClockColorRow(uiState = uiState, role = ComponentColorRole.CARD, onClick = onOpenColorPicker)
        }
    }
}

@Composable
private fun EditClockColorRow(uiState: MainUiState, role: ComponentColorRole, onClick: (ComponentColorRole) -> Unit) {
    val selected = clockColorRefFor(uiState, role)
    val color = selected?.resolve(AureoleDS.colors) ?: clockDefaultColor(role)
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(clockColorTitleRes(role)),
            subtitle = clockColorName(selectedColorEntry(selected, role, isSystemInDarkTheme())),
            icon = { m, _ -> ColorDot(color = color, modifier = m) },
            showChevron = true,
            onClick = { onClick(role) }
        )
    )
}

/** What the clock draws for [role] when it follows the theme. */
@Composable
@ReadOnlyComposable
internal fun clockDefaultColor(role: ComponentColorRole): Color = when (role) {
    ComponentColorRole.TEXT -> AureoleDS.colors.onSurfaceHigh
    ComponentColorRole.CARD -> AureoleDS.colors.surfaceVariant
}

/** Name of the clock's color: "Theme", the palette that has it, or "Current". */
@Composable
@ReadOnlyComposable
private fun clockColorName(entry: ComponentColorEntry): String = when (entry) {
    ComponentColorEntry.Theme -> stringResource(R.string.theme_color_swatch_default)
    is ComponentColorEntry.Current -> stringResource(R.string.theme_color_option_current)
    is ComponentColorEntry.Palette -> entry.name
}

@Composable
private fun ColorDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(AureoleDS.dimens.xxSmall)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(color)
                .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline, CircleShape)
        )
    }
}

@Composable
private fun EditClockCardSwitch(checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AureoleText(
            text = stringResource(R.string.home_clock_show_background),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}

@AureolePreview
@Composable
private fun EditClockColorsSectionPreview() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)) {
            EditClockColorsSection(
                uiState = MainUiState(isClockBackgroundEnabled = true, clockTextColor = PREVIEW_OLD_COLOR),
                actions = createDefaultHomeScreenActions()
            )
        }
    }
}

private const val PREVIEW_OLD_COLOR = 0xFF90CAF9.toInt()
