package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.model.clockColorRefOf
import dev.mnascimentos.aureole.composable.color.AureoleThemeColorPicker
import dev.mnascimentos.aureole.composable.color.model.ThemeDefaultOption
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val TARGET_TEXT = 0
private const val TARGET_CARD = 1

/**
 * Colors tab of the clock: the same theme color picker as the background, folder and quick button sheets, for the
 * "Text | Card" target chosen in the content (RF-D1, D2). "Theme default" is the starting choice (RF-D3). With the
 * card switched off its color cannot be picked, so the picker gives way to the switch (RF-D4).
 */
@Composable
internal fun EditClockColorsSection(uiState: MainUiState, actions: HomeScreenActions) {
    var target by rememberSaveable { mutableIntStateOf(TARGET_TEXT) }
    val targetLabel = stringResource(R.string.home_clock_color_target_label)
    val isCard = target == TARGET_CARD
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)) {
        AureoleSegmentedControl(
            options = listOf(
                stringResource(R.string.home_clock_color_target_text),
                stringResource(R.string.home_clock_color_target_card)
            ),
            selectedIndex = target,
            onSelect = { target = it },
            modifier = Modifier.semantics { contentDescription = targetLabel }
        )
        if (isCard) {
            EditClockCardSwitch(checked = uiState.isClockBackgroundEnabled, onToggle = actions.onToggleClockBackground)
        }
        if (!isCard || uiState.isClockBackgroundEnabled) {
            val selected = if (isCard) {
                clockColorRefOf(uiState.clockBackgroundColor, uiState.clockBackgroundColorRef)
            } else {
                clockColorRefOf(uiState.clockTextColor, uiState.clockTextColorRef)
            }
            val onPick: (ThemeColorRef?) -> Unit =
                if (isCard) actions.onUpdateClockBackgroundColor else actions.onUpdateClockTextColor
            AureoleThemeColorPicker(
                selected = selected,
                onSelect = { onPick(it) },
                defaultOption = ThemeDefaultOption(
                    color = (if (isCard) AureoleDS.colors.surfaceVariant else AureoleDS.colors.onSurfaceHigh).toArgb(),
                    isSelected = selected == null,
                    onSelect = { onPick(null) }
                )
            )
        }
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
