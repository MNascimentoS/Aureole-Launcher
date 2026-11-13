package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.composable.color.model.DefaultPickerColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.SetBackgroundBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams

@Composable
private fun BackgroundCase(isLeftHanded: Boolean, isCustomImageSet: Boolean = false) {
    AureoleLauncherTheme {
        SetBackgroundBottomSheet(
            SetBackgroundParams(
                currentColor = DefaultPickerColor,
                isCustomImageSet = isCustomImageSet,
                onSolidColorSelected = {},
                onCustomImageClick = {},
                onDismissRequest = {},
                isLeftHanded = isLeftHanded
            )
        )
    }
}

@Composable
private fun ColorPickerCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        ColorPickerDialog(
            initialColor = DefaultPickerColor,
            onColorSelected = {},
            onDismiss = {},
            isLeftHanded = isLeftHanded
        )
    }
}

/** Background sheet: type cards, swatches, mini-home anchored above and Cancel / Apply, Apply under the thumb. */
@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundRightHandedScreenshotTest() = BackgroundCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundLeftHandedScreenshotTest() = BackgroundCase(isLeftHanded = true)

/** Custom image type: no swatches. */
@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundCustomImageScreenshotTest() = BackgroundCase(isLeftHanded = false, isCustomImageSet = true)

/** Color picker: color and hex anchored above, recommended colors and the hue slider, Cancel / Confirm. */
@PreviewTest
@AureolePreview
@Composable
private fun ColorPickerRightHandedScreenshotTest() = ColorPickerCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ColorPickerLeftHandedScreenshotTest() = ColorPickerCase(isLeftHanded = true)
