package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockBackgroundSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = stringResource(R.string.home_clock_card_background))
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
        Switch(
            checked = uiState.isClockBackgroundEnabled,
            onCheckedChange = { actions.onToggleClockBackground() }
        )
    }

    if (uiState.isClockBackgroundEnabled) {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        AureoleText(
            text = stringResource(R.string.home_clock_custom_bg_color),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact)
        ) {
            PRESET_BG_COLORS.forEach { (name, colorValue) ->
                EditClockColorSwatchItem(
                    name = name,
                    colorValue = colorValue,
                    isSelected = uiState.clockBackgroundColor == colorValue,
                    onClick = { actions.onUpdateClockBackgroundColor(colorValue) }
                )
            }
        }
    }
}

@Composable
internal fun EditClockActionButtons(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    onResetGreeting: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {
                actions.onResetClockSettings()
                onResetGreeting()
            }
        ) {
            AureoleText(
                text = stringResource(R.string.home_clock_reset_defaults),
                color = AureoleDS.colors.error
            )
        }

        Button(
            onClick = onDismissRequest,
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleDS.colors.onSurfaceHigh,
                contentColor = AureoleDS.colors.surface
            )
        ) {
            AureoleText(stringResource(R.string.home_container_edit_save), color = AureoleDS.colors.surface)
        }
    }
}

@AureolePreview
@Composable
private fun EditClockBottomSheetPreview() {
    val uiState = MainUiState()
    val actions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = { _, _ -> },
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
        onEnterGridEditMode = {},
        onCancelGridEditMode = {},
        onSaveGridEditMode = {}
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides uiState) {
            EditClockBottomSheet(
                uiState = uiState,
                actions = actions,
                onDismissRequest = {}
            )
        }
    }
}
