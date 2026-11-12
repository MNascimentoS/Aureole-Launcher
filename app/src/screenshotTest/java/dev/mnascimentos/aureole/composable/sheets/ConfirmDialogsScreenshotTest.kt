package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.actions.model.HOLD_CONFIRM_MS
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmDialogParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateAvailableDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateDownloadedDialog
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun DialogCase(isLeftHanded: Boolean, content: @Composable () -> Unit) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme { content() }
    }
}

@Composable
private fun ResetLayoutCase(isLeftHanded: Boolean) = DialogCase(isLeftHanded) {
    AureoleConfirmDialog(
        params = ConfirmDialogParams(
            title = "Reset layout?",
            message = "Items go back to the starting position of the home screen.",
            confirmLabel = "Restore",
            onConfirm = {},
            onDismiss = {}
        )
    )
}

@Composable
private fun ResetLauncherCase(isLeftHanded: Boolean) = DialogCase(isLeftHanded) {
    AureoleConfirmDialog(
        params = ConfirmDialogParams(
            title = "Reset launcher?",
            message = "Erases all your settings and starts over. This cannot be undone.",
            confirmLabel = "Hold to reset",
            onConfirm = {},
            onDismiss = {},
            holdMs = HOLD_CONFIRM_MS
        )
    )
}

@PreviewTest
@AureolePreview
@Composable
private fun ResetLayoutRightHandedScreenshotTest() = ResetLayoutCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ResetLayoutLeftHandedScreenshotTest() = ResetLayoutCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun ResetLauncherRightHandedScreenshotTest() = ResetLauncherCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ResetLauncherLeftHandedScreenshotTest() = ResetLauncherCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun UpdateAvailableRightHandedScreenshotTest() = DialogCase(isLeftHanded = false) {
    UpdateAvailableDialog(onConfirmUpdate = {}, onDismiss = {})
}

@PreviewTest
@AureolePreview
@Composable
private fun UpdateAvailableLeftHandedScreenshotTest() = DialogCase(isLeftHanded = true) {
    UpdateAvailableDialog(onConfirmUpdate = {}, onDismiss = {})
}

@PreviewTest
@AureolePreview
@Composable
private fun UpdateReadyRightHandedScreenshotTest() = DialogCase(isLeftHanded = false) {
    UpdateDownloadedDialog(onConfirmRestart = {}, onDismiss = {})
}

@PreviewTest
@AureolePreview
@Composable
private fun UpdateReadyLeftHandedScreenshotTest() = DialogCase(isLeftHanded = true) {
    UpdateDownloadedDialog(onConfirmRestart = {}, onDismiss = {})
}
