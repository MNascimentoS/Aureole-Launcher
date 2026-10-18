package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleSectionHeaderScreenshotTest() {
    AureoleLauncherTheme {
        AureoleSectionHeader(
            title = "A"
        )
    }
}
