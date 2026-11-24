package dev.mnascimentos.aureole.telas.desfazer

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.undo.AureoleUndoSnackbar
import dev.mnascimentos.aureole.composable.undo.model.UndoSnackbarState
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Snackbar de desfazer (4 s) depois de uma remoção. */
@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = AureoleLauncherTheme {
    AureoleUndoSnackbar(
        state = UndoSnackbarState(1, TextResource.fromString("Navegador saiu dos favoritos")),
        isLeftHanded = false,
        onUndo = {},
        onTimeout = {},
    )
}
