package dev.mnascimentos.aureole.telas.confirmacao

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.actions.model.HOLD_CONFIRM_MS
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmDialog
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmDialogParams
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase

/** Confirmação simples: restaurar o layout da home. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = HomeCase(MainUiState()) {
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

/** Confirmação destrutiva: resetar o launcher segurando o botão. */
@PreviewTest
@AureolePreview
@Composable
private fun ResetarScreenshotTest() = HomeCase(MainUiState()) {
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
