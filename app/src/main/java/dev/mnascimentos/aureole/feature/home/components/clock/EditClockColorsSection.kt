package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockColorTile
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockMetrics
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorRefFor
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorTitleRes
import dev.mnascimentos.aureole.feature.home.components.clock.model.selectedClockColorTile
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlin.math.sqrt

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
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(clockColorTitleRes(role)),
            subtitle = clockColorTileName(tile),
            icon = { m, _ ->
                if (tile == ClockColorTile.None) NoneColorDot(modifier = m) else ColorDot(color = color, modifier = m)
            },
            showChevron = true,
            onClick = { onClick(role) }
        )
    )
}

/** What the clock draws for [role] when it follows the theme: the text tone, or the theme card. */
@Composable
@ReadOnlyComposable
internal fun clockDefaultColor(role: ComponentColorRole): Color = when (role) {
    ComponentColorRole.TEXT -> AureoleDS.colors.onSurfaceHigh
    ComponentColorRole.CARD -> CLOCK_CARD_THEME_REF.resolve(AureoleDS.colors)
}

/** Name of a clock color: "None", "Theme", the palette that has it, or "Current". */
@Composable
@ReadOnlyComposable
internal fun clockColorTileName(tile: ClockColorTile): String = when (tile) {
    ClockColorTile.None -> stringResource(R.string.home_clock_color_none)
    is ClockColorTile.Entry -> when (val entry = tile.entry) {
        ComponentColorEntry.Theme -> stringResource(R.string.theme_color_swatch_default)
        is ComponentColorEntry.Current -> stringResource(R.string.theme_color_option_current)
        is ComponentColorEntry.Palette -> entry.name
    }
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

/** The "None" card color: a dashed circle crossed out by a diagonal line. */
@Composable
internal fun NoneColorDot(modifier: Modifier = Modifier) {
    val color = AureoleDS.colors.onSurfaceLow
    val strokeWidth = AureoleDS.dimens.borderMinimal
    Box(
        modifier = modifier
            .padding(AureoleDS.dimens.xxSmall)
            .drawBehind {
                val width = strokeWidth.toPx()
                val dash = size.minDimension * EditClockMetrics.NONE_DOT_DASH_FRACTION
                val radius = (size.minDimension - width) / 2f
                drawCircle(
                    color = color,
                    radius = radius,
                    style = Stroke(width = width, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)))
                )
                // Corner to corner of the square inside the circle, so the line ends on the circle.
                val half = radius / sqrt(2f)
                drawLine(
                    color = color,
                    start = center + Offset(half, -half),
                    end = center + Offset(-half, half),
                    strokeWidth = width
                )
            }
    )
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
