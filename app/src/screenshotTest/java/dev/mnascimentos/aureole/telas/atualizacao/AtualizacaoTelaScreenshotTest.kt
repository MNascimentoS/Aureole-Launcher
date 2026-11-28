/**
 * Atualização.
 *
 * @en Update
 * @area home
 * @paisagem
 */
package dev.mnascimentos.aureole.telas.atualizacao

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateAvailableDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateDownloadedDialog
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase

/**
 * Disponível.
 *
 * Atualização disponível na loja.
 *
 * @en Available
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = HomeCase(MainUiState()) {
    UpdateAvailableDialog(onConfirmUpdate = {}, onDismiss = {})
}

/**
 * Baixada.
 *
 * Atualização baixada, pronta para reiniciar.
 *
 * @en Downloaded
 */
@PreviewTest
@AureolePreview
@Composable
private fun BaixadaScreenshotTest() = HomeCase(MainUiState()) {
    UpdateDownloadedDialog(onConfirmRestart = {}, onDismiss = {})
}
