package dev.mnascimentos.aureole.feature.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.MenuIconColumn
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.AlignRight
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Header
import dev.mnascimentos.aureole.core.designsystem.icons.Press
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.PickerDimensions
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val MIN_OFFSET_PERCENT = 0
private const val MAX_OFFSET_PERCENT = 60

/**
 * The Behavior groups: "Gestures and app list" and "Home screen". "Check for updates" lives in Advanced (D-03).
 */
@Composable
fun BehaviorSections(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    modifier: Modifier = Modifier
) {
    val deliberate = rememberDeliberateTap()
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SettingsSectionGroup(label = stringResource(R.string.settings_section_gestures), isFirst = true) {
            AureoleSwitchRow(
                SwitchRowParams(
                    title = stringResource(R.string.settings_behavior_home_opens_all_apps),
                    checked = uiState.homeButtonOpensAllApps,
                    onCheckedChange = { if (deliberate()) actions.onToggleHomeButtonOpensAllApps() },
                    icon = { m, c -> AureoleDS.icons.Press(modifier = m, tint = c) }
                )
            )
            AureoleSwitchRow(
                SwitchRowParams(
                    title = stringResource(R.string.settings_behavior_all_apps_left_side),
                    checked = uiState.isLeftHandedMode,
                    onCheckedChange = { if (deliberate()) actions.onToggleLeftHandedMode() },
                    icon = { m, c -> AureoleDS.icons.AlignRight(modifier = m, tint = c) }
                )
            )
            AureoleSwitchRow(
                SwitchRowParams(
                    title = stringResource(R.string.settings_behavior_disable_alphabet_scrubber),
                    checked = uiState.isAlphabetScrubberDisabled,
                    onCheckedChange = { if (deliberate()) actions.onToggleDisableAlphabetScrubber() },
                    icon = { m, c -> AureoleDS.icons.AlignRight(modifier = m, tint = c) }
                )
            )
        }
        SettingsSectionGroup(label = stringResource(R.string.settings_section_home_screen)) {
            AureoleSwitchRow(
                SwitchRowParams(
                    title = stringResource(R.string.settings_behavior_show_widget_indicator),
                    checked = uiState.showWidgetDots,
                    onCheckedChange = { if (deliberate()) actions.onToggleShowWidgetDots() },
                    icon = { m, c -> AureoleDS.icons.Dots(modifier = m, tint = c) }
                )
            )
            HeaderOffsetRow(
                headerOffsetPercent = uiState.headerOffsetPercent,
                onOffsetChanged = actions.onHeaderOffsetChanged
            )
        }
    }
}

@Composable
private fun HeaderOffsetRow(
    headerOffsetPercent: Int,
    onOffsetChanged: (Int) -> Unit
) {
    LeftHandMirror {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AureoleDS.dimens.menuRowHeight)
                .padding(horizontal = AureoleDS.dimens.xxSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            MenuIconColumn(icon = { m, c -> AureoleDS.icons.Header(modifier = m, tint = c) })
            AureoleText(
                text = stringResource(R.string.settings_behavior_header_offset),
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
            )
            HeaderOffsetWheelPicker(
                value = headerOffsetPercent,
                range = MIN_OFFSET_PERCENT..MAX_OFFSET_PERCENT,
                onValueChange = onOffsetChanged,
                modifier = Modifier.height(PickerDimensions.Height).width(PickerDimensions.Width)
            )
        }
    }
}

@AureolePreview
@Composable
private fun BehaviorSectionsPreview() {
    AureoleLauncherTheme {
        BehaviorSections(uiState = SettingsUiState(), actions = SettingsScreenActions())
    }
}
