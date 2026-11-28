/**
 * Paleta e fonte.
 *
 * @en Palette and font
 * @area ajustes
 * @paisagem
 * @grupo Tema / Theme
 */
package dev.mnascimentos.aureole.telas.tema

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.fixtures.OrientedCase
import dev.mnascimentos.aureole.fixtures.SettingsSheetCase

/**
 * Paleta.
 *
 * Seletor de paletas (o que abre do Personalizar e do onboarding).
 *
 * @en Palette
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = OrientedCase {
    AureoleLauncherTheme {
        ProvideHomeUiState(MainUiState()) {
            PickPaletteBottomSheet(selectedPaletteName = "Frostbite", onPaletteSelected = {}, onDismissRequest = {})
        }
    }
}

/**
 * Fonte.
 *
 * Seletor de fontes.
 *
 * @en Font
 */
@PreviewTest
@AureolePreview
@Composable
private fun FonteScreenshotTest() {
    AureoleLauncherTheme {
        ProvideHomeUiState(MainUiState()) {
            PickFontBottomSheet(selectedFontName = "Istok Web", onFontSelected = {}, onDismissRequest = {})
        }
    }
}

/**
 * Prévia.
 *
 * Paleta dentro da sheet Aparência: a mesma prévia da home, a grade no lugar da lista.
 *
 * @en Preview
 */
@PreviewTest
@AureolePreview
@Composable
private fun PreviaScreenshotTest() = SettingsSheetCase(
    sheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.PALETTE)
)
