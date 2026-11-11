package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

class OverlaySheetGestureZoneScreenshotTest {

    @PreviewTest
    @AureolePreview
    @Composable
    private fun OverlaySheetGestureZoneDefault() {
        AureoleLauncherTheme {
            OverlaySheetGestureZone(
                isExpanded = false,
                isLeftHanded = false,
                onToggleExpanded = {},
                onSettingsClick = {}
            )
        }
    }
}
