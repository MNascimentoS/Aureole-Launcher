package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.color.model.DefaultPickerColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.SetBackgroundBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.LockScreenOption
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams

@Composable
private fun LockScreenCase(option: LockScreenOption) {
    AureoleLauncherTheme {
        SetBackgroundBottomSheet(
            SetBackgroundParams(
                currentColor = DefaultPickerColor,
                isCustomImageSet = false,
                onSolidColorSelected = {},
                onCustomImageClick = {},
                onDismissRequest = {},
                lockScreen = option
            )
        )
    }
}

/** Background sheet with "Also use on lock screen" on. */
@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundLockScreenOnScreenshotTest() = LockScreenCase(LockScreenOption(checked = true))

/** The switch off, as it starts. */
@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundLockScreenOffScreenshotTest() = LockScreenCase(LockScreenOption(checked = false))

/** The device does not allow changing the wallpaper: off, disabled and explained. */
@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundLockScreenUnavailableScreenshotTest() =
    LockScreenCase(LockScreenOption(isAvailable = false))
