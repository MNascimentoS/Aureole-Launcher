package dev.mnascimentos.aureole.composable.undo

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.undo.model.UndoSnackbarState
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val LONG_TEXT =
    "Navegador de internet com um nome muito comprido saiu dos favoritos e precisa quebrar a linha"

@Composable
private fun SnackbarCase(text: String, isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        AureoleUndoSnackbar(
            state = UndoSnackbarState(1, TextResource.fromString(text)),
            isLeftHanded = isLeftHanded,
            onUndo = {},
            onTimeout = {},
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun UndoSnackbarRightHandedScreenshotTest() = SnackbarCase("Navegador saiu dos favoritos", false)

@PreviewTest
@AureolePreview
@Composable
private fun UndoSnackbarLeftHandedScreenshotTest() = SnackbarCase("Navegador saiu dos favoritos", true)

@PreviewTest
@AureolePreview
@Composable
private fun UndoSnackbarLongTextScreenshotTest() = SnackbarCase(LONG_TEXT, false)
