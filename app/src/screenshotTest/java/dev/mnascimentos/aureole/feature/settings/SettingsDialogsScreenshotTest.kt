package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
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
@Preview(name = "Color Picker Dialog Flow", showBackground = true)
@Composable
fun ColorPickerDialogScreenshotTest() {
    AureoleLauncherTheme {
        ColorPickerDialog(
            initialColor = 0xFF1B65C0.toInt(),
            onColorSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@Preview(name = "Container Position Dialog Flow", showBackground = true)
@Composable
fun ContainerPositionDialogScreenshotTest() {
    AureoleLauncherTheme {
        ContainerPositionDialog(
            currentPosition = "Center",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@Preview(name = "Settings Button Position Dialog Flow", showBackground = true)
@Composable
fun SettingsButtonPositionDialogScreenshotTest() {
    AureoleLauncherTheme {
        SettingsButtonPositionDialog(
            currentPosition = "Right",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@Preview(name = "Search Icon Position Dialog Flow", showBackground = true)
@Composable
fun SearchIconPositionDialogScreenshotTest() {
    AureoleLauncherTheme {
        SearchIconPositionDialog(
            currentPosition = "Left",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@Preview(name = "Pick Palette Bottom Sheet Flow", showBackground = true)
@Composable
fun PickPaletteBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        PickPaletteBottomSheet(
            selectedPaletteName = "Frostbite",
            onPaletteSelected = {},
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@Preview(name = "Pick Font Bottom Sheet Flow", showBackground = true)
@Composable
fun PickFontBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        PickFontBottomSheet(
            selectedFontName = "Istok Web",
            onFontSelected = {},
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@Preview(name = "Pick Border Radius Bottom Sheet Flow", showBackground = true)
@Composable
fun PickBorderRadiusBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        PickBorderRadiusBottomSheet(
            selectedRadiusDp = 16,
            onRadiusSelected = {},
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@Preview(name = "Set Background Bottom Sheet Flow", showBackground = true)
@Composable
fun SetBackgroundBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        SetBackgroundBottomSheet(
            onSolidColorClick = {},
            onCustomImageClick = {},
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@Preview(name = "Blur Bottom Sheet Flow", showBackground = true)
@Composable
fun BlurBottomSheetScreenshotTest() {
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
@Preview(name = "Settings Bottom Sheet Flow", showBackground = true)
@Composable
fun SettingsBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        SettingsBottomSheet(
            title = "Settings Bottom Sheet",
            onDismissRequest = {}
        ) {
            AureoleText("Sample Content")
        }
    }
}

