package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Undo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions

/** The reset warning says the action cannot be undone: it has to show whole, never cut by an ellipsis. */
private const val RESET_WARNING_MAX_LINES = 3

@Composable
internal fun FactoryResetBottomSheet(
    actions: SettingsScreenActions = SettingsScreenActions()
) {
    AureoleMenuSheet(
        title = stringResource(R.string.settings_advanced_reset_dialog_title),
        onDismiss = actions.onDismissFactoryResetDialog
    ) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_reset_dialog_action),
                subtitle = stringResource(R.string.settings_advanced_reset_dialog_message),
                icon = { modifier, tint -> AureoleDS.icons.Undo(modifier = modifier, tint = tint) },
                isDestructive = true,
                requiresHold = true,
                subtitleMaxLines = RESET_WARNING_MAX_LINES,
                onClick = actions.onConfirmFactoryReset
            )
        )
    }
}

@AureolePreview
@Composable
private fun FactoryResetBottomSheetPreview() {
    AureoleLauncherTheme {
        FactoryResetBottomSheet()
    }
}
