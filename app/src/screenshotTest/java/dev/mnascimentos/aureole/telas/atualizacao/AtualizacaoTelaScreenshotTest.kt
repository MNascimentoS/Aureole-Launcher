package dev.mnascimentos.aureole.telas.atualizacao

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateAvailableDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateDownloadedDialog
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase

/** Atualização disponível na loja. */
@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = HomeCase(MainUiState()) {
    UpdateAvailableDialog(onConfirmUpdate = {}, onDismiss = {})
}

/** Atualização baixada, pronta para reiniciar. */
@PreviewTest
@AureolePreview
@Composable
private fun BaixadaScreenshotTest() = HomeCase(MainUiState()) {
    UpdateDownloadedDialog(onConfirmRestart = {}, onDismiss = {})
}
