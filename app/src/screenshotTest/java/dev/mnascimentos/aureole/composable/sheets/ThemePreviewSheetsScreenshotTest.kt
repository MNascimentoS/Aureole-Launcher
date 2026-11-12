package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun PaletteCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
            PickPaletteBottomSheet(
                selectedPaletteName = "Frostbite",
                onPaletteSelected = {},
                onDismissRequest = {}
            )
        }
    }
}

@Composable
private fun FontCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
            PickFontBottomSheet(
                selectedFontName = "Istok Web",
                onFontSelected = {},
                onDismissRequest = {}
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun PickPaletteSheetRightHandedScreenshotTest() = PaletteCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun PickPaletteSheetLeftHandedScreenshotTest() = PaletteCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun PickFontSheetRightHandedScreenshotTest() = FontCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun PickFontSheetLeftHandedScreenshotTest() = FontCase(isLeftHanded = true)
