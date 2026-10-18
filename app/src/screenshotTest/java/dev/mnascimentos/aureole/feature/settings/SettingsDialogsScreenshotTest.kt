package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.BlurBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.ContainerPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.PickBorderRadiusBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.PickFontBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.PickPaletteBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SearchIconPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SetBackgroundBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SettingsBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SettingsButtonPositionDialog

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
private fun PickBorderRadiusBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        PickBorderRadiusBottomSheet(
            selectedRadiusDp = 16,
            onRadiusSelected = {},
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
            onSolidColorClick = {},
            onCustomImageClick = {},
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun BlurBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        BlurBottomSheet(
            currentOpacity = 0.5f,
            isHazeEnabled = true,
            onOptionSelected = { _, _ -> },
            onDismissRequest = {}
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
