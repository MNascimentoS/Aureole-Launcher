package dev.mnascimentos.aureole.feature.settings

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

@Composable
private fun LicensesScreenCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        SettingsLicensesScreen(
            uiState = SettingsUiState(isLeftHandedMode = isLeftHanded),
            onNavigateBack = {}
        )
    }
}

@Composable
private fun LicenseSheetCase(isLeftHanded: Boolean, license: LicenseType) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            LicenseDetailBottomSheet(entry = OpenSourceEntries.first { it.license == license }, onDismiss = {})
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun LicensesScreenRightHandedScreenshotTest() = LicensesScreenCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun LicensesScreenLeftHandedScreenshotTest() = LicensesScreenCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun LicenseSheetRightHandedScreenshotTest() = LicenseSheetCase(false, LicenseType.APACHE_2_0)

@PreviewTest
@AureolePreview
@Composable
private fun LicenseSheetLeftHandedScreenshotTest() = LicenseSheetCase(true, LicenseType.OFL_1_1)

@PreviewTest
@AureolePreview
@Composable
private fun LicenseSheetTermsScreenshotTest() = LicenseSheetCase(false, LicenseType.PLAY_CORE_TERMS)
