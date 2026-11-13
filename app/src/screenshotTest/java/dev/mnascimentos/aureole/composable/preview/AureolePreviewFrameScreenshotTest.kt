package dev.mnascimentos.aureole.composable.preview

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureolePreviewFrameScreenshotTest() {
    AureoleLauncherTheme {
        AureolePreviewFrame {
            AureoleText(text = "Preview content", color = AureoleDS.colors.onSurfaceHigh)
        }
    }
}
