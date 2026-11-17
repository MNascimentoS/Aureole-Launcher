package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

/** The background picker inside the Appearance sheet (polimento-ui RF-01, RF-02). */
private val BackgroundSheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.BACKGROUND)

@Composable
private fun BackgroundCase(isLeftHanded: Boolean, isCustomImageSet: Boolean = false) {
    SettingsSheetScreenshot(
        sheet = BackgroundSheet,
        uiState = SettingsUiState(isLeftHandedMode = isLeftHanded, isCustomWallpaperSet = isCustomImageSet)
    )
}

/** Background picker: type cards, swatches, mini-home anchored above and, no buttons, back saves. */
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

/** In landscape (RF-01): the mini-home on the left, on the chosen background, and the picker on the right. */
@PreviewTest
@AureoleLandscapePreview
@Composable
private fun SetBackgroundLandscapeScreenshotTest() = BackgroundCase(isLeftHanded = false)

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun SetBackgroundLeftHandedLandscapeScreenshotTest() = BackgroundCase(isLeftHanded = true)
