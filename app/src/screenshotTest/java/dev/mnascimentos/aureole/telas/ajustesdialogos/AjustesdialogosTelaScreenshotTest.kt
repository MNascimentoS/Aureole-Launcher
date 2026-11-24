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

/** Confirmação de resetar a grade. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = OrientedCase {
    AureoleLauncherTheme {
        ResetGridDialog()
    }
}

/** Sheet de colaboradores, aberta pela seção Sobre do Avançado. */
@PreviewTest
@AureolePreview
@Composable
private fun SobreScreenshotTest() = SettingsSheetCase(
    sheet = SettingsSheet.Child(SettingsChildKind.CONTRIBUTORS, returnTo = SettingsArea.ADVANCED)
)

/** Sheet de posição do botão de ajustes. */
@PreviewTest
@AureolePreview
@Composable
private fun PosicaoBotaoScreenshotTest() = OrientedCase {
    AureoleLauncherTheme {
        SettingsButtonPositionDialog(currentPosition = "Right", onPositionSelected = {}, onDismiss = {})
    }
}

/** Sheet de posição do ícone de busca. */
@PreviewTest
@AureolePreview
@Composable
private fun PosicaoBuscaScreenshotTest() = OrientedCase {
    AureoleLauncherTheme {
        SearchIconPositionDialog(currentPosition = "Left", onPositionSelected = {}, onDismiss = {})
    }
}
