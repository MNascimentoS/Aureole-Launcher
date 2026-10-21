package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleEditOverlaySheetScreenshotTest() {
    AureoleLauncherTheme {
        OverlaySheetBody(
            title = "Preview Overlay Sheet",
            onSettingsClick = {},
            bottomBarContent = {
                Text(
                    text = "Bottom Bar Content",
                    color = AureoleDS.colors.onSurfaceMedium
                )
            },
            sheetContent = {
                Text(
                    text = "Sheet Body Content",
                    color = AureoleDS.colors.onSurfaceHigh
                )
            }
        )
    }
}
