package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerGridParams
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val GRID_CASE_HEIGHT_MULTIPLIER = 3

@Composable
private fun PaletteCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
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
        ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
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

@Composable
private fun PickerGridCase() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.height(AureoleDS.dimens.sizeExtraLarge * GRID_CASE_HEIGHT_MULTIPLIER)) {
            ThemePickerGrid(
                params = ThemePickerGridParams(
                    label = stringResource(R.string.settings_appearance_palette_section_label),
                    columns = 3
                )
            ) {
                items(items = allPalettes, key = { it.name }) { palette ->
                    PaletteChip(
                        palette = palette,
                        displayName = palette.name,
                        isSelected = palette == allPalettes.first(),
                        onSelect = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipsCase(isLeftHanded: Boolean = false) {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier.padding(AureoleDS.dimens.medium),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            PaletteChip(
                palette = allPalettes.first(),
                displayName = allPalettes.first().name,
                isSelected = true,
                onSelect = {},
                modifier = Modifier.fillMaxWidth()
            )
            FontChip(
                fontName = "Istok Web",
                isSelected = false,
                onSelect = {},
                modifier = Modifier.fillMaxWidth(),
                isLeftHanded = isLeftHanded
            )
            FontChip(
                fontName = "Istok Web",
                isSelected = true,
                onSelect = {},
                modifier = Modifier.fillMaxWidth(),
                isLeftHanded = isLeftHanded
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ThemePickerGridScreenshotTest() = PickerGridCase()

@PreviewTest
@AureolePreview
@Composable
private fun ThemePickerChipsScreenshotTest() = ChipsCase()

@PreviewTest
@AureolePreview
@Composable
private fun ThemePickerChipsLeftHandedScreenshotTest() = ChipsCase(isLeftHanded = true)
