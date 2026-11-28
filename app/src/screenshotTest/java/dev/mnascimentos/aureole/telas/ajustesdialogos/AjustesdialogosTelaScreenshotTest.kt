/**
 * Diálogos dos ajustes.
 *
 * @en Settings dialogs
 * @area ajustes
 * @paisagem
 * @excecao screenshots-telas-fluxos D-20
 */
package dev.mnascimentos.aureole.telas.ajustesdialogos

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.components.SearchIconPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SettingsButtonPositionDialog
import dev.mnascimentos.aureole.feature.settings.screens.ResetGridDialog
import dev.mnascimentos.aureole.fixtures.OrientedCase
import dev.mnascimentos.aureole.fixtures.SettingsSheetCase

/**
 * Resetar.
 *
 * Confirmação de resetar a grade.
 *
 * @en Reset
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = OrientedCase {
    AureoleLauncherTheme {
        ResetGridDialog()
    }
}

/**
 * Sobre.
 *
 * Sheet de colaboradores, aberta pela seção Sobre do Avançado.
 *
 * @en About
 */
@PreviewTest
@AureolePreview
@Composable
private fun SobreScreenshotTest() = SettingsSheetCase(
    sheet = SettingsSheet.Child(SettingsChildKind.CONTRIBUTORS, returnTo = SettingsArea.ADVANCED)
)

/**
 * Posição do botão.
 *
 * Sheet de posição do botão de ajustes.
 *
 * @en Button position
 */
@PreviewTest
@AureolePreview
@Composable
private fun PosicaoBotaoScreenshotTest() = OrientedCase {
    AureoleLauncherTheme {
        SettingsButtonPositionDialog(currentPosition = "Right", onPositionSelected = {}, onDismiss = {})
    }
}

/**
 * Posição da busca.
 *
 * Sheet de posição do ícone de busca.
 *
 * @en Search position
 */
@PreviewTest
@AureolePreview
@Composable
private fun PosicaoBuscaScreenshotTest() = OrientedCase {
    AureoleLauncherTheme {
        SearchIconPositionDialog(currentPosition = "Left", onPositionSelected = {}, onDismiss = {})
    }
}
