package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.wallpaper.WallpaperEditorOverlay
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorParams
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorTab

/** Adjusted values, so the sliders and the zoom tag do not sit at their defaults. */
private val EDITED = WallpaperAdjustments(zoom = 2f, dim = 0.45f, blurDp = 8f)

@Composable
private fun EditorCase(tab: WallpaperEditorTab, isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        WallpaperEditorOverlay(
            params = WallpaperEditorParams(
                photoPath = null,
                initial = EDITED,
                onFinish = { _, _ -> },
                onDismissRequest = {},
                isLeftHanded = isLeftHanded,
                initialTab = tab
            )
        )
    }
}

/** Editor: ghost home, screen frame, zoom tag and the sheet on the Frame tab (Center / Rotate 90°). */
@PreviewTest
@AureolePreview
@Composable
private fun WallpaperEditorFrameScreenshotTest() = EditorCase(WallpaperEditorTab.FRAME, isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun WallpaperEditorFrameLeftHandedScreenshotTest() = EditorCase(WallpaperEditorTab.FRAME, isLeftHanded = true)

/** Dim slider with its value. */
@PreviewTest
@AureolePreview
@Composable
private fun WallpaperEditorDimScreenshotTest() = EditorCase(WallpaperEditorTab.DIM, isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun WallpaperEditorDimLeftHandedScreenshotTest() = EditorCase(WallpaperEditorTab.DIM, isLeftHanded = true)

/** Blur slider with its value. */
@PreviewTest
@AureolePreview
@Composable
private fun WallpaperEditorBlurScreenshotTest() = EditorCase(WallpaperEditorTab.BLUR, isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun WallpaperEditorBlurLeftHandedScreenshotTest() = EditorCase(WallpaperEditorTab.BLUR, isLeftHanded = true)

@Composable
private fun PhotoCardCase(isLeftHanded: Boolean) {
    SettingsSheetScreenshot(
        sheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.BACKGROUND),
        uiState = SettingsUiState(
            isLeftHandedMode = isLeftHanded,
            isCustomWallpaperSet = true,
            customWallpaperPath = "",
            wallpaperAdjustments = EDITED
        )
    )
}

/** "Custom image" card with the thumbnail and the Edit photo / Change photo buttons; both cards equally tall. */
@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundPhotoCardScreenshotTest() = PhotoCardCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun SetBackgroundPhotoCardLeftHandedScreenshotTest() = PhotoCardCase(isLeftHanded = true)
