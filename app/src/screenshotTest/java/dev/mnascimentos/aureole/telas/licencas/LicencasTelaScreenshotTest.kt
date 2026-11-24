package dev.mnascimentos.aureole.telas.licencas

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.LicenseDetailBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.LicenseType
import dev.mnascimentos.aureole.feature.settings.model.OpenSourceEntries
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsLicensesScreen

/** Lista de licenças de código aberto. */
@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() {
    AureoleLauncherTheme {
        SettingsLicensesScreen(uiState = SettingsUiState(), onNavigateBack = {})
    }
}

/** Sheet com o texto de uma licença (Apache 2.0). */
@PreviewTest
@AureolePreview
@Composable
private fun DetalheScreenshotTest() {
    ProvideHomeUiState(MainUiState()) {
        AureoleLauncherTheme {
            LicenseDetailBottomSheet(
                entry = OpenSourceEntries.first { it.license == LicenseType.APACHE_2_0 },
                onDismiss = {}
            )
        }
    }
}
