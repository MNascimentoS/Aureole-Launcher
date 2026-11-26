package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.composable.colorpage.AureoleColorRow
import dev.mnascimentos.aureole.composable.colorpage.colorPageTileName
import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageTile
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorRefFor
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorTitleRes
import dev.mnascimentos.aureole.feature.home.components.clock.model.selectedClockColorTile
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Colors tab of the clock (RF-E1, RF-E2): the "Text color" and "Card color" rows, always both, each with a dot of
 * the current color and its name. A clock without card shows "None" with a dashed, crossed out dot. A row opens the
 * color page inside the same sheet ([onOpenColorPicker], RF-E4).
 */
@Composable
internal fun EditClockColorsSection(
    uiState: MainUiState,
    onOpenColorPicker: (ComponentColorRole) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        EditClockColorRow(uiState = uiState, role = ComponentColorRole.TEXT, onClick = onOpenColorPicker)
        EditClockColorRow(uiState = uiState, role = ComponentColorRole.CARD, onClick = onOpenColorPicker)
    }
}

@Composable
private fun EditClockColorRow(uiState: MainUiState, role: ComponentColorRole, onClick: (ComponentColorRole) -> Unit) {
    val selected = clockColorRefFor(uiState, role)
    val tile = selectedClockColorTile(selected, role, isSystemInDarkTheme())
    val color = selected?.resolve(AureoleDS.colors) ?: clockDefaultColor(role)
    AureoleColorRow(
        title = stringResource(clockColorTitleRes(role)),
        value = colorPageTileName(tile),
        color = color.takeUnless { tile == ColorPageTile.None },
        onClick = { onClick(role) }
    )
}

/** What the clock draws for [role] when it follows the theme: the text tone, or the theme card. */
@Composable
@ReadOnlyComposable
internal fun clockDefaultColor(role: ComponentColorRole): Color = when (role) {
    ComponentColorRole.TEXT -> AureoleDS.colors.onSurfaceHigh
    ComponentColorRole.CARD -> CLOCK_CARD_THEME_REF.resolve(AureoleDS.colors)
}

@AureolePreview
@Composable
private fun EditClockColorsSectionPreview() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)) {
            EditClockColorsSection(uiState = MainUiState(clockTextColor = PREVIEW_OLD_COLOR))
        }
    }
}

private const val PREVIEW_OLD_COLOR = 0xFF90CAF9.toInt()
