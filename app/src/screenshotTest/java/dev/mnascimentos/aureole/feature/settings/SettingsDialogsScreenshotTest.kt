package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.color.model.DefaultPickerColor
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.composable.sheets.SettingsBottomSheet
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.ContainerPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.FactoryResetBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SearchIconPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SetBackgroundBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SettingsButtonPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.UninstallBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import dev.mnascimentos.aureole.feature.settings.screens.ResetGridDialog

@PreviewTest
@AureolePreview
@Composable
private fun ColorPickerDialogScreenshotTest() {
    AureoleLauncherTheme {
        ColorPickerDialog(
            initialColor = 0xFF1B65C0.toInt(),
            onColorSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ContainerPositionDialogScreenshotTest() {
    AureoleLauncherTheme {
        ContainerPositionDialog(
            currentPosition = "Center",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsButtonPositionDialogScreenshotTest() {
    AureoleLauncherTheme {
        SettingsButtonPositionDialog(
            currentPosition = "Right",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SearchIconPositionDialogScreenshotTest() {
    AureoleLauncherTheme {
        SearchIconPositionDialog(
            currentPosition = "Left",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun PickPaletteBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        PickPaletteBottomSheet(
            selectedPaletteName = "Frostbite",
            onPaletteSelected = {},
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun PickFontBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        PickFontBottomSheet(
            selectedFontName = "Istok Web",
            onFontSelected = {},
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        SetBackgroundBottomSheet(
            SetBackgroundParams(
                currentColor = DefaultPickerColor,
                isCustomImageSet = false,
                onSolidColorSelected = {},
                onCustomImageClick = {},
                onDismissRequest = {}
            )
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        SettingsBottomSheet(
            title = "Settings Bottom Sheet",
            onDismissRequest = {}
        ) {
            AureoleText("Sample Content")
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun FactoryResetBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        FactoryResetBottomSheet()
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun UninstallBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        UninstallBottomSheet(context = LocalContext.current, onDismiss = {})
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ResetGridDialogScreenshotTest() {
    AureoleLauncherTheme {
        ResetGridDialog()
    }
}

@Composable
private fun LeftHanded(content: @Composable () -> Unit) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = true)) {
        AureoleLauncherTheme { content() }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ContainerPositionDialogLeftHandedScreenshotTest() {
    LeftHanded { ContainerPositionDialog(currentPosition = "Center", onPositionSelected = {}, onDismiss = {}) }
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsButtonPositionDialogLeftHandedScreenshotTest() {
    LeftHanded { SettingsButtonPositionDialog(currentPosition = "Right", onPositionSelected = {}, onDismiss = {}) }
}

@PreviewTest
@AureolePreview
@Composable
private fun SearchIconPositionDialogLeftHandedScreenshotTest() {
    LeftHanded { SearchIconPositionDialog(currentPosition = "Left", onPositionSelected = {}, onDismiss = {}) }
}
