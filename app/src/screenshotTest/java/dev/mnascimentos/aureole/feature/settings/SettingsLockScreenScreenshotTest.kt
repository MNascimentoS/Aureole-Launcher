package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.LockScreenOption
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

@Composable
private fun LockScreenCase(option: LockScreenOption) {
    SettingsSheetScreenshot(
        sheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.BACKGROUND),
        uiState = SettingsUiState(
            applyToLockScreen = option.checked,
            canSetWallpaper = option.isAvailable,
            isLockScreenNoticeSeen = option.isNoticeSeen
        )
    )
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
