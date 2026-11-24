package dev.mnascimentos.aureole.telas.ajustesdialogos

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
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
