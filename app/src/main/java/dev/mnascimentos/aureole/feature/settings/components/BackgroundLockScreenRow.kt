package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmDialog
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmDialogParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.LockScreenDialog
import dev.mnascimentos.aureole.feature.settings.model.LockScreenOption

/**
 * "Also use on lock screen" (RF-13), under the type cards. [onToggle] receives the state the user asked for; the
 * sheet turns it into a question or a change. Without permission to change the wallpaper the switch is off,
 * disabled, and says why (RF-17).
 */
@Composable
internal fun BackgroundLockScreenRow(
    option: LockScreenOption,
    onToggle: (turnOn: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleSwitchRow(
        params = SwitchRowParams(
            title = stringResource(R.string.settings_lock_screen_title),
            checked = option.checked && option.isAvailable,
            onCheckedChange = onToggle,
            description = if (option.isAvailable) null else stringResource(R.string.settings_lock_screen_unavailable),
            enabled = option.isAvailable
        ),
        modifier = modifier
    )
}

/** The question [dialog] asks, as a low confirm sheet; it must sit beside the background sheet, not inside it. */
@Composable
internal fun LockScreenDialogs(
    dialog: LockScreenDialog?,
    option: LockScreenOption,
    onDismiss: () -> Unit
) {
    when (dialog) {
        LockScreenDialog.FIRST_TIME -> AureoleConfirmDialog(
            params = ConfirmDialogParams(
                title = stringResource(R.string.settings_lock_screen_notice_title),
                message = stringResource(R.string.settings_lock_screen_notice_message),
                confirmLabel = stringResource(R.string.settings_lock_screen_notice_confirm),
                onConfirm = option.onTurnOn,
                onDismiss = onDismiss,
                cancelLabel = stringResource(R.string.settings_lock_screen_notice_cancel)
            )
        )
        LockScreenDialog.TURN_OFF -> AureoleConfirmDialog(
            params = ConfirmDialogParams(
                title = stringResource(R.string.settings_lock_screen_off_title),
                message = stringResource(R.string.settings_lock_screen_off_message),
                confirmLabel = stringResource(R.string.settings_lock_screen_off_keep),
                onConfirm = { option.onTurnOff(false) },
                onDismiss = onDismiss,
                cancelLabel = stringResource(R.string.settings_lock_screen_off_restore),
                onCancel = { option.onTurnOff(true) }
            )
        )
        null -> Unit
    }
}

@AureolePreview
@Composable
private fun BackgroundLockScreenRowPreview() {
    AureoleLauncherTheme {
        BackgroundLockScreenRow(option = LockScreenOption(checked = true), onToggle = {})
    }
}

@AureolePreview
@Composable
private fun BackgroundLockScreenRowUnavailablePreview() {
    AureoleLauncherTheme {
        BackgroundLockScreenRow(option = LockScreenOption(isAvailable = false), onToggle = {})
    }
}
